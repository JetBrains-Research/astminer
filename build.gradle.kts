import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.shadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.github.vovak.astminer"

val branchName: String by project
val ciVersion: String by project

version = if (project.hasProperty("ciVersion")) {
    ciVersion
} else {
    "0.6"
}

println(version)

plugins {
    id("java")
    kotlin("jvm") version "1.3.61" apply true
    id("antlr")
    id("idea")
    id("application")
    id("tanvd.kosogor") version "1.0.6"
    id("org.jetbrains.dokka") version "0.9.18"
    id("me.champeau.gradle.jmh") version "0.5.0"
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
    api("io.shiftleft", "fuzzyc2cpg_2.13", "1.2.9")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j", "slf4j-simple", "1.7.30")

    testImplementation("junit:junit:4.11")
    testImplementation(kotlin("test-junit"))

    implementation("com.github.ajalt", "clikt", "2.1.0")

    jmhImplementation("org.jetbrains.kotlin:kotlin-reflect:1.3.61")
    jmhImplementation("org.openjdk.jmh:jmh-core:1.21")
    jmhImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.21")
}

val shadowJar = shadowJar {
    jar {
        archiveName = "lib-$version.jar"
        mainClass = "astminer.MainKt"
    }
}.apply {
    task.archiveClassifier.set("")
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

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets["main"].java.srcDir(file(generatedSourcesPath))


idea {
    module {
        generatedSourceDirs.add(file(generatedSourcesPath))
    }
}

publishJar {
    publication {
        artifactId = if (project.hasProperty("branchName")) {
            when(branchName) {
                "master" -> "astminer"
                "master-dev" -> "astminer-dev"
                else -> ""
            }
        } else {
            "astminer"
        }
    }

    bintray {

        // If username and secretKey not set, will be taken from System environment param `bintray_user`, 'bintray_key'
        repository = "astminer"

        info {
            githubRepo = "JetBrains-Research/astminer"
            vcsUrl = "https://github.com/JetBrains-Research/astminer"
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

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
