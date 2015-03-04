<img src="http://www.takes.org/clapper.jpg" width="96px" height="96px"/>

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/takes)](http://www.rultor.com/p/yegor256/takes)

[![Build Status](https://img.shields.io/travis/yegor256/takes.svg)](https://travis-ci.org/yegor256/takes)
[![Build status](https://img.shields.io/appveyor/ci/yegor256/takes.svg)](https://ci.appveyor.com/project/yegor256/takes/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/org.takes/takes.svg)](https://maven-badges.herokuapp.com/maven-central/org.takes/takes)

**It is still an early DRAFT. If you have ideas or corrections, please submit them [here](https://github.com/yegor256/takes/issues).**

Takes is a [pure object-oriented](http://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html)
and [immutable](http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html)
Java6 web development framework. Its key benefits, comparing to all others, include:

 * not a single mutable class!
 * not a single `public` `static` method!
 * not a single `instanceof` keyword or type casting!
 * not a single `null`
 * no configuration files

Besides that, these are more traditional features, out of the box:

 * hit-refresh debugging
 * [XML+XSLT](http://www.yegor256.com/2014/06/25/xml-and-xslt-in-browser.html)
 * [JSON](http://en.wikipedia.org/wiki/JSON)
 * [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer)
 * Templates, incl. [Apache Velocity](http://velocity.apache.org/)

This is what is not supported and won't be supported:

 * [WebSockets](http://en.wikipedia.org/wiki/WebSocket)

## Quick Start

Create this `App.java` file:

```java
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.ts.TsRegex;
public final class App {
  public static void main(final String... args) throws Exception {
    new FtBasic(
      new TsRegex().with("/", "hello, world!"), 8080
    ).start(Exit.NEVER);
  }
}
```

Then, download [`takes.jar`](http://search.maven.org/#artifactdetails|org.takes|takes) and compile your Java code:

```
$ javac -cp takes.jar App.java
```

Now, run it like this:

```bash
$ java -Dfile.encoding=UTF-8 -cp takes.jar App
```

Should work :)

This code starts a new HTTP server on port 8080 and renders a plain-text page on
all requests at the root URI.

**Important**: Pay attention that UTF-8 encoding is set on the command line.
The entire framework relies on your default Java encoding, which is not
necessarily UTF-8 by default. To be sure, always set it on the command line
with `file.encoding` Java argument.

Maven artifact is in Maven Central:

```xml
<dependency>
  <groupId>org.takes</groupId>
  <artifactId>takes</artifactId>
</dependency>
```

If you're using Maven, this is how your `pom.xml` should look like:

```xml
<project>
  <dependencies>
    <dependency>
      <groupId>org.takes</groupId>
      <artifactId>takes</artifactId>
      <version>0.3.1</version>
    </dependency>
  </dependencies>
  <profiles>
    <profile>
      <id>hit-refresh</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.3</version>
            <executions>
              <execution>
                <id>start-server</id>
                <phase>pre-integration-test</phase>
                <goals>
                  <goal>java</goal>
                </goals>
                <configuration>
                  <mainClass>foo.App</mainClass> <!-- your main class -->
                  <cleanupDaemonThreads>false</cleanupDaemonThreads>
                  <arguments>
                    <argument>--port=${port}</argument>
                  </arguments>
                </configuration>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
</project>
```

With this configutation you can run it from command line:

```
$ mvn clean integration-test -Phit-refresh -Dport=8080
```

Maven will start the server and you can see it at `http://localhost:8080`.

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TsRegex()
        .with("/robots\\.txt", "")
        .with("/", new TkIndex()),
      8080
    ).start(Exit.NEVER);
  }
}
```

The `FtBasic` is accepting new incoming sockets on port 8080,
parses them according to HTTP 1.1 specification and creates instances
of class `Request`. Then, it gives requests to the instance of `TsRegex`
(`ts` stands for "takes") and expects it to return an instance of `Take` back.
As you probably understood already, the first regular expression that matches
returns a take. `TkIndex` is our custom class (`tk` stands for "take"),
let's see how it looks:

```java
public final class TkIndex implements Take {
  @Override
  public Response act() {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

It is immutable and must implement a single method `act()`, which is returning
an instance of `Response`. So far so good, but this class doesn't have an access
to an HTTP request. Here is how we solve this:

```java
new TsRegex().with(
  "/file/(?<path>[^/]+)",
  new TsRegex.Fast() {
    @Override
    public Take route(final RqRegex request) {
      final File file = new File(
        request.matcher().group("path")
      );
      return new TkHTML(
        FileUtils.readFileToString(file, Charsets.UTF_8)
      );
    }
  }
)
```

We're using `TsRegex.Fast` instead of `Takes`, in order to deal with
`RqRegex` instead of a more generic `Request`. `RqRegex` gives an instance
of `Matcher` used by `TsRegex` for pattern matching.

Here is a more complex and verbose example:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TsRegex()
        .with("/robots.txt", "")
        .with("/", new TkIndex())
        .with(
          "/xsl/.*",
          new TsWithType(new TsClasspath(), "text/xsl")
        )
        .with(
          "/account",
          new Takes() {
            @Override
            public Take route(final Request request) {
              return new TkAccount(users, request);
            }
          }
        )
        .with(
          "/balance/(?<user>[a-z]+)",
          new TsRegex.Fast() {
            @Override
            public Take route(final RqRegex request) {
              return new TkBalance(request.matcher().group("user"));
            }
          }
        )
    ).start(Exit.NEVER);
  }
}
```

## Templates

Now let's see how we can render something more complex than an plain text.
First, XML+XSLT is a recommended mechanism of HTML rendering. Even though it may
too complex, give it a try, you won't regret. Here is how we render a simple XML
page that is transformed to HTML5 on-fly (more about `RsXembly` read below):

```java
public final class TkAccount implements Take {
  private final User user;
  public TkAccount(final Users users, final Request request) {
    this.user = users.find(new RqCookies(request).get("user"));
  }
  @Override
  public Response act() {
    return new RsLogin(
      new RsXSLT(
        new RsXembly(
          new XeStylesheet("/xsl/account.xsl"),
          new XeAppend("page", this.user)
        )
      ),
      this.user
    );
  }
}
```

This is how that `User` class may look like:

```java
public final class User implements XeSource {
  private final String name;
  private final int balance;
  @Override
  public Iterable<Directive> toXembly() {
    return new Directives().add("user")
      .add("name").set(this.name).up()
      .add("balance").set(Integer.toString(this.balance));
  }
}
```

Here is how `RsLogin` may look like:

```java
public final class RsLogin extends RsWrap {
  public RsLogin(final Response response, final User user) {
    super(
      new RsWithCookie(
        response, "user", user.toString()
      )
    );
  }
}
```

## Velocity Templates

Let's say, you want to use [Velocity](http://velocity.apache.org/):

```java
public final class TkHelloWorld implements Take {
  @Override
  public Response act() {
    return new RsVelocity("hi, ${user.name}! You've got ${user.balance}")
      .with("user", new User());
  }
}
```

You will need this extra dependency in classpath:

```xml
<dependency>
  <groupId>org.apache.velocity</groupId>
  <artifactId>velocity-engine-core</artifactId>
  <scope>runtime</scope>
</dependency>
```

## Static Resources

Very often you need to serve static resources to your web users, like CSS
stylesheets, images, JavaScript files, etc. There are a few supplementary
classes for that:

```java
new TsRegex()
  .with("/css/.+", new TsWithType(new TsClasspath(), "text/css"))
  .with("/data/.+", new TsFiles(new File("/usr/local/data"))
```

Class `TsClasspath` takes static part of the request URI and finds a resource with this name in classpath.

`TsFiles` just looks by file name in the directory configured.

`TsWithType` sets content type of all responses coming out of the decorated takes.

## Hit Refresh Debugging

It is a very convenient feature. Once you start the app you want to be able to
modify its static resources (CSS, JS, XSL, etc), refresh the page in a browser
and immediately see the result. You don't want to re-compile the entire project
and restart it. Here is what you need to do to your sources in order to enable
that feature:

```java
new TsRegex()
  .with(
    "/css/.+",
    new TsWithType(
      new TsHitRefresh(
        "./target/classes/foo", // where to get fresh files
        "./src/main/resources/foo/scss/**", // what sources to watch
        "mvn sass:compile", // what to run when sources are modified
        new TsClasspath()
      ),
      "text/css"
    )
  )
```

This `TsHitRefresh` takes is a decorator of another takes. Once it sees
`X-Takes-Refresh` header in the request, it realizes that the server is running in
"hit-refresh" mode and doesn't pass the request to the encapsulated takes. Instead, it
tries to understand whether any of the resources are older than compiled files.
If they are older, it tries to run compilation tool to build them again.

## Request Methods (POST, PUT, HEAD, etc.)

Here is an example:

```java
new TsRegex()
  .with(
    "/user",
    new TsMethods()
      .with("GET", new TkGetUser())
      .with("POST", new TkPostUser())
      .with("DELETE", new TkDeleteUser())
  )
```

## Request Parsing

Here is how you can parse an instance of `Request`:

```java
RqQuery req = new RqQuery(request);
URI uri = req.query();
String value = req.param("key", "default");
```

For a more complex parsing try to use Apache Http Client or something
similar.

## Form Processing

Here is an example:

```java
public final class TkSavePhoto implements Take {
  private final RqForm request;
  public TkSavePhoto(final Request req) {
    this.request = new RqForm(req);
  }
  @Override
  public Response act() {
    final String name = this.request.param("name");
    final File file = this.request.file("image");
    return new RsWithStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}
```

## Exception Handling

By default, `TsRegex` lets all exceptions bubble up. If one of your takes
crashes, a user will see a default error page. Here is how you can configure
this behavior:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TsFallback(
        new TsRegex()
          .with("/robots\\.txt", "")
          .with("/", new TkIndex()),
        new TkHTML("oops, something went wrong!")
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

`TsFallback decorates an instance of Takes and catches all exceptions any of
`its takes may throw. Once it's thrown, an instance of TkHTML will be returned.

## Redirects

Sometimes it's very useful to return a redirect response (`30x` status code),
either by a normal `return` or by throwing an exception. This example
illustrates both methods:

```java
public final class TkPostMessage implements Take {
  private final Request request;
  public TkPostMessage(final Request req) {
    this.request = req;
  }
  @Override
  public Response act() {
    final String body = new RqPost(this.request).text();
    if (body.isEmpty()) {
      throw new RsWithHeader(
        new RsForward(
          HttpURLConnection.HTTP_SEE_OTHER, "/"
        ),
        "X-Foo-Flash",
        "message can't be empty"
      );
    }
    // save the message to the database
    return new RsWithHeader(
      new RsForward(
        HttpURLConnection.HTTP_SEE_OTHER,
      ),
      "X-Foo-Flash",
      "thanks, the message was posted"
    );
  }
}
```

Then, you should decorate the entire `TsRegex` with this `TsForward`:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TsForward(
        new TsRegex().with("/", new TkPostMessage())
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

## RsJSON

Here is how we can deal with JSON:

```java
public final class TkBalance extends TkFixed {
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

Here is how you generate an XML page using [Xembly](http://www.takes.org):

```java
Response response = new RsXembly(
  new XeAppend("page"),
  new XeDirectives("XPATH '/page'", this.user)
)
```

This is a complete example, with all possible options:

```java
Response response = new RsXembly(
  new XeStylesheet("/xsl/account.xsl"), // add processing instruction
  new XeAppend(
    "page", // create a DOM document with "page" root element
    new XeMillis(false), // add "millis" attribute to the root, with current time
    this.user, // add this.user to the root element
    new XeSource() {
      @Override
      public Iterable<Directive> toXembly() {
        return new Directives().add("status").set("alive");
      }
    },
    new XeMillis(true), // replace "millis" attribute with take building time
  ),
)
```

This is the output that will be produced:

```xml
<?xml version='1.0'?>
<?xsl-stylesheet href='/xsl/account.xsl'?>
<page>
  <millis>5648</millis>
  <user>
    <name>Jeff Lebowski</name>
    <balance>123</balance>
  </user>
  <status>alive</status>
</page>
```

To avoid duplication of all this scaffolding in every page, you can create your
own class, which will be used in every page, for example:

```java
Response response = new RsXembly(
  new XeFoo(this.user)
)
```

This is how this `XeFoo` class would look like:

```java
public final class XeFoo implements XeSource.Wrap {
  public XeFoo(final String stylesheet, final XeSource... sources) {
    super(
      new XeChain(
        new XeRoot("page"),
        new XeMillis(false),
        new XeStylesheet(stylesheet),
        new XeChain(sources),
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

You will need this extra dependency in classpath:

```xml
<dependency>
  <groupId>com.jcabi.incubator</groupId>
  <artifactId>xembly</artifactId>
</dependency>
```

## Cookies

Here is how we drop a cookie to the user:

```java
public final class TkIndex implements Take {
  @Override
  public Response act() {
    return new RsWithCookie("auth", "John Doe");
  }
}
```

An HTTP response will contain this header, which will place
a `auth` cookie into the user's browser:

```
HTTP/1.1 200 OK
Set-Cookie: auth="John Doe"
```

This is how you read cookies from a request:

```java
public final class TsIndex implements Takes {
  @Override
  public Take route(final Request req) {
    // the list may be empty
    final List<String> cookies = new RqCookies(req).cookie("my-cookie");
  }
}
```

## Content Negotiation

Say, you want to return different content based on `Accept` header
of the request (a.k.a. [content negotation](http://en.wikipedia.org/wiki/Content_negotiation)):

```java
public final class TkIndex implements Take {
  @Override
  public Response act() {
    return new RsNegotiation(this.request)
      .with("text/*", new RsText("it's a text"))
      .with("application/json", new RsJSON("{\"a\":1}"))
      .with("image/png", /* something else */);
  }
}
```

## Authentication

Here is an example of login via [Facebook](https://developers.facebook.com/docs/reference/dialogs/oauth/):

```java
new TsAuth(
  new TsRegex()
    .with("/", new TkHTML("hello, check <a href='/acc'>account</a>"))
    .with("/acc", new TsSecure(new TsAccount()))
    .with("/facebook", new TkFacebook("key", "secret")),
  new PsCookie("some-secret-word")
)
```

Similar mechanism can be used for `TkGithub`, `TkGoogle`, `TkLinkedin`, `TkTwitter`, etc.

This is how you get currently logged in user:

```java
public final class TkAccount implements Take {
  private final RqAuth request;
  public TkAccount(final Request req) {
    this.request = new RqAuth(req);
  }
  @Override
  public Response act() {
    if (this.request.authenticated()) {
      // returns "urn:facebook:1234567" for a user logged in via Facebook
      this.request.identity();
    }
  }
}
```

## Command Line Arguments

There is a convenient class `FtCLI` that parses command line arguments and
starts the necessary `Front` accordingly.

There are a few command line arguments that should be passed to
`FtCLI` constructor:

```
--port=1234     Tells the server to listen to TCP port 1234
--lifetime=5000 The server will die in five seconds (useful for integration testing)
--refresh       Run the server in hit-refresh mode
--daemon        Runs the server in Java daemon thread (for integration testing)
--threads=30    Processes incoming HTTP requests in 30 parallel threads
```

For example:

```java
public final class App {
  public static void main(final String... args) {
    new FtCLI(
      new TsRegex().with("/", "hello, world!"), args
    ).start(Exit.NEVER);
  }
}
```

Then, run it like this:

```
$ java -jar takes.jar App.class --port=8080 --refresh
```

You should see "hello, world!" at `http://localhost:8080`.

## Logging

The framework sends all logs to SLF4J logging facility. If you want to see them,
configure one of [SLF4J bindings](http://www.slf4j.org/manual.html).

## Directory Layout

You are free to use any build tool, but we recommend Maven. This is how your project directory layout may/should look like:

```
/src
  /main
    /java
      /foo
        App.java
    /resources
      /foo
        /xsl
        /js
        /scss
        /coffeescript
        /css
        robot.txt
      log4j.properties
  /test
    /java
      /foo
        AppTest.java
    /resources
      /foo
        /xml
      log4j.properties
pom.xml
LICENSE.txt
```

