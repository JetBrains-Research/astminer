# Parsers

`astminer` supports multiple parsers for various programming languages.
Here we describe the integrated parsers and their peculiarities.

## ANTLR

[ANTLR](https://www.antlr.org) provides an infrastructure to generate lexers and parsers for languages based on grammars.
For now, `astminer` supports ANTLR-based parsers for Java, Python, JS, and PHP.

## GumTree

[GumTree](https://github.com/GumTreeDiff/gumtree)
is a framework to work with source code as trees and to compute differences of trees between different versions of code.
It also builds language-agnostic representations of code.
For now, `astminer` supports GumTree-based parsers for Java and Python.

### python-parser

Running GumTree with Python requires `python-parser`.
It can be set up through the following steps:
1. Download sources from [GitHub](https://github.com/JetBrains-Research/pythonparser/blob/master/)
2. Install dependencies
```shell
pip install -r requirements.txt
```
3. Make the `python-parser` script executable
```shell
chmod +x src/main/python/pythonparser/pythonparser_3.py
```
4. Add python-parser to `PATH`
```shell
cp src/main/python/pythonparser/pythonparser_3.py src/main/python/pythonparser/pythonparser
export PATH="<path>/src/main/python/pythonparser/pythonparser:${PATH}"
```

## Fuzzy

Originally [fuzzyc2cpg](https://github.com/ShiftLeftSecurity/fuzzyc2cpg), Fuzzy is
now part of [codepropertygraph](https://github.com/ShiftLeftSecurity/codepropertygraph/).
`astminer`uses it to parse C/C++ code. `g++` is required for this parser.

## Other languages and parsers

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
