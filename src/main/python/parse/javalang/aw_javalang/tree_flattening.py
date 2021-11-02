from typing import Optional, List, Tuple
from aw_javalang.ast_generation import Node, Range
from dataclasses import dataclass


@dataclass
class EnumeratedTree:
    tree: List["EnumeratedNode"]


@dataclass
class EnumeratedNode:
    token: Optional[str]
    nodeType: str
    children: List[int]
    range: Range

class TreeSerializer:
    def __init__(self):
        self._current_id = 0

    def _enumerate_tree(self, node) -> Tuple[List["EnumeratedNode"], int]:
        enumerated_root = EnumeratedNode(node.value, node.type, [], node.node_range)
        root_id = self._current_id
        self._current_id += 1
        enumerated_tree = [enumerated_root]
        for child in node.children:
            subtree, subtree_root_id = self._enumerate_tree(child)
            enumerated_root.children.append(subtree_root_id)
            enumerated_tree.extend(subtree)
        return enumerated_tree, root_id

    def get_enumerated_tree(self, root: Node) -> EnumeratedTree:
        return EnumeratedTree(self._enumerate_tree(root)[0])
