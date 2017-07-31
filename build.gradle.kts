import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    application
    java
    groovy
}

configure<ApplicationPluginConvention> {
    mainClassName = "auctionsniper.Main"
    group = "com.richardwardle"
    version = "1.0-SNAPSHOT"
}

configure<JavaPluginConvention> {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
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
    testCompile("org.codehaus.groovy:groovy-all:2.4.8")
    testCompile("org.spockframework:spock-core:1.1-groovy-2.4-rc-3")
    testCompile("commons-io:commons-io:+")
    testCompile("com.natpryce:make-it-easy:4.0.1")
}
