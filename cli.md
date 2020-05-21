[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![CircleCI](https://circleci.com/gh/JetBrains-Research/astminer.svg?style=svg)](https://circleci.com/gh/JetBrains-Research/astminer)

# Astminer usage example / CLI

The project implements a CLI for [astminer](github.com/vovak/astminer) and serves as a usage example for the library.  

For now the CLI provides four options:

* Extract data (method names and path contexts) suitable as input for [code2vec](https://github.com/tech-srl/code2vec);
* Parse a project in one of supported languages and save the extracted ASTs;
* Extract path contexts from the project files and save them in code2vec format;
* Preprocess code in C/C++ to unfold `#define` directives to enable further processing.

### Version history

#### Coming up in 0.4

* Extraction of path-based representations at method level
* Support of Javascript

#### 0.3

* Extraction of path-based representations
    * For now it works only at file level
* Compatibility with [code2vec](https://github.com/tech-srl/code2vec) model (see [code2vec section](#code2vec))
* New features in AST parsing:
    * Saving in [DOT format](https://www.graphviz.org/doc/info/lang.html)
    * Selection of granularity level (`file` or `method`)
    * You can pass `--split-token` flag to split tokens into pipe-separated sub-tokens
    * You can pass `--hide-method-name` to replace names of methods with dummy `METHOD_NAME` token

#### 0.2

* Parsing ASTs for Java, Python, C/C++
* Preprocessing for C/C++

#### 0.1

* Weird alpha-release


## Supported languages

* Python &ndash; supported via parser generated from [ANTLR grammar](https://github.com/antlr/grammars-v4/tree/master/python3).
* Java &ndash; supported via [GumTree](https://github.com/GumTreeDiff/gumtree) and [ANTLR Java8 grammar](https://github.com/antlr/grammars-v4/tree/master/java8).
* C and C++ &ndash; supported via [ShiftLeft CPG constructor](https://github.com/ShiftLeftSecurity/codepropertygraph).
It does not work properly with macroses (`#define` directives), thus, they should be substituted before parsing the project.
To do so, we provide a `preprocess` option for the CLI.

## Requirements

1. `java` to run jar 
2. `g++` for preprocessing, **only for C/C++**

## Extending the CLI

1. Clone the repository
2. If you want to update the astminer library:
    1. Make changes to astminer (located in the root of this repository)
    2. Build astminer in the root folder with `./gradlew shadowJar`
3. Move to `astminer-cli`
4. If you want to update the CLI:
    1. To add another task for the jar, create an extension of `CliktCommand()` class 
    (see [ProjectParser](src/main/kotlin/cli/ProjectParser.kt) for an example) and link it in [Main.kt](src/main/kotlin/cli/Main.kt)
    2. To modify existing tasks (e.g., parse only files with specific names), update code of corresponding classes
5. Run `./gradlew shadowJar` to create a runnable jar with all the dependencies
6. Created jar is located in `build/shadow/cli-versionNumber.jar`
7. Run the jar explicitly or use `./cli.sh` for short
