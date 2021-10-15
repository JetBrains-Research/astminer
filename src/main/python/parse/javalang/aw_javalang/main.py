import javalang
from aw_javalang.ast_generation import generate_presentable_AST
from aw_javalang.tree_flattening import TreeSerializer
from argparse import ArgumentParser
import json

parser = ArgumentParser()
parser.add_argument("-f", "--file", dest="filename", type=str, help="path to the target file", metavar="FILE")
parser.add_argument("-d", "--declined", dest="show_declined", type=bool, help="Show declined attributes", default=False)


def main():
    args, unknown = parser.parse_known_args()

    if args.show_declined:
        print("Declined attributes:")

    java_file = open(args.filename, "r").read()
    tree = javalang.parse.parse(java_file)
    AST = generate_presentable_AST(tree, show_declined=args.show_declined)

    if args.show_declined:
        print("---------------------------------------------------------------------------")
        print("Generated AST:")

    print(json.dumps(TreeSerializer().get_tree_as_json(AST)))


if __name__ == "__main__":
    main()
