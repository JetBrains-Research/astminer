import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.proxy.publishJar

buildscript {
    extra.set("kotlin_version", "1.3.41")
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = extra.get("kotlin_version") as String))
    }
}

group = "io.github.vovak.astminer"
version = "0.2"

plugins {
    id("java")
    // check https://kotlinlang.org/docs/reference/using-gradle.html
    kotlin("jvm") version "1.3.41" apply true
    id("antlr")
    id("idea")
    id("application")
    id("signing")
    id("maven-publish")
    id("tanvd.kosogor") version "1.0.6"
}


application {
    mainClassName = "astminer.MainKt"
}

defaultTasks("run")

repositories {
    mavenLocal()
    mavenCentral()
}


val generatedSourcesPath = "src/main/generated"
val kotlin_version = extra.get("kotlin_version")

dependencies {
    antlr("org.antlr:antlr4:4.7.1")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")

    // https://mvnrepository.com/artifact/com.github.gumtreediff
    compile("com.github.gumtreediff", "core", "2.1.0")
    compile("com.github.gumtreediff", "client", "2.1.0")
    compile("com.github.gumtreediff", "gen.jdt", "2.1.0")

    testCompile("junit:junit:4.11")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}



task<JavaExec> ("performanceTest") {
    main = "astminer.performance.PerformanceTest"
    classpath = sourceSets["main"].runtimeClasspath
}


tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-package", "me.vovak.antlr.parser")
    // Keep a copy of generated sources
    doLast {
        println("Copying generated grammar lexer/parser files to main directory.")
        copy {
            from("$buildDir/generated-src/antlr/main")
            into("$generatedSourcesPath/me/vovak/antlr/parser")
        }
        file("$buildDir/generated-src/antlr").deleteRecursively()
    }
    // Run when source dir has changed or was removed
    outputs.dir(generatedSourcesPath)
}

tasks.clean {
    doLast {
        file(generatedSourcesPath).deleteRecursively()
    }
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}
tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
}

sourceSets["main"].java.srcDir(file(generatedSourcesPath))


idea {
    module {
        generatedSourceDirs.add(file(generatedSourcesPath))
    }
}


publishJar {
    publication {
        artifactId = "astminer"
    }

    bintray {

       // If username and secretKey not set, will be taken from System environment param `bintray_user`, 'bintray_key'
        repository = "astminer-fork"

        info {
            githubRepo = "elena-lyulina/astminer-fork"
            vcsUrl = "https://github.com/elena-lyulina/astminer"
            license = "MIT"
            description = "Extract AST and AST-related metrics from source code: forked from vovak/astminer"
        }
    }
}