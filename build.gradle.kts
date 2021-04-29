plugins {
    id("java")
    kotlin("jvm") version "1.4.32" apply true
    id("antlr")
    id("idea")
    id("application")
    id("org.jetbrains.dokka") version "0.9.18"
    id("me.champeau.gradle.jmh") version "0.5.0"
    id("maven-publish")
}

defaultTasks("run")

repositories {
    mavenCentral()
}

dependencies {
    // ===== Parsers =====
    antlr("org.antlr:antlr4:4.7.1")
    // https://mvnrepository.com/artifact/com.github.gumtreediff
    api("com.github.gumtreediff", "core", "2.1.0")
    api("com.github.gumtreediff", "client", "2.1.0")
    api("com.github.gumtreediff", "gen.jdt", "2.1.0")
    // https://mvnrepository.com/artifact/io.shiftleft/fuzzyc2cpg
    api("io.shiftleft", "fuzzyc2cpg_2.13", "1.2.9")

    // ===== Main =====
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt", "clikt", "2.1.0")

    // ===== Test =====
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j", "slf4j-simple", "1.7.30")
    testImplementation("junit:junit:4.11")
    testImplementation(kotlin("test-junit"))

    // ===== JMH =====
    jmhImplementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32")
    jmhImplementation("org.openjdk.jmh:jmh-core:1.21")
    jmhImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.21")
}

val generatedSourcesPath = "src/main/generated"
sourceSets["main"].java.srcDir(file(generatedSourcesPath))
idea.module.generatedSourceDirs.add(file(generatedSourcesPath))

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
    kotlinOptions.jvmTarget = "1.8"
}
tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

jmh {
    duplicateClassesStrategy = DuplicatesStrategy.WARN
    profilers = listOf("gc")
    resultFormat = "CSV"
    isZip64 = true
    failOnError = true
    forceGC = true
    warmupIterations = 1
    iterations = 4
    fork = 2
    jvmArgs = listOf("-Xmx32g")
    benchmarkMode = listOf("AverageTime")
    resultsFile = file("build/reports/benchmarks.csv")
}

val groupId: String by project
val artifactId: String by project
val version: String by project
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = groupId
            artifactId = artifactId
            version = version
        }
    }
    repositories {
        maven {
            name = artifactId
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
