# Takes

**It is still an early DRAFT. If you have ideas or corrections, please submit them [here](https://github.com/yegor256/takes/issues).**

Takes is a [pure object-oriented](http://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html) and [immutable](http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html) Java7 web development framework. Its key benefits, comparing to all others, include:

 * not a single mutable class!
 * not a single `public` `static` method!
 * not a single `instanceof` keyword!
 * no configuration files

Besides that, these are more traditional features, out of the box:

 * hit-refresh debugging
 * [XML+XSLT](http://www.yegor256.com/2014/06/25/xml-and-xslt-in-browser.html)
 * [JSON](http://en.wikipedia.org/wiki/JSON)
 * [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer)
 * [WebSockets](http://en.wikipedia.org/wiki/WebSocket)
 * Templates, incl. [Apache Velocity](http://velocity.apache.org/)

## Quick Start

Here it is:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksRegex().with("/", "hello, world!")
    ).listen(args);
  }
}
```

Compile and run it like this (`takes.jar` is the only dependency you need):

```bash
$ java -cp takes.jar App.class --port=1234
```

Should work :) This code starts a new HTTP server on port 1234 and renders a plain-text page on all requests at the root URI.

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksRegex()
        .with("/robots\\.txt", "")
        .with("/", new TkIndex())
    ).listen(args);
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
).listen(args);
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
    ).listen(args);
  }
}
```

## Templates

Now let's see how we can render something more complex than an plain text. First, XML+XSLT is a recommended mechanism of HTML rendering. Even though it may too complex, give it a try, you won't regret. Here is how we render a simple XML page that is transformed to HTML5 on-fly (more about `RsXembly` read below):

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

This is how that `User` class may look like:

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

Let's say, you want to use [Velocity](http://velocity.apache.org/):

```java
@Immutable
public final class TkHelloWorld implements Take {
  @Override
  public Response print() {
    return new RsVelocity(
      "<html>Hello, ${user.name}! Balance: ${user.balance}</html>",
      new ArrayMap<String, Object>().with("user", new User())
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
).listen(args);
```

Class `TkClasspath` takes static part of the request URI and finds a resource with this name in classpath.

`TkFiles` just looks by file name in the directory configured.

`TkContentType` sets content type of all responses coming out of the decorated take.

## Request Methods (POST, PUT, HEAD, etc.)

Here is an example:

```java
new TksRegex()
  .with(
    "/user",
    new TksMethods()
      .with("GET", new TkGetUser())
      .with("POST", new TkPostUser())
      .with("DELETE", new TkDeleteUser())
  )
```

## Form Processing

Here is an example:

```java
@Immutable
public final class TkSavePhoto implements Take {
  private final RqForm request;
  public TkSavePhoto(final Request req) {
    this.request = new RqForm(req);
  }
  @Override
  public Response print() {
    final String name = this.request.param("name");
    final File file = this.request.file("image");
    return new Response.NO_CONTENT;
  }
}
```

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
    ).listen(args);
  }
}
```

`TksFallback` decorates an instance of `Takes` and catches all exceptions any of its takes may throw. Once it's thrown, an instance of `TkHTML` will be returned.

## Redirects

Sometimes it's very useful to return a redirect response (`30x` status code), either by a normal `return` or by throwing an exception. This example illustrates both methods:

```java
@Immutable
public final class TkPostMessage implements Take {
  private final Request request;
  public TkPostMessage(final Request req) {
    this.request = req;
  }
  @Override
  public Response print() {
    final String body = new RqPost(this.request).text();
    if (body.isEmpty()) {
      throw new TksFailFast.Incident(
        new RsRedirect(HttpURLConnection.HTTP_SEE_OTHER)
          .location("/")
          .header("X-Foo-Flash", "message can't be empty")
      );
    }
    // save the message to the database
    return new RsRedirect(HttpURLConnection.HTTP_SEE_OTHER)
      .location("/")
      .header("X-Foo-Flash", "thanks, the message was posted");
  }
}
```

Then, you should decorate the entire `TksRegex` with this `TksFailFast`:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new TksFailFast(
        new TksRegex().with("/", new TkPostMessage())
      )
    ).listen(args);
  }
}
```

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

Here is how you generate an XML page using [Xembly](http://www.xembly.org):

```java
Response response = new RsXembly(
  new XePlain("ADD 'page'"),
  new XePrepend("XPATH '/page'", this.user)
)
```

This is a complete example, with all possible options:

```java
Response response = new RsXembly(
  new XeStylesheet("/xsl/account.xsl"), // add processing instruction
  new XeRoot("take"), // create a DOM document with "take" root element
  new XeMillis(false), // add "millis" attribute to the root, with current time
  new XeToRoot(this.user), // add this.user to the root element
  new XeSource() {
    @Override
    public Iterable<Directive> toXembly() {
      return new Directives().add("status").set("alive");
    }
  },
  new XeMillis(true), // replace "millis" attribute with take building time
)
```

This is the output that will be produced:

```xml
<?xml version='1.0'?>
<?xsl-stylesheet href='/xsl/account.xsl'?>
<take millis='5664'>
  <user>
    <name>Jeff Lebowski</name>
    <balance>123</balance>
  </user>
  <status>alive</status>
</take>
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

## OAuth Login

Here is an example of login via [Facebook](https://developers.facebook.com/docs/reference/dialogs/oauth/):

```java
public final class App {
  public static void main(final String... args) {
    final Auth auth = new AuFacebook();
    new TakesServer(
      new TksAuth(
        new TksRegex()
          .with("/", new TkHTML("hello, check <a href='/acc'>account</a>"))
          .with("/acc", new TkAuthOnly(new TkAccount()))
          .with("/facebook", new TkFacebook("key", "secret")),
        "some-secret-word",
        "/facebook"
      )
    ).listen(args);
  }
}
```

Similar mechanism can be used for `TkGithub`, `TkGoogle`, `TkLinkedin`, `TkTwitter`, etc.

This is how you get currently logged in user:

```java
@Immutable
public final class TkAccount implements Take {
  private final RqAuth request;
  public TkAccount(final Request req) {
    this.request = new RqAuth(req);
  }
  @Override
  public Response print() {
    if (this.request.authenticated()) {
      // returns "urn:facebook:1234567" for a user logged in via Facebook
      this.request.identity();
    }
  }
}
```

## Command Line Arguments

There are a few command line arguments that should be passed to `TakesServer#listen()` method:

```
--port=1234     Tells the server to listen to TCP port 1234
--lifetime=5s   The server will die in five seconds (useful for integration testing)
```

## Logging

The framework sends all logs to SLF4J logging facility. If you want to see them, configure one of [SLF4J bindings](http://www.slf4j.org/manual.html).


## WebSockets

**this section is not really correct, thinking about it...**

Here is how WebSockets work:

```java
public final class App {
  public static void main(final String... args) {
    new TakesServer(
      new Takes.Single( // a simple alternative to TksRegex
        new Take.Source() {
          @Override
          public Take take(final Request request) {
            return new TkMyChat(request);
          }
        }
      )
    ).listen(args);
  }
}
```

WebSocket supporting take is not really different from any other one. The only difference is that it doesn't rush to close the input stream:

```java
@Immutable
public final class TkMyChat implements Take {
  private final Request request;
  public TkMyChat(final Request req) {
    this.request = req;
  }
  @Override
  public Response print() {
    return new Response() {
      @Override
      public InputStream body() {
        // Return a stream that doesn't close immediately,
        // but keeps showing some data until the "chat"
        // is finished. Also, read the data from this.request.body()
      }
    };
  }
}
```
