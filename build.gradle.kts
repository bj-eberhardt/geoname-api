plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.0"
    id("io.micronaut.test-resources") version "4.4.0"
    id("io.micronaut.aot") version "4.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

version = "1.0"
group = "city.db"

val kotlinVersion = project.properties["kotlinVersion"]

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut.openapi:micronaut-openapi")
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut.data:micronaut-data-processor")

    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("com.opencsv:opencsv:5.9")

    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("org.flywaydb:flyway-mysql")
}

application {
    mainClass = "city.db.ApplicationKt"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("city.db.*")
    }
    testResources {
        additionalModules.add("jdbc-postgresql")
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("geonames-api")
        }
    }
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
    instruction("RUN apk add curl")
    instruction("""HEALTHCHECK CMD curl -s localhost:8080/health | grep '"status":"UP"' """)
}

ksp {
    arg("micronaut.openapi.project.dir", projectDir.toString())
}

// deployment and docker push to docker hub registry

tasks.register<Exec>("dockerTagLatest") {
    group = "release"
    dependsOn("dockerBuildNative")
    commandLine("docker", "tag", "geonames-api:latest", "beberhardt/geonames-api:latest")
}

tasks.register<Exec>("dockerTagVersion") {
    group = "release"
    dependsOn("dockerBuildNative")
    commandLine("docker", "tag", "geonames-api:latest", "beberhardt/geonames-api:$version")
}

tasks.register<Exec>("dockerPushLatestImage") {
    group = "release"
    dependsOn("dockerTagLatest")
    commandLine("docker", "push", "beberhardt/geonames-api:latest")
}

tasks.register<Exec>("dockerPushVersionedImage") {
    group = "release"
    dependsOn("dockerTagVersion")
    commandLine("docker", "push", "beberhardt/geonames-api:$version")
}

tasks.register("tagAndPushImages") {
    group = "release"
    dependsOn("dockerPushVersionedImage", "dockerPushLatestImage")
}
