plugins {
    id("java")
    id ("application")
    id ("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.sagu.fhv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "17.0.15"
    modules("javafx.controls")
}


dependencies {
    implementation("org.joml:joml:1.10.8")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}