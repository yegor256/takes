First, create an app:

```java
public final class App {
  public static void main(final String... args) {
    new Server(
      new PgsRegex()
        .with("/help", "hello, world!")
        .with("/", new PgIndex())
        .with(
          "/account", 
          new Page.() {
            @Override
            public Page page(final Request request) {
              return new PgAccount(request);
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
public final class PgIndex implements Page {
  @Override
  public Response draw() {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

Let's create a more complex page:

```java
@Immutable
public final class PgAccount implements Page {
  private final User user;
  public PgAccount(final Users users, final Request request) {
    this.user = users.find(new RqCookies(request).get("user"));
  }
  @Override
  public Response draw() {
    return new RsLogin(
      new RsXSLT(
        new RsXembly(
          new RsXML(),
          this.user
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
public final class User implements RsXembly.Source {
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
