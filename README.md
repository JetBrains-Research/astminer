[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
![astminer version](https://img.shields.io/badge/astminer-v0.9.0-blue)

# `astminer`
A library for mining [path-based representations of code](https://arxiv.org/pdf/1803.09544.pdf) and more
supported by the
[Machine Learning Methods for Software Engineering](https://research.jetbrains.org/groups/ml_methods)
group at [JetBrains Research](https://research.jetbrains.org).

Supported languages of the input:

|         | Java | Python | C/C++ | JavaScript | PHP |
|---------|------|--------|-------|------------|-----|
| ANTLR   | ✅    | ✅      |       | ✅          | ✅   |
| GumTree | ✅ (JDT and srcML)    | ✅      |       |            |     |
| Fuzzy   |      |        | ✅     |            |     |
| JavaParser | ✅ |        |        |             |      |
| TreeSitter | ✅ |       |        |            |     |
| JavaLang| ✅    |       |        |           |      |



## About
`astminer` lets you create an end-to-end pipeline to process code for machine learning models.

Currently, it supports the extraction of:
* Path-based representations of files/methods
* Raw ASTs of files/methods

`astminer` was first implemented as a part of the pipeline in the [code style extraction project](https://arxiv.org/abs/2002.03997) and later converted into a reusable tool.
It is designed to be easily extensible to new languages.

`astminer` allows you to convert source code cloned from VCSs to formats suitable for training.
To achieve that, `astminer` incorporates the following processing modules:
- [Filters](./docs/filters.md) to remove redundant samples from data.
- [Label extractors](./docs/label_extractors.md) to create a label for each tree.
- [Storages](./docs/storages.md) to define the storage format.

## Usage
There are two ways to use `astminer`:

- [As a standalone CLI tool](#using-astminer-cli) with a pre-implemented logic for common processing and mining tasks.
- [Integrated](#using-astminer-as-a-dependency) into your Kotlin/Java mining pipelines as a Gradle dependency.

### Using `astminer` CLI

1. [Build the CLI](./docs/cli.md#Getting+started) from the sources.

2. Prepare your inputs and [configure](./docs/cli.md#Configuration) pipeline options. For config examples, see the [configs](./configs) directory. 

3. To run the CLI, pass the config to the shell script:
    ```shell
    ./cli.sh <path-to-YAML-config>
    ```
Alternatively, you can run the tool inside the [Docker image](./docs/cli.md#Docker).

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
    implementation 'io.github.vovak:astminer:<VERSION>'
}
```

If you use `build.gradle.kts`:
```
repositories {
    maven(url = uri("https://packages.jetbrains.team/maven/p/astminer/astminer"))
}

dependencies {
    implementation("io.github.vovak:astminer:<VERSION>")
}
```

#### Local development

To use a specific version of the library, navigate to the required branch and build a local version of `astminer`:
```shell
./gradlew publishToMavenLocal
```
After that add `mavenLocal()` into the `repositories` section in your gradle configuration.

#### Examples

If you want to use `astminer` as a library in your Java/Kotlin-based data mining tool, check the following usage examples:

* Simple standalone [example scripts](src/examples) in Java and Kotlin with calling to different APIs of `astminer`.
* [psiminer](https://github.com/JetBrains-Research/psiminer), a mining tool that uses `astminer` to extract paths from PSI trees. See the [code2seq storage implementation] (https://github.com/JetBrains-Research/psiminer/blob/master/psiminer-core/src/main/kotlin/storage/paths/Code2SeqStorage.kt).

Please consider trying Kotlin for your data mining pipelines: from our experience, it is much better suited for data collection and transformation instruments than Java.

## Contribution

We believe that `astminer` can find use beyond our own mining tasks.

Please help make `astminer` easier to use by sharing your use cases. Pull requests are welcome as well.
Support for other languages and documentation are the key areas of improvement.

## Citing `astminer`

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
