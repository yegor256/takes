# Takes

Takes is a pure object-oriented and immutable Java7 web development framework. Its key benefits, comparing to all others, include:

 * all (!) interfaces and classes are immutable
 * not a single static property or a method
 * not a single reflection or class manipulation
 * XML+XSLT and JSON out-of-the-box
 * 100% RESTful
 
## Quick Start

Here it is:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksRegex().with("/", "hello, world!")
    ).listen();
  }
}
```

Compile and run it. Should work :) This code starts a new HTTP server on port 80 and renders a plain-text page on all requests at the root URI.

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksRegex()
        .with("/robots\\.txt", "")
        .with("/", new TkIndex())
    ).listen();
  }
}
```

The `TakesServer` is accepting new incoming sockets on port 80, parses them according to HTTP 1.1 specification and creates instances of class `Request`. Then, it gives requests to the instance of `TksRegex` (`tks` stands for "takes") and expects it to return an instance of `Take` back. As you probably understood already, the first regular expression that matches returns a take. `TkIndex` is our custom class, let's see how it looks:

```java
@Immutable
public final class TkIndex implements Take {
  @Override
  public Response print() {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

It is immutable and must implement a single method `print()`, which is returning an instance of `Response`. So far so good, but this class doesn't have an access to an HTTP request. Here is how we solve this:

```java
new TakesServer(
  new TksRegex().with(
    "/file/(?<path>[^/]+)", 
    new TksRegex.Source() {
      @Override
      public Take take(final RqRegex request) {
        final File file = new File(request.matcher().group("path"));
        return new TkHTML(
          FileUtils.readFileToString(file, Charsets.UTF_8)
        );
      }
    }
  )
).listen();
```

Instead of giving an instance of `Take` to the `TksRegex`, we're giving it an instance of `TksRegex.Source`, which is capable of building takes on demand, providing all necessary arguments to their constructors.

Here is a more complex and verbose example:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksRegex()
        .with("/robots.txt", "")
        .with("/", new TkIndex())
        .with(
          "/xsl/.*", 
          new TkContentType(new TkClasspath(), "text/xsl")
        )
        .with(
          "/account", 
          new Take.Source() {
            @Override
            public Take take(final Request request) {
              return new TkAccount(users, request);
            }
          }
        )
        .with(
          "/balance/(?<user>[a-z]+)", 
          new TksRegex.Source() {
            @Override
            public Take take(final RqRegex request) {
              return new TkBalance(request);
            }
          }
        )
    ).listen();
  }
}
```

Now let's create a more complex take:

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

Let's see how that `User` class should look like:

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

## Static Resources

Very often you need to serve static resources to your web users, like CSS stylesheets, images, JavaScript files, etc. There are a few supplementary classes for that:

```java
new Server(
  new TksRegex()
    .with("/css/.+", new TkContentType(new TkClasspath(), "text/css"))
    .with("/data/.+", new TkFiles(new File("/usr/local/data"))
).listen();
```

Class `TkClasspath` takes static part of the request URI and finds a resource with this name in classpath.

`TkFiles` just looks by file name in the directory configured.

`TkContentType` sets content type of all responses coming out of the decorated take.

## Exception Handling

By default, `TksRegex` lets all exceptions bubble up. If one of your takes crashes, a user will see a default error page. Here is how you can configure this behavior:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksFallback(
        new TksRegex()
          .with("/robots\\.txt", "")
          .with("/", new TkIndex()),
        new TkHTML("oops, something went wrong!")
      )
    ).listen();
  }
}
```

`TksFallback` decorates an instance of `Takes` and catches all exceptions any of its takes may throw. Once it's thrown, an instance of `TkHTML` will be returned.

## RsJSON

Here is how we can deal with JSON:

```java
@Immutable
public final class TkBalance extends Take.Fixed {
  public TkBalance(final RqRegex request) {
    super(
      new RsJSON(
        new User(request.matcher().group("user")))
      )
    );
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
