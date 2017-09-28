import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    id("org.jetbrains.kotlin.jvm") version "1.1.51"
}

application {
    mainClassName = "auctionsniper.Main"
    group = "com.richardwardle"
    version = "1.0-SNAPSHOT"
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.auto.value:auto-value:1.2")
    compile("org.igniterealtime.smack:smack-im:4.2.0-beta2")
    compile("org.igniterealtime.smack:smack-tcp:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-core:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-java7:4.2.0-beta2")
    testCompile("junit:junit:4.12")
    testCompile("org.assertj:assertj-swing-junit:3.4.0")
    testCompile("org.mockito:mockito-core:2.+")
    testCompile("org.hamcrest:hamcrest-library:1.3")
    testCompile("commons-io:commons-io:+")
    testCompile("com.natpryce:make-it-easy:4.0.1")
    testCompile(kotlin("stdlib-jre8", "1.1.51"))
    testCompile(kotlin("test", "1.1.51"))
    testCompile(kotlin("test-junit", "1.1.51"))
    testCompile("net.serenity-bdd:serenity-core:1.5.3")
    testCompile("net.serenity-bdd:serenity-screenplay:1.5.3")
    testCompile("net.serenity-bdd:serenity-junit:1.5.3")
}
