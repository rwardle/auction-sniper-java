import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    idea
    kotlin("jvm") version "1.3.10"
    id("org.unbroken-dome.test-sets") version "2.0.3"
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
    get("integrationTestImplementation").extendsFrom(get("endToEndTestImplementation"))
}

idea {
    module {
        sourceDirs.removeAll(files("src/endToEndTest/kotlin", "src/integrationTest/kotlin"))
        testSourceDirs.addAll(files("src/endToEndTest/kotlin", "src/integrationTest/kotlin"))
    }
}

dependencies {
    val autoValueVersion = "1.6.2"
    val serenityVersion = "1.5.3"
    val smackVersion = "4.2.0-beta2"

    annotationProcessor("com.google.auto.value:auto-value:$autoValueVersion")
    implementation("com.google.auto.value:auto-value-annotations:$autoValueVersion")
    implementation("org.igniterealtime.smack:smack-im:$smackVersion")
    implementation("org.igniterealtime.smack:smack-tcp:$smackVersion")
    runtime("org.igniterealtime.smack:smack-core:$smackVersion")
    runtime("org.igniterealtime.smack:smack-java7:$smackVersion")
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.+")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
    testImplementation("com.natpryce:make-it-easy:4.0.1")
    add("endToEndTestImplementation", kotlin("stdlib-jdk8"))
    add("endToEndTestImplementation", kotlin("test"))
    add("endToEndTestImplementation", kotlin("test-junit"))
    add("endToEndTestImplementation", "org.assertj:assertj-swing-junit:3.4.0")
    add("endToEndTestImplementation", "net.serenity-bdd:serenity-core:$serenityVersion")
    add("endToEndTestImplementation", "net.serenity-bdd:serenity-screenplay:$serenityVersion")
    add("endToEndTestImplementation", "net.serenity-bdd:serenity-junit:$serenityVersion")
    add("endToEndTestImplementation", "org.awaitility:awaitility:3.0.0")
    add("endToEndTestImplementation", sourceSets["main"].output.classesDirs)
    add("endToEndTestImplementation", sourceSets["test"].output.classesDirs)
    add("integrationTestImplementation", sourceSets["endToEndTest"].output.classesDirs)
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
