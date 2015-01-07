This is a pure object-oriented Java7 web development framework. Its key benefits, comparing to all others, include:

 * all (!) interfaces and classes are immutable
 * not a single static property or a method
 * no reflection or class manipulations
 * XML+XSLT out-of-the-box
 * 100% RESTful
 
## Quick Start

Here it is:

```java
public final class App {
  public static void main(final String... args) {
    new Server(
      new TksRegex().with("/", "hello, world!")
    ).listen();
  }
}
```

Compile and run it. Should work :)

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new Server(
      new TksRegex()
        .with("/robots.txt", "")
        .with("/", new TkIndex())
        .with(
          "/xsl/.*", 
          new TkContentType(new TkClasspath(), "text/xsl")
        )
        .with(
          "/account", 
          new Page.Source() {
            @Override
            public Page page(final Request request) {
              return new TkAccount(users, request);
            }
          }
        )
        .with(
          "/balance/(?<user>[a-z]+)", 
          new TksRegex.Source() {
            @Override
            public Page page(final RqRegex request) {
              return new TkBalance(request);
            }
          }
        )
    ).listen();
  }
}
```

Here is a simple page:

```java
@Immutable
public final class TkIndex implements Take {
  @Override
  public Response print() {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

Let's create a more complex page:

```java
@Immutable
public final class TkAccount implements Take {
  private final User user;
  public TkAccount(final Users users, final Request request) {
    this.user = users.find(new RqCookies(request).get("user"));
  }
  @Override
  public Response print() {
    return new RsLogin(
      new RsXSLT(
        new RsXembly(
          new XePlain("PI 'xsl-stylesheet', 'href=\"/xsl/account.xsl\"'"),
          new XePlain("ADD 'page'"),
          new XePrepend("XPATH '/page'", this.user)
        )
      ),
      this.user
    );
  }
}
```

Now let's see how that `User` class should look like:

```java
@Immutable
public final class User implements XeSource {
  private final String name;
  private final int balance;
  @Override
  public Iterable<Directive> toXembly() {
    return new Directives().add("user")
      .add("name").set(this.name).up()
      .add("balance").set(Integer.toString(this.balance));
  }
  @Override
  public String toString() {
    return this.name;
  }
}
```

Here is how `RsLogin` may look like:

```java
@Immutable
public final class RsLogin extends Response.Wrap {
  public RsLogin(final Response response, final User user) {
    super(
      new RsCookied(response).with(
        "user", user.toString()
      )
    );
  }
}
```

## Key Interfaces

Here is how key interfaces look like. First, the `Request`:

```java
@Immutable
public interface Request {
  String method();
  String uri();
  Headers headers();
  InputStream body();
}
```

And the `Response`:

```java
@Immutable
public interface Response {
  int status();
  String line();
  Headers headers();
  InputStream body();
}
```

Also, the `Headers` is a multi-key map:

```java
@Immutable
public interface Headers {
  List<String> get(String key);
  Collection<String> keys();
}
```

## RsJSON

Here is how we can deal with JSON:

```java
@Immutable
public final class TkBalance extends Take.Fixed {
  public TkBalance(final RqRegex request) {
    super(new RsJSON(request.matcher().group("user"))));
  }
}
```

This is the method to add to `User`:

```java
@Immutable
public final class User implements XeSource, RsJSON.Source {
  @Override
  public JsonObject toJSON() {
    return Json.createObjectBuilder()
      .add("balance", this.balance)
      .build();
  }
}
```

## RsXembly

Here is how you generate an XML page using Xembly:

```java
Response response = new RsXembly(
  new XePlain("ADD 'page'"),
  new XePrepend("XPATH '/page'", this.user)
)
```

This is a complete example, with all possible options:

```java
Response response = new RsXembly(
  new XeRoot("page"),
  new XeMillis(false),
  new XeStylesheet("/xsl/account.xsl"),
  new XeToRoot(this.user),
  new XeSource() {
    @Override
    public Iterable<Directive> toXembly() {
      return new Directives().add("status").set("alive");
    }
  },
  new XeMillis(true),
)
```

This is the output that will be produced:

```xml
<?xml version='1.0'?>
<?xsl-stylesheet href='/xsl/account.xsl'?>
<page millis='5664'>
  <user>
    <name>Jeff Lebowski</name>
    <balance>123</balance>
  </user>
  <status>alive</status>
</page>
```

To avoid duplication of all this scaffolding in every page, you can create your own class, which will be used in every page, for example:

```java
Response response = new RsXembly(
  new XeFoo(this.user)
)
```

This is how this `XeFoo` class would look like:

```java
@Immutable
public final class XeFoo implements XeSource.Wrap {
  public XeFoo(final String stylesheet, final XeSource... sources) {
    super(
      XeConcat(
        new XeRoot("page"),
        new XeMillis(false),
        new XeStylesheet(stylesheet),
        new XeToRoot(
          new XeConcat(sources)
        ),
        new XeSource() {
          @Override
          public Iterable<Directive> toXembly() {
            return new Directives().add("status").set("alive");
          }
        },
        new XeMillis(true)
      )
    );
  }
}
```
