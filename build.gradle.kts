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

/**
 * Without the next section Gradle will add a "compile" dependency on Antlr3:
 * https://github.com/gradle/gradle/issues/820
 */


//todo: decide which one is correct
//val compile by configurations.creating {
//    setExtendsFrom(extendsFrom.filter { it != configurations.antlr })
//}
//
//configurations {
//    compile {
//        setExtendsFrom(extendsFrom.filter { it != configurations.antlr })
//    }
//}

val generatedSourcesPath = "src/main/generated"
val kotlin_version = extra.get("kotlin_version")

dependencies {
    antlr("org.antlr:antlr4:4.7.1")
    compile("org.antlr:antlr4-runtime:4.7.1")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")

    // https://mvnrepository.com/artifact/com.github.gumtreediff/core
    compile("com.github.gumtreediff", "core", "2.1.0")

    // https://mvnrepository.com/artifact/com.github.gumtreediff/client
    compile("com.github.gumtreediff", "client", "2.1.0")

    // https://mvnrepository.com/artifact/com.github.gumtreediff/gen.jdt
    compile("com.github.gumtreediff", "gen.jdt", "2.1.0")

    testCompile("junit:junit:4.11")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

task<JavaExec>("processPyExample") {
    main = "astminer.examples.pyExample.PyExample"
    classpath = sourceSets["main"].runtimeClasspath
}

//task<JavaExec>("runCppExample") {
//    main = "astminer.examples.CppExample"
//    classpath = sourceSets["main"].runtimeClasspath
//}

task<JavaExec> ("performanceTest") {
    main = "astminer.performance.PerformanceTest"
    classpath = sourceSets["main"].runtimeClasspath
}


tasks.generateGrammarSource {
    maxHeapSize = "64m"
    exclude(listOf("CoarseSimpleDecl.g4", "Common.g4", "Expressions.g4", "FineSimpleDecl.g4",
        "FunctionDef.g4", "ModuleLex.g4", "SimpleDecl.g4"))
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


//
//task sourceJar(type: Jar) {
//    classifier("sources")
//    from(sourceSets.main.allSource)
//}
//
//task javadocJar(type: Jar, dependsOn: javadoc) {
//    classifier("javadoc")
//    from(javadoc.destinationDir)
//}
//
//artifacts {
//    archives(jar)
//    archives(sourceJar)
//    archives(javadocJar)
//}
//
//signing {
//    sign(configurations.archives)
//}
//
//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            customizePom(pom)
//            groupId = "io.github.vovak"
//            artifactId = "astminer"
//            version = "0.2"
//
//            from(components.java)
//
//            // create the sign pom artifact
//            pom.withXml {
//                def pomFile = file("${project.buildDir}/generated-pom.xml")
//                writeTo(pomFile)
//                def pomAscFile = signing.sign(pomFile).signatureFiles[0]
//                artifact(pomAscFile) {
//                    classifier = null
//                    extension = "pom.asc"
//                }
//            }
//
//            artifact(sourceJar) {
//                classifier = "sources"
//            }
//
//            artifact(javadocJar) {
//                classifier = "javadoc"
//            }
//
//            // create the signed artifacts
//            project.tasks.signArchives.signatureFiles.each {
//                artifact(it) {
//                    def matcher = it.file =~ /-(sources|javadoc)\.jar\.asc$/
//                    if (matcher.find()) {
//                        classifier = matcher.group(1)
//                    } else {
//                        classifier = null
//                    }
//                    extension = it.file.path.reverse().take(7).reverse()
//                }
//            }
//        }
//    }
//
//    repositories {
//        maven {
//            url = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
//            credentials {
//                username = System.getenv("SONATYPE_USERNAME")
//                password = System.getenv("SONATYPE_PASSWORD")
//            }
//        }
//    }
//}
//
//def customizePom(pom) {
//    pom.withXml {
//        def root = asNode()
//
//        // eliminate test-scoped dependencies (no need in maven central POMs)
//        root.dependencies.removeAll { dep ->
//            dep.scope == "test"
//        }
//
//        // add all items necessary for maven central publication
//        root.children().last() + {
//            resolveStrategy = Closure.DELEGATE_FIRST
//
//            description = "Extract AST and AST-related metrics from source code"
//            name = "AST miner"
//            url = "https://github.com/vovak/astminer"
//            organization {
//                name = "io.github.vovak"
//                url = "https://github.com/vovak"
//            }
//            issueManagement {
//                system = "GitHub"
//                url = "https://github.com/vovak/astminer/issues"
//            gradlew}
//            licenses {
//                license {
//                    name = "MIT License"
//                    url = "https://github.com/vovak/astminer/blob/master/LICENSE"
//                    distribution = "repo"
//                }
//            }
//            scm {
//                url = "https://github.com/vovak/astminer"
//                connection = "scm:git:git://github.com/vovak/astminer.git"
//                developerConnection = "scm:git:ssh://git@github.com:vovak/astminer.git"
//            }
//            developers {
//                developer {
//                    name = "Egor Bogomolov"
//                    email = "eobogomolov@edu.hse.ru"
//                }
//                developer {
//                    name = "Vladimir Kovalenko"
//                    email = "V.V.Kovalenko@tudelft.nl"
//                }
//            }
//        }
//    }
//}
//
//model {
//    tasks.generatePomFileForMavenJavaPublication {
//        destination = file("$buildDir/generated-pom.xml")
//    }
//    tasks.publishMavenJavaPublicationToMavenLocal {
//        dependsOn(project.tasks.signArchives)
//    }
//    tasks.publishMavenJavaPublicationToMavenRepository {
//        dependsOn(project.tasks.signArchives)
//    }
//}

publishJar {
    publication {
        artifactId = "astminer"
    }

    bintray {
        username = "vovak"
        repository = "astminer"
        info {
            githubRepo = "vovak/astminer"
            vcsUrl = "https://github.com/vovak/astminer"
            license = "MIT"
            description = "Extract AST and AST-related metrics from source code"
        }
    }
}

