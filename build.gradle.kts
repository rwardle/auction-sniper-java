import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    application
}

configure<ApplicationPluginConvention> {
    mainClassName = "auctionsniper.Main"
}

configure<JavaPluginConvention> {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.igniterealtime.smack:smack-im:4.2.0-beta2")
    compile("org.igniterealtime.smack:smack-tcp:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-core:4.2.0-beta2")
    runtime("org.igniterealtime.smack:smack-java7:4.2.0-beta2")
    testCompile("junit:junit:4.12")
    testCompile("org.assertj:assertj-swing-junit:3.4.0")
    testCompile("org.mockito:mockito-core:2.+")
}
