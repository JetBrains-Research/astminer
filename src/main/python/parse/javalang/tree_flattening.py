from typing import TypedDict, Optional, List
from ast_generation import Node

NodeAsDict = TypedDict("NodeAsDict", {"token": Optional[str], "nodeType": str, "children": List[int]})
TreeAsDict = TypedDict("TreeAsDict", {"tree": List[NodeAsDict]})


class TreeSerializer:
    _current_id: int

    def __init__(self):
        self._current_id = 0

    class EnumeratedNode(Node):
        node_id: int

        def __init__(self, node: Node, node_id: int, children: list):
            super().__init__(node.type, node.value, children, node.parent)
            self.node = node
            self.node_id = node_id

    def _enumerate_node(self, node: Node) -> EnumeratedNode:
        self._current_id += 1
        return self.EnumeratedNode(node, self._current_id, [self._enumerate_node(n) for n in node.children])

    def _get_node_as_json(self, e_node: EnumeratedNode) -> NodeAsDict:
        return {
            "token": e_node.value,
            "nodeType": e_node.type,
            "children": [c.node_id for c in e_node.children],
        }

    def get_tree_as_json(self, tree) -> TreeAsDict:
        self._current_id = 0
        enumerated_tree = self._enumerate_node(tree)
        return {"tree": [self._get_node_as_json(n) for n in enumerated_tree.pre_order()]}
