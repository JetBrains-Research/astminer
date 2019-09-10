import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.3.41"
    id("application")
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
    testCompile("junit", "kotlinx-cli", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
