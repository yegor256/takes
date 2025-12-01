# Pure Object-Oriented Java Web Framework

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/yegor256/takes)](https://www.rultor.com/p/yegor256/takes)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/takes/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/takes/actions/workflows/mvn.yml)
[![Javadoc](https://www.javadoc.io/badge/org.takes/takes.svg)](https://www.javadoc.io/doc/org.takes/takes)
[![Z vitals](https://www.takes.org/takes-badge.svg)](https://www.takes.org/takes-vitals.html)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/takes/blob/master/LICENSE.txt)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/takes.svg)](https://codecov.io/github/yegor256/takes?branch=master)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/takes)](https://hitsofcode.com/view/github/yegor256/takes)
[![Maven Central](https://img.shields.io/maven-central/v/org.takes/takes.svg)](https://maven-badges.herokuapp.com/maven-central/org.takes/takes)
[![PDD status](https://www.0pdd.com/svg?name=yegor256/takes)](https://www.0pdd.com/p?name=yegor256/takes)

**Takes** is a [true object-oriented][oop]
and [immutable][immutable]
Java 8 web development framework. Its key benefits, compared to all others,
include these four fundamental principles:

1. Not a single `null`
([why is NULL bad?][null])
2. Not a single `public` `static` method
([why are they bad?][utility])
3. Not a single mutable class
([why are they bad?][immutable])
4. Not a single `instanceof` keyword, type casting, or reflection
([why?][casting])

Of course, there are no configuration files.
Besides that, these are the more traditional features, out of the box:

* Hit-refresh debugging
* [XML+XSLT](http://www.yegor256.com/2014/06/25/xml-and-xslt-in-browser.html)
* [JSON](http://en.wikipedia.org/wiki/JSON)
* [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer)
* Templates, including [Apache Velocity](http://velocity.apache.org/)

This is what is not supported and will not be supported:

* [WebSockets](http://en.wikipedia.org/wiki/WebSocket)

These two web systems use Takes, and they are open source:
[rultor.com](http://www.rultor.com) ([sources](https://github.com/yegor256/rultor)),
[jare.io](http://www.jare.io) ([sources](https://github.com/yegor256/jare)).

Watch these videos to learn more:
[An Immutable Object-Oriented Web Framework][webcast] and
[Takes, Java Web Framework, Intro](https://www.youtube.com/watch?v=nheD2LNYrpk).
This
[blog post](http://www.yegor256.com/2015/03/22/takes-1.24.6-web-framework.html)
may help you as well.

## Contents

* [Quick Start](#quick-start)
* [Build and Run With Maven](#build-and-run-with-maven)
* [Build and Run With Gradle](#build-and-run-with-gradle)
* [Unit Testing](#unit-testing)
* [Integration Testing](#integration-testing)
* [A Bigger Example](#a-bigger-example)
  * [Front Interface](#front-interface)
  * [Back Interface](#back-interface)
* [Templates](#templates)
  * [Velocity Templates](#velocity-templates)
* [Static Resources](#static-resources)
* [Hit Refresh Debugging](#hit-refresh-debugging)
* [Request Methods (POST, PUT, HEAD, etc.)](#request-methods-post-put-head-etc)
* [Request Parsing](#request-parsing)
* [Form Processing](#form-processing)
* [Exception Handling](#exception-handling)
* [Redirects](#redirects)
* [RsJSON](#rsjson)
* [RsXembly](#rsxembly)
* [GZIP Compression](#gzip-compression)
* [SSL Configuration](#ssl-configuration)
* [Authentication](#authentication)
* [Command Line Arguments](#command-line-arguments)
* [Logging](#logging)
* [Directory Layout](#directory-layout)
* [Optional dependencies](#optional-dependencies)
* [Backward compatibility](#backward-compatibility)
* [Version pattern for RESTful API](#version-pattern-for-restful-api)
* [How to contribute](#how-to-contribute)

## Quick Start

Create this `App.java` file:

```java
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
public final class App {
  public static void main(final String... args) throws Exception {
    new FtBasic(
      new TkFork(new FkRegex("/", "hello, world!")), 8080
    ).start(Exit.NEVER);
  }
}
```

Then, download [`takes-1.24.6-jar-with-dependencies.jar`][jar]
and compile your Java code:

```bash
javac -cp takes-1.24.6-jar-with-dependencies.jar App.java
```

Now, run it like this:

```bash
java -Dfile.encoding=UTF-8 -cp takes-1.24.6-jar-with-dependencies.jar:. App
```

It should work!

This code starts a new HTTP server on port 8080 and renders a plain-text page for
all requests at the root URI.

> [!CAUTION]
> Pay attention that UTF-8 encoding is set on the command line.
> The entire framework relies on your default Java encoding, which is not
> necessarily UTF-8 by default. To be sure, always set it on the command line
> with `file.encoding` Java argument. We decided not to hard-code "UTF-8" in
> our code mostly because this would be against the entire idea of
> Java localization,
> according to which a user always should have a choice of encoding and language
> selection. We're using `Charset.defaultCharset()` everywhere in the code.

## Build and Run With Maven

If you're using Maven, this is how your `pom.xml` should look:

```xml
<project>
  <dependencies>
    <dependency>
      <groupId>org.takes</groupId>
      <artifactId>takes</artifactId>
      <version>1.24.6</version>
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
            <executions>
              <execution>
                <id>start-server</id>
                <phase>pre-integration-test</phase>
                <goals>
                  <goal>java</goal>
                </goals>
              </execution>
            </executions>
            <configuration>
              <mainClass>foo.App</mainClass> <!-- your main class -->
              <cleanupDaemonThreads>false</cleanupDaemonThreads>
              <arguments>
                <argument>--port=${port}</argument>
              </arguments>
            </configuration>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
</project>
```

With this configuration you can run it from the command line:

```bash
mvn clean integration-test -Phit-refresh -Dport=8080
```

Maven will start the server and you can see it at `http://localhost:8080`.

## Using in a Servlet App

Create a Take with constructor accepting `ServletContext`:

```java
package com.myapp;
public final class TkApp implements Take {
  private final ServletContext ctx;
  public TkApp(final ServletContext context) {
    this.ctx = context;
  }
  @Override
  public Response act(final Request req) throws Exception {
    return new RsText("Hello servlet!");
  }
}
```

Add `org.takes.servlet.SrvTake` to your `web.xml`, don't forget to specify
take class as servlet `init-param`:

```xml
<servlet>
  <servlet-name>takes</servlet-name>
  <servlet-class>org.takes.servlet.SrvTake</servlet-class>
  <init-param>
    <param-name>take</param-name>
    <param-value>com.myapp.TkApp</param-value>
  </init-param>
</servlet>
<servlet-mapping>
  <servlet-name>takes</servlet-name>
  <url-pattern>/*</url-pattern>
</servlet-mapping>
```

## Build and Run With Gradle

If you're using Gradle, this is how your `build.gradle` should look:

```groovy
plugins {
  id 'java'
  id 'application'
}
repositories {
  mavenCentral()
}
dependencies {
  implementation group: 'org.takes', name: 'takes', version: '1.24.6'
}
mainClassName='foo.App' //your main class
```

With this configuration you can run it from the command line:

```bash
gradle run -Phit-refresh -Dport=8080
```

## Unit Testing

This is how you can unit-test the app, using JUnit 4.x and
[Hamcrest](http://hamcrest.org):

```java
public final class AppTest {
  @Test
  public void returnsHttpResponse() throws Exception {
    MatcherAssert.assertThat(
      new RsPrint(
        new App().act(new RqFake("GET", "/"))
      ).printBody(),
      Matchers.equalTo("hello, world!")
    );
  }
}
```

You can create a fake request with form parameters like this:

```java
new RqForm.Fake(
  new RqFake(),
  "foo", "value-1",
  "bar", "value-2"
)
```

## Integration Testing

Here is how you can test the entire server via HTTP, using JUnit and
[jcabi-http](http://http.jcabi.com) for making HTTP requests:

```java
public final class AppITCase {
  @Test
  public void returnsTextPageOnHttpRequest() throws Exception {
    new FtRemote(new App()).exec(
      new FtRemote.Script() {
        @Override
        public void exec(final URI home) throws IOException {
          new JdkRequest(home)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(Matchers.equalTo("hello, world!"));
        }
      }
    );
  }
}
```

More complex integration testing examples can be found in one
of the open source projects that use Takes, for example:
[rultor.com][rultor-code].

## A Bigger Example

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFork(
        new FkRegex("/robots\\.txt", ""),
        new FkRegex("/", new TkIndex())
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

The `FtBasic` accepts new incoming sockets on port 8080,
parses them according to HTTP 1.1 specification and creates instances
of class `Request`. Then, it gives requests to the instance of `TkFork`
(`tk` stands for "take") and expects it to return an instance of `Take` back.
As you probably understood already, the first regular expression that matches
returns a take. `TkIndex` is our custom class,
let's see how it looks:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

It is immutable and must implement a single method `act()`, which returns
an instance of `Response`. So far so good, but this class does not have access
to the HTTP request. Here is how to solve this:

```java
new TkFork(
  new FkRegex(
    "/file/(?<path>[^/]+)",
    new TkRegex() {
      @Override
      public Response act(final RqRegex request) throws Exception {
        final File file = new File(
          request.matcher().group("path")
        );
        return new RsHTML(
          FileUtils.readFileToString(file, Charsets.UTF_8)
        );
      }
    }
  )
)
```

We are using `TkRegex` instead of `Take`, in order to work with
`RqRegex` instead of a more generic `Request`. `RqRegex` gives an instance
of `Matcher` used by `FkRegex` for pattern matching.

Here is a more complex and verbose example:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFork(
        new FkRegex("/robots.txt", ""),
        new FkRegex("/", new TkIndex()),
        new FkRegex(
          "/xsl/.*",
          new TkWithType(new TkClasspath(), "text/xsl")
        ),
        new FkRegex("/account", new TkAccount(users)),
        new FkRegex("/balance/(?<user>[a-z]+)", new TkBalance())
      )
    ).start(Exit.NEVER);
  }
}
```

## Front Interface

An essential part of the [Bigger Example](#a-bigger-example) is the
[Front](src/main/java/org/takes/http/Front.java) interface.
It encapsulates server's [back-end](src/main/java/org/takes/http/Back.java)
and is used to start an instance, which will accept requests and return results.
`FtBasic`, which is a basic front, implements that interface - you have
seen its usage in the above-mentioned example.

There are other useful implementations of this interface:

* The [FtRemote](src/main/java/org/takes/http/FtRemote.java)
class allows you to provide a script that will be executed against
a given front. You can see how it's used in
[integration tests](#integration-testing).
* The [FtCli](src/main/java/org/takes/http/FtCli.java) class
allows you to start your application with command-line arguments. More details
in [Command Line Arguments](#command-line-arguments).
* The [FtSecure](src/main/java/org/takes/http/FtSecure.java) class allows
you to start your application with SSL. More details in
[SSL Configuration](#ssl-configuration).

## Back Interface

The [Back](src/main/java/org/takes/http/Back.java) interface is the back-end that
is responsible for IO operations on TCP network level. There are various useful
implementations of that interface:

* The [BkBasic](src/main/java/org/takes/http/BkBasic.java) class is a basic
implementation of the `Back` interface. It is responsible for accepting the
request from `Socket`, converting the socket's input to the
[Request](src/main/java/org/takes/Request.java), dispatching it to the
provided [Take](src/main/java/org/takes/Take.java) instance, getting
the result and printing it to the socket's output until the request is
fulfilled.
* The [BkParallel](src/main/java/org/takes/http/BkParallel.java) class is
a decorator of the `Back` interface, that is responsible for running the
back-end in parallel threads. You can specify the number of threads or try
to use the default number, which depends on available processors number in JVM.
* The [BkSafe](src/main/java/org/takes/http/BkSafe.java) class is a decorator
of the `Back` interface, that is responsible for running the back-end in a
safe mode. That means that it will ignore exceptions thrown from the original `Back`.
* The [BkTimeable](src/main/java/org/takes/http/BkTimeable.java) class is a
decorator of the `Back` interface, that is responsible for running the back-end
for a specified maximum lifetime in milliseconds. It is constantly checking if
the thread with the original `back` exceeds the provided limit and if so - it
interrupts the thread of that `back`.
* The [BkWrap](src/main/java/org/takes/http/BkWrap.java) class is a convenient
wrap over the original `Back` instance. It just delegates the `accept`
to that `Back` and might be useful if you want to add your own decorators of the
`Back` interface. This class is used in `BkParallel` and `BkSafe` as
a parent class.

## Templates

Now let's see how we can render something more complex than a plain text.
First, XML+XSLT is a recommended mechanism of HTML rendering.
Even though it may seem complex, give it a try; you will not regret it.
Here is how we render a simple XML page that is transformed
to HTML5 on the fly (more about `RsXembly` below):

```java
public final class TkAccount implements Take {
  private final Users users;
  public TkAccount(final Users users) {
    this.users = users;
  }
  @Override
  public Response act(final Request req) {
    final User user = this.users.find(new RqCookies(req).get("user"));
    return new RsLogin(
      new RsXslt(
        new RsXembly(
          new XeStylesheet("/xsl/account.xsl"),
          new XeAppend("page", user)
        )
      ),
      user
    );
  }
}
```

This is how that `User` class may look:

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

Here is how `RsLogin` may look:

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

Let's say you want to use [Velocity](http://velocity.apache.org/):

```java
public final class TkHelloWorld implements Take {
  @Override
  public Response act(final Request req) {
    return new RsVelocity(
      "Hi, ${user.name}! You've got ${user.balance}",
      new RsVelocity.Pair("user", new User())
    );
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

For Gradle users:

```groovy
dependencies {
  ...
  runtime group: 'org.apache.velocity',
  name: 'velocity-engine-core',
  version: 'x.xx' // put the version here
  ...
}
```

## Static Resources

Very often you need to serve static resources to your web users, like CSS
stylesheets, images, JavaScript files, etc. There are a few supplementary
classes for that:

```java
new TkFork(
  new FkRegex("/css/.+", new TkWithType(new TkClasspath(), "text/css")),
  new FkRegex("/data/.+", new TkFiles(new File("/usr/local/data")))
)
```

The `TkClasspath` class takes the static part of the request URI and finds
a resource with this name in the classpath.

`TkFiles` looks for files by name in the configured directory.

`TkWithType` sets the content type of all responses coming out of
the decorated take.

## Hit Refresh Debugging

It is a very convenient feature. Once you start the app you want to be able to
modify its static resources (CSS, JS, XSL, etc), refresh the page in a browser
and immediately see the result. You don't want to re-compile the entire project
and restart it. Here is what you need to do to your sources in order to enable
that feature:

```java
new TkFork(
  new FkRegex(
    "/css/.+",
    new TkWithType(
      new TkFork(
        new FkHitRefresh(
          "./src/main/resources/foo/scss/**", // what sources to watch
          "mvn sass:compile", // what to run when sources are modified
          new TkFiles("./target/css")
        ),
        new FkFixed(new TkClasspath())
      ),
      "text/css"
    )
  )
)
```

This `FkHitRefresh` fork is a decorator of Take. Once it sees
`X-Take-Refresh` header in the request, it realizes that the server
is running in
"hit-refresh" mode and passes the request to the encapsulated take. Before it
passes the request, it tries to understand whether any of the resources
are older than the compiled files. If they are older, it tries
to run the compilation tool to build them again.

## Request Methods (POST, PUT, HEAD, etc.)

Here is an example:

```java
new TkFork(
  new FkRegex(
    "/user",
    new TkFork(
      new FkMethods("GET", new TkGetUser()),
      new FkMethods("POST,PUT", new TkPostUser()),
      new FkMethods("DELETE", new TkDeleteUser())
    )
  )
)
```

## Request Parsing

Here is how you can parse an instance of `Request`:

```java
Href href = new RqHref.Base(request).href();
URI uri = href.uri();
Iterable<String> values = href.param("key");
```

For a more complex parsing try to use Apache HTTP Client or something
similar.

## Form Processing

Here is an example:

```java
public final class TkSavePhoto implements Take {
  @Override
  public Response act(final Request req) {
    final String name = new RqForm(req).param("name");
    return new RsWithStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}
```

## Exception Handling

By default, `TkFork` lets all exceptions bubble up. If one of your Takes
crashes, a user will see a default error page. Here is how you can configure
this behavior:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFallback(
        new TkFork(
          new FkRegex("/robots\\.txt", ""),
          new FkRegex("/", new TkIndex())
        ),
        new FbChain(
          new FbStatus(404, new RsText("Sorry, page is absent")),
          new FbStatus(405, new RsText("This method is not allowed here")),
          new Fallback() {
            @Override
            public Iterator<Response> route(final RqFallback req) {
              return Collections.<Response>singleton(
                new RsHTML("Oops, something went terribly wrong!")
              ).iterator();
            }
          }
        )
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

`TkFallback` decorates an instance of Take and catches all exceptions any of
its Takes may throw. Once it is thrown, an instance of `FbChain` will
find the most suitable fallback and will fetch a response from there.

## Redirects

Sometimes it's very useful to return a redirect response (`30x` status code),
either by a normal `return` or by throwing an exception. This example
illustrates both methods:

```java
public final class TkPostMessage implements Take {
  @Override
  public Response act(final Request req) {
    final String body = new RqPrint(req).printBody();
    if (body.isEmpty()) {
      throw new RsForward(
        new RsFlash("Message can't be empty")
      );
    }
    // save the message to the database
    return new RsForward(
      new RsFlash(
        "Thanks, the message was posted"
      ),
      "/"
    );
  }
}
```

Then, you should decorate the entire `TkFork` with this
`TkForward` and `TkFlash`:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFlash(
        new TkForward(
          new TkFork(new FkRegex("/", new TkPostMessage())
        )
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
  @Override
  public Response act(final RqRegex request) {
    return new RsJSON(
      new User(request.matcher().group("user"))
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

Here is how to generate an XML page using [Xembly](http://www.xembly.org):

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
    user, // add user to the root element
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
  new XeFoo(user)
)
```

This is how this `XeFoo` class would look:

```java
public final class XeFoo extends XeWrap {
  public XeFoo(final String stylesheet, final XeSource... sources) {
    super(
      new XeAppend(
        "page",
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

More about this mechanism in this blog post:
[XML Data and XSL Views in Takes Framework][xsl].

## Cookies

Here is how we drop a cookie to the user:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsWithCookie("auth", "John Doe");
  }
}
```

An HTTP response will contain this header, which will place
an `auth` cookie into the user's browser:

```text
HTTP/1.1 200 OK
Set-Cookie: auth="John Doe"
```

This is how you read cookies from a request:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    // the list may be empty
    final Iterable<String> cookies = new RqCookies(req).cookie("my-cookie");
  }
}
```

## GZIP Compression

If you want to compress all your responses with GZIP, wrap your take in
`TkGzip`:

```java
new TkGzip(take)
```

Now, each request that contains the `Accept-Encoding` request header with `gzip`
compression method inside will receive a GZIP-compressed response. Also,
you can compress an individual response, using `RsGzip` decorator.

## Content Negotiation

Say you want to return different content based on the `Accept` header
of the request (a.k.a.
[content negotiation](http://en.wikipedia.org/wiki/Content_negotiation)):

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsFork(
      req,
      new FkTypes("text/*", new RsText("it's a text")),
      new FkTypes("application/json", new RsJSON("{\"a\":1}")),
      new FkTypes("image/png", /* something else */)
    );
  }
}
```

## SSL Configuration

First of all, set up your keystore settings, for example:

```java
final String file = this.getClass().getResource("/org/takes/http/keystore").getFile();
final String password = "abc123";
System.setProperty("javax.net.ssl.keyStore", file);
System.setProperty("javax.net.ssl.keyStorePassword", password);
System.setProperty("javax.net.ssl.trustStore", file);
System.setProperty("javax.net.ssl.trustStorePassword", password);
```

Then simply create an instance of the
[`FtSecure`](src/main/java/org/takes/http/FtSecure.java) class with socket factory

```java
final ServerSocket skt = SSLServerSocketFactory.getDefault().createServerSocket(0);
new FtRemote(
  new FtSecure(new BkBasic(new TkFixed("hello, world")), skt),
  skt,
  true
);
```

## Authentication

Here is an example of login via
[Facebook](https://developers.facebook.com/docs/reference/dialogs/oauth/):

```java
new TkAuth(
  new TkFork(
    new FkRegex("/", new TkHTML("Hello, check <a href='/acc'>account</a>")),
    new FkRegex("/acc", new TkSecure(new TkAccount()))
  ),
  new PsChain(
    new PsCookie(
      new CcSafe(new CcHex(new CcXOR(new CcCompact(), "secret-code")))
    ),
    new PsByFlag(
      new PsByFlag.Pair(
        PsFacebook.class.getSimpleName(),
        new PsFacebook("facebook-app-id", "facebook-secret")
      ),
      new PsByFlag.Pair(
        PsLogout.class.getSimpleName(),
        new PsLogout()
      )
    )
  )
)
```

Then, you need to show a login link to the user, which the user
can click to get to the Facebook OAuth authentication page. Here is how
you do this with XeResponse:

```java
new RsXembly(
  new XeStylesheet("/xsl/index.xsl"),
  new XeAppend(
    "page",
    new XeFacebookLink(req, "facebook-app-id"),
    // ... other xembly sources
  )
)
```

The link will be added to the XML page like this:

```xml
<page>
  <links>
    <link rel="take:facebook" href="https://www.facebook.com/dialog/oauth..."/>
  </links>
</page>
```

Similar mechanism can be used for `PsGithub`,
`PsGoogle`, `PsLinkedin`, `PsTwitter`, etc.

This is how to get the currently logged-in user:

```java
public final class TkAccount implements Take {
  @Override
  public Response act(final Request req) {
    final Identity identity = new RqAuth(req).identity();
    if (identity.equals(Identity.ANONYMOUS)) {
      // returns "urn:facebook:1234567" for a user logged in via Facebook
      identity.urn();
    }
  }
}
```

More about it in this blog post:
[How Cookie-Based Authentication Works in the Takes Framework][cookies].

## Command Line Arguments

There is a convenient `FtCLI` class that parses command-line arguments and
starts the necessary `Front` accordingly.

There are a few command-line arguments that should be passed to
`FtCLI` constructor:

```text
--port=1234         Tells the server to listen to TCP port 1234
--lifetime=5000     The server will die in five seconds (useful for integration testing)
--hit-refresh       Run the server in hit-refresh mode
--daemon            Runs the server in Java daemon thread (for integration testing)
--threads=30        Processes incoming HTTP requests in 30 parallel threads
--max-latency=5000  Maximum latency in milliseconds per each request
                    (longer requests will be interrupted)
```

For example:

```java
public final class App {
  public static void main(final String... args) {
    new FtCLI(
      new TkFork(new FkRegex("/", "hello, world!")),
      args
    ).start(Exit.NEVER);
  }
}
```

Then run it like this:

```bash
java -cp take.jar App.class --port=8080 --hit-refresh
```

You should see "hello, world!" at `http://localhost:8080`.

Parameter `--port` also accepts a file name, instead of a number. If the file
exists, `FtCLI` will try to read its content and use it as
port number. If the file is absent, `FtCLI` will allocate a new random
port number, use it to start a server, and save it to the file.

## Logging

The framework sends all logs to SLF4J logging facility. If you want to see them,
configure one of [SLF4J bindings](http://www.slf4j.org/manual.html).

To make a `Take` log, wrap it in the `TkSlf4j`, for example:

```java
new TkSlf4j(
  new TkFork(/* your code here */)
)
```

## Directory Layout

You are free to use any build tool, but we recommend Maven.
This is how your project directory layout may/should look:

```text
src/
  main/
    java/
      foo/
        App.java
    scss/
    coffeescript/
    resources/
      vtl/
      xsl/
      js/
      css/
      robots.txt
pom.xml
LICENSE.txt
```

## Optional Dependencies

If you are using Maven and include Takes as a dependency in your own project,
you can choose which of the optional dependencies to include in your project.
The list of all of the optional dependencies can be seen in the
Takes project `pom.xml`.

For example, to use the Facebook API shown above, simply add a dependency to
the `restfb` API in your project:

```xml
<dependency>
  <groupId>com.restfb</groupId>
  <artifactId>restfb</artifactId>
  <scope>runtime</scope>
</dependency>
```

For Gradle, add the dependencies as usual:

```groovy
dependencies {
  ...
  runtime group: 'com.restfb', name: 'restfb', version: '1.15.0'
}
```

## Backward Compatibility

Version 2.0 is not backward-compatible with previous versions.

## Version Pattern for RESTful API

The URL should NOT contain the version, but the type requested.

For example:

```text
===>
GET /architect/256 HTTP/1.1
Accept: application/org.takes.architect-v1+xml
<===
HTTP/1.1 200 OK
Content-Type: application/org.takes.architect-v1+xml
<architect>
  <name>Yegor Bugayenko</name>
</architect>
```

Then clients aware of a newer version of this service can call:

```text
===>
GET /architect/256 HTTP/1.1
Accept: application/org.takes.architect-v2+xml
<===
HTTP/1.1 200 OK
Content-Type: application/org.takes.architect-v2+xml

<architect>
  <firstName>Yegor</firstName>
  <lastName>Bugayenko</lastName>
  <salutation>Mr.</salutation>
</architect>
```

[This article][rest-types]
explains why it's done this way.

## How to Contribute

Fork the repository, make changes, and send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request, please run the full Maven build:

```bash
mvn clean install -Pqulice
```

To avoid build errors, use Maven 3.2+.

Note that our `pom.xml` inherits a lot of configuration
from [jcabi-parent](http://parent.jcabi.com).
[This article](http://www.yegor256.com/2015/02/05/jcabi-parent-maven-pom.html)
explains why it's done this way.

[jar]: https://repo1.maven.org/maven2/org/takes/takes/1.24.6/takes-1.24.6-jar-with-dependencies.jar
[oop]: http://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html
[immutable]: http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html
[null]: http://www.yegor256.com/2014/05/13/why-null-is-bad.html
[utility]: http://www.yegor256.com/2014/05/05/oop-alternative-to-utility-classes.html
[casting]: http://www.yegor256.com/2015/04/02/class-casting-is-anti-pattern.html
[webcast]: https://www.youtube.com/watch?v=-Y4XS7ZtQ2g
[rultor-code]: https://github.com/yegor256/rultor/tree/master/src/test/java/com/rultor/web
[xsl]:https://www.yegor256.com/2015/06/25/xml-data-xsl-views-takes-framework.html
[cookies]: http://www.yegor256.com/2015/05/18/cookie-based-authentication.html
[rest-types]: http://thereisnorightway.blogspot.com/2011/02/versioning-and-types-in-resthttp-api.html
