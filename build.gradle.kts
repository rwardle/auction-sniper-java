import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet

plugins {
    application
    java
    idea
    id("org.jetbrains.kotlin.jvm") version "1.1.51"
    id("org.unbroken-dome.test-sets") version "1.4.2"
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

testSets {
    create("endToEndTest")
    create("integrationTest")
}

configurations {
    get("integrationTestCompile").extendsFrom(get("endToEndTestCompile"))
}

idea {
    module {
        sourceDirs.removeAll(files("src/endToEndTest/kotlin", "src/integrationTest/kotlin"))
        testSourceDirs.addAll(files("src/endToEndTest/kotlin", "src/integrationTest/kotlin"))
    }
}

dependencies {
    compileOnly("com.google.auto.value:auto-value:1.2")
    compile("org.igniterealtime.smack:smack-im:4.2.0-beta2")
    compile("org.igniterealtime.smack:smack-tcp:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-core:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-java7:4.2.0-beta2")
    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:2.+")
    testCompile("org.hamcrest:hamcrest-library:1.3")
    testCompile("com.natpryce:make-it-easy:4.0.1")
    add("endToEndTestCompile", kotlin("stdlib-jre8", "1.1.51"))
    add("endToEndTestCompile", kotlin("test", "1.1.51"))
    add("endToEndTestCompile", kotlin("test-junit", "1.1.51"))
    add("endToEndTestCompile", "org.assertj:assertj-swing-junit:3.4.0")
    add("endToEndTestCompile", "net.serenity-bdd:serenity-core:1.5.3")
    add("endToEndTestCompile", "net.serenity-bdd:serenity-screenplay:1.5.3")
    add("endToEndTestCompile", "net.serenity-bdd:serenity-junit:1.5.3")
    add("endToEndTestCompile", java.sourceSets["test"].output.classesDirs)
    add("integrationTestCompile", java.sourceSets["endToEndTest"].output.classesDirs)
}

tasks {
    val test: Task = get("test")
    val integrationTest: Task = get("integrationTest")
    val endToEndTest: Task = get("endToEndTest")

    get("check").dependsOn(integrationTest, endToEndTest)

    integrationTest.mustRunAfter(test)
    endToEndTest.mustRunAfter(test)
    endToEndTest.mustRunAfter(integrationTest)
}
