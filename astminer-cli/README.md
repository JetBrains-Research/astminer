# Astminer usage example / CLI

The project implements a CLI for [Astminer](github.com/vovak/astminer).  

For now it has two options:

* Parsing projects in Java, Python, C, and C++ and saving extracted data
* Preprocess code in C/C++ to unfold `#define` directives 

We hope, that the implementation is short and clear enough to be extended to other tasks available in Astminer 
(e.g., mining of path-based representations or AST features). 
If it's not the case, don't hesitate to contact us for any support! 

## Usage

1. Download the project
2. Run `./gradlew install` to build the project as an application
3. Call `./build/install/astminer-examples/bin/astminer-examples` or `./cli.sh` to run the CLI

## CLI
CLI takes `preprocess` or `parse` as the first parameter to choose the task.

To get help and information about other parameters, run:
```$xslt
./cli.sh preprocess --help
./cli.sh parse --help
```

To evaluate CLI on test data, run:
```$xslt
./cli.sh parse --project data/ --output extractedData/ --lang java,py
./cli.sh preprocess --project data/ --output preprocessedData/
./cli.sh parse --project preprocessedData/ --output extractedData/ --lang cpp
```

## Extending the CLI

To add another task, go to [Main.kt](src/main/kotlin/cli/Main.kt) and add another case to _when_ statement.

To configure extracted data see `parsing` function in [ProjectParser.kt](src/main/kotlin/cli/ProjectParser.kt).
