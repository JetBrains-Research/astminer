# Parsers

`astminer` supports multiple parsers for a large wide of programming languages.
Here we describe integrated parsers and their peculiarities.

## ANTLR

ANother Tool for Language Recognition from [antlr.org](https://www.antlr.org).
It provides lexer and parsers for languages that can be generated into Java code.
For now, `astminer` supports Java, Python, JS, and PHP.

## GumTree

[GumTree](https://github.com/GumTreeDiff/gumtree)
framework to work with source code as trees and to compute difference between them.
It also builds language-agnostic representation.
For now, `astminer` supports Java and Python.

### python-parser

You should install python-parser to run GumTree with Python.
There is instruction of how to do it:
1. Download sources from [GitHub](https://github.com/JetBrains-Research/pythonparser/blob/master/)
2. Install dependencies
```shell
pip install -r requirements.txt
```
3. Make python parser script executable
```shell
chmod +x src/main/python/pythonparser/pythonparser_3.py
```
4. Add python-parser to `PATH`
```shell
cp src/main/python/pythonparser/pythonparser_3.py src/main/python/pythonparser/pythonparser
export PATH="<path>/src/main/python/pythonparser/pythonparser:${PATH}"
```

## Fuzzy

Originally [fuzzyc2cpg](https://github.com/ShiftLeftSecurity/fuzzyc2cpg)
and now part of [codepropertygraph](https://github.com/ShiftLeftSecurity/codepropertygraph/).
`astminer`uses it C/C++ parser from that. `G++`required for this parser.

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
