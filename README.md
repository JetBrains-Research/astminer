[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![CircleCI](https://circleci.com/gh/JetBrains-Research/astminer.svg?style=svg)](https://circleci.com/gh/JetBrains-Research/astminer)
[ ![Download](https://api.bintray.com/packages/egor-bogomolov/astminer/astminer/images/download.svg) ](https://bintray.com/egor-bogomolov/astminer/astminer/_latestVersion)
[ ![Download](https://api.bintray.com/packages/egor-bogomolov/astminer/astminer-cli/images/download.svg?version=0.3) ](https://bintray.com/egor-bogomolov/astminer/astminer-cli/0.3/link)

# astminer
A library for mining of [path-based representations of code](https://arxiv.org/pdf/1803.09544.pdf) and more, supported by the [Machine Learning Methods for Software Engineering](https://research.jetbrains.org/groups/ml_methods) group at [JetBrains Research](https://research.jetbrains.org).

Supported languages of the input:

- [x] Java
- [x] Python
- [x] C/C++
- [x] Javascript (beta) (see [issue](https://github.com/vovak/astminer/issues/22))

### Version history

#### 0.5

* Beta of Javascript support
* Storage of ASTs in [DOT format](https://www.graphviz.org/doc/info/lang.html)
* Minor fixes

#### 0.4

* Support of code2vec output format
* Extraction of ASTs and path-based representations of individual methods
* Extraction of data for the task of method name prediction ([code2vec paper](https://arxiv.org/abs/1803.09473))

#### 0.3

* Support of C/C++ via [FuzzyC2CPG parser](https://github.com/ShiftLeftSecurity/fuzzyc2cpg)

#### 0.2

* Mining of ASTs

#### 0.1
* astminer is available via Maven Central
* Support of Java and Python
* Mining of [path-based representations of code](https://arxiv.org/pdf/1803.09544.pdf)


## About
Astminer was first implemented as a part of pipeline in the [the code style extraction project](https://arxiv.org/abs/2002.03997) and later converted into a reusable tool.

Currently it supports extraction of:
* Path-based representations of files
* Path-based representations of methods
* Raw ASTs

Supported languages are Java, Python, C/C++, but it is designed to be very easily extensible.

For the output format, see the section below.

## Usage

### Use as CLI

See a [subfolder](/astminer-cli) containing CLI and its description. It can be extended if needed.

### Integrate in your mining pipeline

#### Import

Astminer is available in [Bintray repo](https://bintray.com/egor-bogomolov/astminer/astminer). You can add the dependency in your `build.gradle` file:
```
repositories {
    maven {
        url  "https://dl.bintray.com/egor-bogomolov/astminer" 
    }
}

dependencies {
    compile 'io.github.vovak.astminer:astminer:0.5'
}
```

If you use `build.gradle.kts`:
```
repositories {
    maven(url = "https://dl.bintray.com/egor-bogomolov/astminer/")
}

dependencies {
    compile("io.github.vovak.astminer", "astminer", "0.5")
}
```

#### Examples

If you want to use astminer as a library in your Java/Kotlin based data mining tool, check the following examples:

* A few [simple usage examples](src/main/kotlin/astminer/examples) can be run with `./gradlew run`.

* A somewhat more verbose [example of usage in Java](src/main/kotlin/astminer/examples/AllJavaFiles.kt) is available as well. 

Please consider trying Kotlin for your data mining pipelines: from our experience, it is much better suited for data collection and transformation instruments.

### Output format

For path-based representations, astminer supports two output formats. In both of them, we store 4 `.csv` files:
1. `node_types.csv` contains numeric ids and corresponding node types with directions (up/down, as described in [paper](https://arxiv.org/pdf/1803.09544.pdf));
2. `tokens.csv` contains numeric ids and corresponding tokens;
3. `paths.csv` contains numeric ids and AST paths in form of space-separated sequences of node type ids;
4. `path_contexts.csv` contains labels and sequences of path contexts (triples of two tokens and a path between them).

If the replica of [code2vec](https://github.com/tech-srl/code2vec) format is used, each line in `path_contexts.csv` starts with a label, 
then it contains a sequence of space-separated triples. Each triple contains start token id, path id, end token id, separated with commas.

If csv format is used, each line in `path_contexts.csv` contains label, then comma, then a sequence of `;`-separated triples.
Each triple contains start token id, path id, end token id, separated with spaces.

## Other languages

Support for a new programming language can be implemented in a few simple steps.

If there is an ANTLR grammar for the language:
1. Add the corresponding [ANTLR4 grammar file](https://github.com/antlr/grammars-v4) to the `antlr` directory;
2. Run the `generateGrammarSource` Gradle task to generate the parser;
3. Implement a small wrapper around the generated parser.
See [JavaParser](src/main/kotlin/astminer/parse/antlr/java/JavaParser.kt) or [PythonParser](src/main/kotlin/astminer/parse/antlr/python/PythonParser.kt) for an example of a wrapper.

If the language has a parsing tool that is available as Java library:
1. Add the library as a dependency in [build.gradle.kts](/build.gradle.kts);
2. Implement a wrapper for the parsing tool.
See [FuzzyCppParser](src/main/kotlin/astminer/parse/cpp/FuzzyCppParser.kt) for an example of a wrapper.

## Contribution
We believe that astminer could find use beyond our own mining tasks.

Please help make astminer easier to use by sharing your use cases. Pull requests are welcome as well.
Support for other languages and documentation are the key areas of improvement.

## Citing astminer
A [paper](https://zenodo.org/record/2595271) dedicated to astminer (more precisely, to its older version [PathMiner](https://github.com/vovak/astminer/tree/pathminer)) was presented at [MSR'19](https://2019.msrconf.org/). 
If you use astminer in your academic work, please consider citing it.
```
@inproceedings{kovalenko2019pathminer,
  title={PathMiner: a library for mining of path-based representations of code},
  author={Kovalenko, Vladimir and Bogomolov, Egor and Bryksin, Timofey and Bacchelli, Alberto},
  booktitle={Proceedings of the 16th International Conference on Mining Software Repositories},
  pages={13--17},
  year={2019},
  organization={IEEE Press}
}
```
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
