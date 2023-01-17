import tanvd.kosogor.proxy.shadowJar

group = "io.github.vovak"
version = "0.9.1"

plugins {
    id("java")
    id("antlr")
    id("application")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.32"
    id("tanvd.kosogor") version "1.0.10"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    kotlin("jvm") version "1.7.21" apply true
    kotlin("plugin.serialization") version "1.7.21"
}

defaultTasks("run")

repositories {
    mavenCentral()
}

dependencies {
    // ===== Parsers =====
    antlr("org.antlr:antlr4:4.7.1")
    // https://mvnrepository.com/artifact/com.github.gumtreediff
    api("com.github.gumtreediff", "core", "3.0.0")
    api("com.github.gumtreediff", "client", "3.0.0")
    api("com.github.gumtreediff", "gen.jdt", "3.0.0")
    api("com.github.gumtreediff", "gen.srcml","3.0.0")
    api("com.github.gumtreediff", "gen.python", "3.0.0")

    // https://github.com/javaparser/javaparser
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.22.1")

    // https://spoon.gforge.inria.fr/
    implementation("fr.inria.gforge.spoon:spoon-core:9.1.0-beta-16")

    // https://mvnrepository.com/artifact/io.shiftleft/fuzzyc2cpg
    api("io.shiftleft", "fuzzyc2cpg_2.13", "1.3.415")

    // ===== Main =====
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt:clikt:3.2.0")
    implementation("com.charleskorn.kaml:kaml:0.33.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // ===== Logging =====
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("io.github.microutils:kotlin-logging:1.5.9")

    // ===== Test =====
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test-junit"))

    // ===== Detekt =====
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")

    // ==== Status bar ====
    implementation("me.tongfei:progressbar:0.9.2")
}

val generatedSourcesPath = "src/main/generated"
sourceSets["main"].java.srcDir(file(generatedSourcesPath))
idea.module.generatedSourceDirs.add(file(generatedSourcesPath))

tasks.generateGrammarSource {
    // maxHeapSize = "64m"
    arguments.addAll(listOf("-package", "me.vovak.antlr.parser"))
    // Keep a copy of generated sources
    doLast {
        println("Copying generated grammar lexer/parser files to main directory.")
        println("To: $generatedSourcesPath/me/vovak/antlr/parser")
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
    kotlinOptions.jvmTarget = "11"
}
tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
    targetCompatibility = "11"
    sourceCompatibility = "11"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/astminer/astminer")
            credentials {
                username = System.getenv("PUBLISH_USER")?.takeIf { it.isNotBlank() } ?: ""
                password = System.getenv("PUBLISH_PASSWORD")?.takeIf { it.isNotBlank() } ?: ""
            }
        }
    }
}

application.mainClassName = "astminer.MainKt"
shadowJar {
    jar {
        archiveName = "astminer.jar"
    }
}.apply {
    task.archiveClassifier.set("")
}

tasks.withType<Test> {
    // Kotlin DSL workaround from https://github.com/gradle/kotlin-dsl-samples/issues/836#issuecomment-384206237
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println(
                    "${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, " +
                    "${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                )
            }
        }
    })
}

detekt {
    allRules = true
    autoCorrect = true
    parallel = true
    config = files("detekt.yaml")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
}
