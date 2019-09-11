import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tanvd.kosogor.proxy.shadowJar

plugins {
    id("java")
    kotlin("jvm") version "1.3.41"
    id("application")
    id("tanvd.kosogor") version "1.0.6"
}

application {
    mainClassName = "examples.MainKt"
}

group = "io.github.vovak.astminer"
version = "0.1"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/egor-bogomolov/astminer/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("io.github.vovak.astminer", "astminer", "0.3")
    compile("com.github.ajalt", "clikt", "2.1.0")
}

shadowJar {
    jar {
        archiveName = "cli.jar"
        mainClass = "examples.MainKt"
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
