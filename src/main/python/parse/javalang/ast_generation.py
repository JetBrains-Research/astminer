from javalang import ast

IGNORE_NONE_ATTR = True
DUMMY_NONE_PROCESSING_IN_ITERABLE = True


class Node:
    type: str
    value: str
    children: list
    parent: 'Node'

    def __init__(self, node_type, value, children, parent):
        self.type = node_type
        self.value = value
        self.children = children
        self.parent = parent

    def __str__(self):
        return self.type + (f" : {self.value}" if self.value is not None else "")

    def pre_order(self):
        result = [self]
        for child in self.children:
            result.extend(child.pre_order())
        return result

    def pretty_print(self, ind=0):
        for i in range(ind):
            print("-", end="")
        print(self)
        for child in self.children:
            child.pretty_print(ind + 1)

    def is_leaf(self):
        return len(self.children) == 0


def generate_presentable_AST(node, parent=None, last_attribute=None, show_declined=True):
    attributes = {x: getattr(node, x) for x in node.attrs}
    children = []
    for attr, value in attributes.items():
        if isinstance(value, str):
            children.append(process_string_attribute(node, attr, value))
        elif isinstance(value, list) or isinstance(value, set):
            attribute_node = Node(attr, None, None, node)
            attribute_node.children = process_iterable_attributes(attribute_node, attr, value, show_declined)
            if not attribute_node.is_leaf():
                children.append(attribute_node)
        elif isinstance(value, ast.Node):
            children.append(process_node_attribute(node, attr, value, show_declined))
        elif (value is not None or (value is None and not IGNORE_NONE_ATTR)) and show_declined:
            process_declined_attribute(node, attr, value)
    return Node(generate_node_type(node, last_attribute), None, children, parent)


def generate_node_type(node, last_attribute) -> str:
    return node.__class__.__name__


def process_iterable_attributes(node, attr, value, show_declined) -> list:
    new_nodes = []
    for sub_value in value:
        if isinstance(sub_value, str):
            new_nodes.append(process_string_attribute(node, get_singular(attr), sub_value))
        elif isinstance(sub_value, ast.Node):
            new_nodes.append(process_node_attribute(node, attr, sub_value, show_declined))
        elif sub_value is None and DUMMY_NONE_PROCESSING_IN_ITERABLE:
            new_nodes.append(process_string_attribute(node, attr, ""))
        elif show_declined:
            process_declined_attribute(node, attr, value)
    return new_nodes


def get_singular(string):
    return string[:-1]


def process_string_attribute(node, attr, value) -> Node:
    return Node(attr, value, [], node)


def process_node_attribute(node, attr, value, show_declined) -> Node:
    return generate_presentable_AST(node=value, parent=node, last_attribute=attr, show_declined=show_declined)


def process_declined_attribute(node, attr, value):
    print(str(attr) + ":" + str(value))