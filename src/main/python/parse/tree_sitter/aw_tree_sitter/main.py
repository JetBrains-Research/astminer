from tree_sitter import Language, Parser
import json
from argparse import ArgumentParser
import os
from typing import List
from aw_tree_sitter.ast import TreeBuilder, TreeAsDict

argument_parser = ArgumentParser()
argument_parser.add_argument("-f", "--file", dest="filename", type=str, help="Path to the target file", metavar="FILE")
argument_parser.add_argument(
    "-b", "--build", dest="grammars", nargs="+", help="Path to grammar folders. For example: -b tree-sitter-java"
)
argument_parser.add_argument("-l", "--language", dest="language", help="Target language. For example: -l java")


def build_libraries(languages: List[str], path):
    # Forcing tree sitter to create new library
    if os.path.isfile(path):
        os.remove(path)
    Language.build_library(
        # Store the library in the `build` directory
        path,
        # Include one or more languages
        languages,
    )


class TreeSitterLauncher:
    _parser: Parser

    def __init__(self, language, library_path):
        grammar = Language(library_path, language)
        self._parser = Parser()
        self._parser.set_language(grammar)

    def _get_code_bytes(self, filepath: str) -> bytes:
        file = open(filepath, "r")
        return bytes(file.read(), "utf-8")

    def parse_file(self, filepath: str) -> TreeAsDict:
        code_bytes = self._get_code_bytes(filepath)
        tree_sitter_tree = self._parser.parse(code_bytes)
        cursor = tree_sitter_tree.walk()
        return TreeBuilder(cursor, code_bytes).get_tree_as_dict()


def main():
    args, unknown = argument_parser.parse_known_args()
    dirname = os.path.dirname(__file__)
    library_path = os.path.join(dirname, "./build/my-languages.so")

    if args.grammars is not None:
        build_libraries(args.grammars, library_path)

    if args.filename is not None:
        tree = TreeSitterLauncher(args.language, library_path).parse_file(args.filename)
        print(json.dumps(tree))


if __name__ == "__main__":
    main()
