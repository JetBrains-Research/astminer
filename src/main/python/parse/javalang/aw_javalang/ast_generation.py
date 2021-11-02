from typing import Optional, Union, List, Any
from javalang.ast import Node as JavaLangNode
from dataclasses import dataclass

IGNORE_NONE_ATTR = True
DUMMY_NONE_PROCESSING_IN_ITERABLE = True


@dataclass
class Position:
    l: int
    c: int


@dataclass
class Range:
    start: Position
    end: Position


def node_range(start: List[int], end: List[int]):
    return Range(Position(start[0], start[1]), Position(end[0], end[1]))


@dataclass
class Node:
    type: str
    value: Optional[str]
    children: List["Node"]
    node_range: Range

    def __str__(self):
        return self.type + (f" : {self.value}" if self.value is not None else "")

    def pre_order(self) -> list:
        result = [self]
        for child in self.children:
            result.extend(child.pre_order())
        return result

    def pretty_print(self, ind=0, ind_len=2):
        print("-" * ind_len * ind)
        print(self)
        for child in self.children:
            child.pretty_print(ind + 1)

    def is_leaf(self) -> bool:
        return len(self.children) == 0


def generate_presentable_AST(node: JavaLangNode, show_declined: bool = True) -> Node:
    attributes = {x: getattr(node, x) for x in node.attrs}
    children = []
    for attr, value in attributes.items():
        if isinstance(value, str):
            children.append(process_string_attribute(node, attr, value))
        elif isinstance(value, list) or isinstance(value, set):
            attribute_node = Node(attr, None, [], node_range([-1, -1], [-1, -1]))
            attribute_node.children = process_iterable_attributes(attribute_node, attr, value, show_declined)
            if not attribute_node.is_leaf():
                children.append(attribute_node)
        elif isinstance(value, JavaLangNode):
            children.append(process_node_attribute(node, value, show_declined))
        elif (value is not None or (value is None and not IGNORE_NONE_ATTR)) and show_declined:
            process_declined_attribute(attr, value)
    node_pos = node.position
    if node_pos is None:
        start = [-1, -1]
    else:
        start = [node_pos.line, node_pos.column]
    return Node(type=generate_node_type(node), value=None, children=children,
                node_range=node_range(start, [-1, -1]))


def generate_node_type(node: JavaLangNode) -> str:
    return node.__class__.__name__


def process_iterable_attributes(node: Node, attr: str, value: Union[list, set], show_declined: bool) -> List[Node]:
    new_nodes = []
    for sub_value in value:
        if isinstance(sub_value, str):
            new_nodes.append(process_string_attribute(node, get_singular(attr), sub_value))
        elif isinstance(sub_value, JavaLangNode):
            new_nodes.append(process_node_attribute(node, sub_value, show_declined))
        elif sub_value is None and DUMMY_NONE_PROCESSING_IN_ITERABLE:
            new_nodes.append(process_string_attribute(node, attr, ""))
        elif show_declined:
            process_declined_attribute(attr, value)
    return new_nodes


def get_singular(string: str) -> str:
    return string[:-1]


def process_string_attribute(node: Node, attr: str, value: str) -> Node:
    return Node(attr, value, [], node_range([-1, -1], [-1, -1]))


def process_node_attribute(node: Node, value: JavaLangNode, show_declined: bool) -> Node:
    return generate_presentable_AST(node=value, show_declined=show_declined)


def process_declined_attribute(attr: str, value: Any):
    print(str(attr) + ":" + str(value))
