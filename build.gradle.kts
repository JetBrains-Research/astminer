import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.shadowJar

group = "io.github.vovak.astminer"
version = "0.5.5"

plugins {
    id("java")
    kotlin("jvm") version "1.3.60" apply true
    id("antlr")
    id("idea")
    id("application")
    id("tanvd.kosogor") version "1.0.6"
    id("org.jetbrains.dokka") version "0.9.18"
}


application {
    mainClassName = "astminer.MainKt"
}

defaultTasks("run")

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}


val generatedSourcesPath = "src/main/generated"

dependencies {
    antlr("org.antlr:antlr4:4.7.1")
    implementation(kotlin("stdlib"))

    // https://mvnrepository.com/artifact/com.github.gumtreediff
    api("com.github.gumtreediff", "core", "2.1.0")
    api("com.github.gumtreediff", "client", "2.1.0")
    api("com.github.gumtreediff", "gen.jdt", "2.1.0")

    // https://mvnrepository.com/artifact/io.shiftleft/fuzzyc2cpg
    api("io.shiftleft", "fuzzyc2cpg_2.12", "0.1.74") {
        exclude("org.slf4j", "slf4j-simple")
    }

    testImplementation("junit:junit:4.11")
    testImplementation(kotlin("test-junit"))
}

val shadowJar = shadowJar {
    jar {
        archiveName = "lib-$version.jar"
        mainClass = "astminer.MainKt"
    }
}.apply {
    task.archiveClassifier.set("")
}

task<JavaExec>("performanceTest") {
    main = "astminer.performance.PerformanceTest"
    classpath = sourceSets["main"].runtimeClasspath
}

task<JavaExec>("processPyExample") {
    main = "astminer.examples.pyExample.PyExample"
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
        artifactId = "astminer-dev"
    }

    bintray {

        // If username and secretKey not set, will be taken from System environment param `bintray_user`, 'bintray_key'
        repository = "astminer"

        info {
            githubRepo = "vovak/astminer"
            vcsUrl = "https://github.com/vovak/astminer"
            labels.addAll(listOf("mining", "ast", "ml4se", "code2vec", "path-based representations"))
            license = "MIT"
            description = "Extract AST, AST-related metrics, and path-based representations from source code"
        }
    }
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}
