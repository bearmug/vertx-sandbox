plugins {
    id("io.vertx.vertx-plugin") version "0.8.0"
    id("io.freefair.lombok") version "4.0.0"
    id("com.palantir.graal") version "0.4.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation("io.vertx:vertx-web")
    implementation("org.apache.logging.log4j:log4j-api:2.1")
    implementation("org.apache.logging.log4j:log4j-core:2.1")
}

vertx {
    mainVerticle = "io.bearmug.vertx.SimpleVerticle"
    vertxVersion = "3.8.0"
}

graal {
    mainClass("io.bearmug.vertx.SimpleVerticle")
    outputName("graal-simple-verticle")
}