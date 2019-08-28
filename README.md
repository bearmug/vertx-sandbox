# Vertx with Java
A series of exercises, built around [Vert.x](https://vertx.io/) tooling

## Run Simple verticle natively with GraalVM
Basic verticle to be created and deployed as a native image.

### Init Java project with Gradle
This is as simple as `gradle init` [task](https://docs.gradle.org/current/userguide/build_init_plugin.html#sec:build_init_tasks).
Given task will initialize project interactively. It is ok to init as `java library` or `java app`. Anyway, build script 
to be rewritten over next steps. Please be aware that this project using **Kotlin** build file 
[build.gradle.kts](./build.gradle.kts)

### Plugin #1. Inject and configure Vertx
Gradle project may have Vert.x tooling integrated with single [plugin](https://plugins.gradle.org/plugin/io.vertx.vertx-plugin) configuration:
With that the whole project configuration will look like this ([latest file version here](./build.gradle.kts)):
```kotlin
plugins {
    id("io.vertx.vertx-plugin") version "0.8.0" // 1
}

repositories {
    jcenter()
}

dependencies {
    implementation("io.vertx:vertx-web") // 2
}

vertx {
    mainVerticle = "io.bearmug.vertx.SimpleVerticle" // 3
}
```
- (1) Gradle standard plugin import
- (2) `vertx-web` [module](https://vertx.io/docs/vertx-web/js/) to be used for simplest web-server implementation
- (3) the default verticle to run defined as a `vertx` plugin [property](https://github.com/jponge/vertx-gradle-plugin#using-kotlin-dsl)

#### Verticle body
Basic verticle barely have any extra code. It is just opens web-server and responds with default
answer to queries ([full code here](./src/main/java/io/bearmug/vertx/SimpleVerticle.java)):
```java
public class SimpleVerticle extends AbstractVerticle { // 1
    @Override
    public void start() throws Exception {
        getVertx()
                .createHttpServer()
                .requestHandler(req -> req.response().end(
                            "Hello from simple verticle at: " + System.currentTimeMillis()))
                .listen(8080);
    }
}
```

### Plugin #2. Kick-off basic logging with Slf4j2 and Lombok
Sl4j2 could be introduced as simple as:
```kotlin
dependencies {
    ...
    implementation("org.apache.logging.log4j:log4j-api:2.1") // API bindings
    implementation("org.apache.logging.log4j:log4j-core:2.1") // core implementation
}
```

Logging may bring some boilerplate code to the project. [Lombok](https://projectlombok.org/) and it's
great Gradle [plugin](https://plugins.gradle.org/plugin/io.freefair.lombok) may help with that in a no time:
```kotlin
plugins {
    ...
    id("io.freefair.lombok") version "4.0.0" // the only move to integrate Lombok to the project
}
```

Then logger could be injected with one-liner like:
```java
@Log4j2 // annotation to bind logger to the class
public class SimpleVerticle extends AbstractVerticle {
    ...
    public void start() throws Exception {
        getVertx()
                .createHttpServer()
                .requestHandler(req -> {
                    req.response().end(
                            "Hello from simple verticle at: " +
                                    System.currentTimeMillis(), event ->
                            log.info("event has been processed {}", event));})  // log member could be used right away
                .listen(8080);
    }
}
```

As an extra step we may configure enhanced log message template or alter log output. See hot it has been done
with simplest config file [here](./src/main/resources/log4j2.xml).

### Plugin #3. Configure native image build with GraalVM
Vertx app starts quite fast. But sometimes it is not quite enough. At this case we may try to transform it
to native image directly and start it as regular executable file. Again, in Gradle ecosystem it is could be
done with single [GraalVM plugin](https://plugins.gradle.org/plugin/com.palantir.graal) import:
```kotlin
plugins {
    ...
    id("com.palantir.graal") version "0.4.0" // regular plugin import
}
...
graal { // native image customization
    mainClass("io.bearmug.vertx.SimpleVerticle") // we should give a hint about main class to execute
    outputName("graal-simple-verticle") // target native image name required
}
```

Now the only extra is `main` method population:
```java
public class SimpleVerticle extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new SimpleVerticle());
    }
    ...
}
```
Native image could be generated with command `./gradlew nativeImage` and then executed with `./build/graal/graal-simple-verticle`

#### GraalVM compilation troubleshooting 
Some issues could be seen for GraalVM native compilation. Some of them are resolvable over extra development
tools setup: `sudo apt install zliblg-dev`
