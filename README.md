[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
![astminer version](https://img.shields.io/badge/astminer-v0.6.4-blue)

# `astminer`
A library for mining of [path-based representations of code](https://arxiv.org/pdf/1803.09544.pdf) and more, supported by the [Machine Learning Methods for Software Engineering](https://research.jetbrains.org/groups/ml_methods) group at [JetBrains Research](https://research.jetbrains.org).

Supported languages of the input:

- [x] Java
- [x] Python
- [x] C/C++
- [x] Javascript

### Version history

See [changelog](changelog.md)

## About
`astminer` was first implemented as a part of pipeline in the [code style extraction project](https://arxiv.org/abs/2002.03997) and later converted into a reusable tool.

Currently, it supports extraction of:
* Path-based representations of files
* Path-based representations of methods
* Raw ASTs

Supported languages are Java, Python, C/C++, but it is designed to be very easily extensible.

For the output format, see [the section below](#output-format).

## Usage
There are two ways to use `astminer`.

- [As a standalone CLI tool](#using-astminer-cli) with pre-implemented logic for common processing and mining tasks 
- [Integrated](#using-astminer-as-a-dependency) into your Kotlin/Java mining pipelines as a Gradle dependency.

### Using `astminer` CLI
#### Building or installing `astminer` CLI
`astminer` CLI can be either built from sources or installed in a pre-built Docker image.

##### Building locally
`./cli.sh` will do the job for you by triggering a Gradle build on the first run.

##### Installing the Docker image
The C++ parser in `astminer` relies on `g++`. To avoid misconfiguration with this and likely other future external dependencies, you can use it from a Docker container.

Install the image with the last release by pulling it from Docker Hub:
```shell
docker pull voudy/astminer
```
To rebuild the image locally, run
```shell
docker build -t voudy/astminer .
```

#### Running `astminer` CLI
Run
```shell
./cli.sh optionName parameters
```
Where `optionName` is one of the following options:

#### Preprocess

Run preprocessing on C/C++ project to unfold `#define` directives. 
In other tasks, if you feed C/C++ file with macroses, they will be dropped as well as their appearances in code. 
```shell script
./cli.sh preprocess --project path/to/project --output path/to/preprocessedProject
```
#### Parse

Extract ASTs from all the files in supported languages.
```shell script
./cli.sh parse --lang py,java,c,cpp,js --project path/to/project --output path/to/result --storage dot
```

#### PathContexts

Extract path contexts from all the files in supported languages and store in form `fileName triplesOfPathContexts`.
```shell script
./cli.sh pathContexts --lang py,java,c,cpp,js --project path/to/project --output path/to/results --maxL L --maxW W --maxContexts C --maxTokens T --maxPaths P
```

#### Code2vec

Extract data suitable as input for [code2vec](https://github.com/tech-srl/code2vec) model.
Parse all files written in specified language into ASTs, split into methods, and store in form `method|name triplesOfPathContexts`.
```shell script
./cli.sh code2vec --lang py,java,c,cpp,js --project path/to/project --output path/to/results --maxL L --maxW W --maxContexts C --maxTokens T --maxPaths P  --split-tokens --granularity method
```

### Using `astminer` as a dependency

#### Import

`astminer` is available in the JetBrains Space package repository. You can add the dependency in your `build.gradle` file:
```
repositories {
    maven {
        url "https://packages.jetbrains.team/maven/p/astminer/astminer"
    }
}

dependencies {
    compile 'io.github.vovak:astminer:<VERSION>'
}
```

If you use `build.gradle.kts`:
```
repositories {
    maven(url = uri("https://packages.jetbrains.team/maven/p/astminer/astminer"))
}

dependencies {
    compile("io.github.vovak", "astminer", <VERSION>)
}
```

#### Local development

To use a specific version of the library, navigate to the required branch and build local version of `astminer`:
```shell
./gradlew publishToMavenLocal
```
After that, add `mavenLocal()` into the `repositories` section in your gradle configuration.

#### Examples

If you want to use `astminer` as a library in your Java/Kotlin based data mining tool, check the following examples:

* A few [simple usage examples](src/main/kotlin/astminer/examples) can be run with `./gradlew run`.

* A somewhat more verbose [example of usage in Java](src/main/kotlin/astminer/examples/AllJavaFiles.kt) is available as well. 

Please consider trying Kotlin for your data mining pipelines: from our experience, it is much better suited for data collection and transformation instruments.

### Output format

For path-based representations, `astminer` supports two output formats. In both of them, we store 4 `.csv` files:
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
See [FuzzyCppParser](src/main/kotlin/astminer/parse/fuzzy/cpp/FuzzyCppParser.kt) for an example of a wrapper.

## Contribution
We believe that `astminer` could find use beyond our own mining tasks.

Please help make `astminer` easier to use by sharing your use cases. Pull requests are welcome as well.
Support for other languages and documentation are the key areas of improvement.

## Citing astminer
A [paper](https://zenodo.org/record/2595271) dedicated to `astminer` (more precisely, to its older version [PathMiner](https://github.com/vovak/astminer/tree/pathminer)) was presented at [MSR'19](https://2019.msrconf.org/). 
If you use `astminer` in your academic work, please cite it.
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
