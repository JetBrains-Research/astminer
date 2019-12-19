import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.shadowJar

plugins {
    id("java")
    kotlin("jvm") version "1.3.61"
    id("application")
    id("tanvd.kosogor") version "1.0.6"
}

application {
    mainClassName = "cli.MainKt"
}

group = "io.github.vovak.astminer"
version = "0.3"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/egor-bogomolov/astminer/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Local build of Astminer
    implementation(files("../build/shadow/lib-0.5.jar"))

    implementation("com.github.ajalt", "clikt", "2.1.0")

    testImplementation("junit:junit:4.11")
    testImplementation(kotlin("test-junit"))
}

val shadowJar = shadowJar {
    jar {
        archiveName = "cli-$version.jar"
        mainClass = "cli.MainKt"
    }
}.apply {
    task.archiveClassifier.set("")
}

publishJar {
    publication {
        artifactId = "astminer-cli"
    }

    jar {
        components = { shadowJar.artifact(this) }
    }

    bintray {

        // If username and secretKey not set, will be taken from System environment param `bintray_user`, 'bintray_key'
        repository = "astminer"

        info {
            githubRepo = "JetBrains-Research/astminer"
            vcsUrl = "https://github.com/JetBrains-Research/astminer/astminer-cli"
            labels.addAll(listOf("mining", "ast", "ml4se", "code2vec", "path-based representations"))
            license = "MIT"
            description = "CLI for AstMiner library"
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType(BintrayUploadTask::class) {
    dependsOn("shadowJar")
}
