[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![CircleCI](https://circleci.com/gh/JetBrains-Research/astminer.svg?style=svg)](https://circleci.com/gh/JetBrains-Research/astminer)
[ ![Download](https://api.bintray.com/packages/egor-bogomolov/astminer/astminer-cli/images/download.svg?version=0.3) ](https://bintray.com/egor-bogomolov/astminer/astminer-cli/0.3/link)

# Astminer usage example / CLI

The project implements a CLI for [Astminer](github.com/vovak/astminer) and serves as a usage example of the library.  

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

## Usage

The CLI is available as runnable jar.

1. Download the jar from [bintray](https://dl.bintray.com/egor-bogomolov/astminer/io/github/vovak/astminer/astminer-cli/0.3/astminer-cli-0.3-all.jar)
2. Rename it to a shorter name like `cli.jar` 
3. Run it with `java -jar cli.jar optionName parameters`, where `optionName` is one of the following options:

#### Preprocess

Run preprocessing on C/C++ project to unfold `#define` directives. 
In other tasks, if you feed C/C++ file with macroses, they will be dropped as well as their appearances in code. 
```shell script
java -jar cli.jar preprocess --project path/to/project --output path/to/preprocessedProject
```
#### Parse

Extract ASTs from all the files in supported languages.
```shell script
java -jar cli.jar parse --lang py,java,c,cpp --project path/to/project --output path/to/result --storage dot
```

#### PathContexts

Extract path contexts from all the files in supported languages and store in form `fileName triplesOfPathContexts`.
```shell script
java -jar cli.jar pathContexts --lang py,java,c,cpp --project path/to/project --output path/to/results --maxH H --maxW W --maxContexts C --maxTokens T --maxPaths P
```

#### Code2vec

Extract data suitable as input for [code2vec](https://github.com/tech-srl/code2vec) model.
Parse all files written in specified language into ASTs, split into methods, and store in form `method|name triplesOfPathContexts`.
```shell script
java -jar cli.jar code2vec --lang py,java,c,cpp --project path/to/project --output path/to/results --maxH H --maxW W --maxContexts C --maxTokens T --maxPaths P
```

## Extending the CLI

1. Clone the repository and go to the `astminer-cli` folder
2. Update the project to suit your needs:
    1. To add another task for the jar, create an extension of `CliktCommand()` class 
    (see [ProjectParser](src/main/kotlin/cli/ProjectParser.kt) for an example) and link it in [Main.kt](src/main/kotlin/cli/Main.kt)
    2. To modify existing tasks (e.g., parse only files with specific names), update code of corresponding classes
3. Run `./gradlew shadowJar` to create a runnable jar with all the dependencies
4. Created jar is located in `build/shadow/cli-versionNumber.jar`
5. Run the jar explicitly or use `./cli.sh` for short
