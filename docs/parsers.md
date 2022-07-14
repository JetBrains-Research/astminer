# Parsers

`astminer` supports multiple parsers for various programming languages.
Here we describe the integrated parsers and their peculiarities.

## ANTLR

[ANTLR](https://www.antlr.org) provides an infrastructure to generate lexers and parsers for languages based on grammars.
For now, `astminer` supports ANTLR-based parsers for Java, Python, JS, and PHP.

## GumTree

[GumTree](https://github.com/GumTreeDiff/gumtree)
is a framework to work with source code as trees and to compute the differences between the trees in different versions of code.
It also builds language-agnostic representations of code.
For now, `astminer` supports GumTree-based parsers for Java and Python.

### python-parser

Running GumTree with Python requires `python-parser`.
You can set it up as follows:
1. Download the sources from [GitHub](https://github.com/JetBrains-Research/pythonparser/blob/master/)
2. Install the dependencies
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

### srcML backend

A lot of languages in gumtree additionally supported with srcML backend, so `astminer`
uses gumtree with srcML as a whole new parser.
Running it requires installing `srcML`: https://www.srcml.org/

If you have any problems with installation check the Dockerfile in the project root

## Fuzzy

Originally [fuzzyc2cpg](https://github.com/ShiftLeftSecurity/fuzzyc2cpg), Fuzzy is
now part of [codepropertygraph](https://github.com/ShiftLeftSecurity/codepropertygraph/).
`astminer`uses it to parse C/C++ code. `g++` is required for this parser.

## JavaParser

Parser for Java which is used to get trees for Code2seq and Code2vec models, and is also 
used in many other studies to collect trees and work with them.
When working with Javaparser `astminer` implements an algorithm similar to the algorithm in
the [JavaExtractor module](https://github.com/tech-srl/code2vec/tree/master/JavaExtractor)
in the Code2Vec repository to get similar trees.

## JavaLang parser
Java parser written in pure python. In order to work with it, you need to install our
self-written translator package that will reformat javalang inner AST into json AST that `astminer`
can understand.
To install this package simply run in the root of the project:
```
pip install src/main/python/parse/javalang
```

## Other languages and parsers

Support for a new programming language can be implemented in a few simple steps.

If there is an ANTLR grammar for the language:
1. Add the corresponding [ANTLR4 grammar file](https://github.com/antlr/grammars-v4) to the `antlr` directory.
2. Run the `generateGrammarSource` Gradle task to generate the parser.
3. Implement a small wrapper around the generated parser.
   See [JavaParser](https://github.com/JetBrains-Research/astminer/blob/5692fa522863b76b0fe2260a075cdba402fe1122/src/main/kotlin/astminer/parse/antlr/java/JavaParser.kt), [AntlrJavaParsingResultFactory](https://github.com/JetBrains-Research/astminer/blob/5692fa522863b76b0fe2260a075cdba402fe1122/src/main/kotlin/astminer/parse/antlr/AntlrParsingResult.kt#L14), and [`getParsingResultFactory`](https://github.com/JetBrains-Research/astminer/blob/5692fa522863b76b0fe2260a075cdba402fe1122/src/main/kotlin/astminer/parse/factory.kt#L19) for an example of building such a wrapper and integrating it in the pipeline.

If the language has a parsing tool that is available as a Java library:
1. Add the library as a dependency in [build.gradle.kts](/build.gradle.kts).
2. Implement a wrapper for the parsing tool.
   See [FuzzyCppParser](src/main/kotlin/astminer/parse/fuzzy/cpp/FuzzyCppParser.kt) for an example of such a wrapper.
