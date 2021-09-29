from tree_sitter import Language, Parser
import json
from argparse import ArgumentParser
import os
from typing import List
from ast import TreeBuilder

argument_parser = ArgumentParser()
argument_parser.add_argument("-f", "--file", dest="filename", type=str, help="path to the target file", metavar="FILE")
argument_parser.add_argument("-b", "--build", dest="grammars", nargs="+")


def build_libraries(languages: List[str]):
    Language.build_library(
        # Store the library in the `build` directory
        "build/my-languages.so",
        # Include one or more languages
        languages,
    )


def setup_java_parser() -> Parser:
    dirname = os.path.dirname(__file__)
    filename = os.path.join(dirname, "build/my-languages.so")
    java_grammar = Language(filename, "java")
    java_parser = Parser()
    java_parser.set_language(java_grammar)
    return java_parser


def get_code_bytes(filepath: str) -> bytes:
    example = open(filepath, "r")
    return bytes(example.read(), "utf-8")


def parse_file(filename: str):
    parser = setup_java_parser()
    code_bytes = get_code_bytes(filename)
    tree = parser.parse(code_bytes)
    cursor = tree.walk()
    tree_as_dict = TreeBuilder(cursor, code_bytes).get_tree_as_dict()
    print(json.dumps(tree_as_dict))


def main():
    args, unknown = argument_parser.parse_known_args()

    if args.grammars is not None:
        build_libraries(args.grammars)

    if args.filename is not None:
        parse_file(args.filename)


if __name__ == "__main__":
    main()
