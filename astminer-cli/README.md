# Astminer usage example / CLI

The project implements a CLI for [Astminer](github.com/vovak/astminer) and serves as a usage example of the library.  

For now the CLI provides two options:

* Parse a project in Java, Python, C, and C++, and save the resulting ASTs;
* Preprocess code in C/C++ to unfold `#define` directives to enable further processing.

This short implementation can be extended to other features of astminer, such as mining of path-based representations and AST features. 
Don't hesitate to contact us for any support with this. 

## Usage

1. Clone the repository
2. Run `./gradlew install` to build the project as an application
3. Run `./cli.sh` to run the CLI

## CLI
The CLI takes `preprocess` or `parse` as the first parameter to choose the task.

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

To add another task, go to [Main.kt](src/main/kotlin/cli/Main.kt) and add a new case to the `when` statement.

To configure extracted data, see `parsing` function in [ProjectParser.kt](src/main/kotlin/cli/ProjectParser.kt).
