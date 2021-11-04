from tree_sitter import TreeCursor
from typing import Optional, TypedDict, List

Position = TypedDict("Position", {"l": int, "c": int})
NodeRange = TypedDict("NodeRange", {"start": Position, "end": Position})
NodeAsDict = TypedDict(
    "NodeAsDict", {"token": Optional[str], "nodeType": str, "range": NodeRange, "children": List[int]}
)
TreeAsDict = TypedDict("TreeAsDict", {"tree": List[NodeAsDict]})


class TreeBuilder:
    _cursor: TreeCursor
    _file_bytes: bytes

    def __init__(self, cursor: TreeCursor, file_bytes: bytes):
        self._cursor = cursor
        self._file_bytes = file_bytes

    def _get_current_node_range(self) -> NodeRange:
        node = self._cursor.node
        start = node.start_point
        end = node.end_point
        return {
            "start": {"l": start[0] + 1, "c": start[1] + 1},
            "end": {"l": end[0] + 1, "c": end[1] + 1}
        }

    def _get_current_node_as_dict(self) -> NodeAsDict:
        node_type = self._cursor.node.type
        node_range = self._get_current_node_range()

        if len(self._cursor.node.children) == 0:
            node_value_bytes = self._file_bytes[self._cursor.node.start_byte : self._cursor.node.end_byte]
            node_value: Optional[str] = node_value_bytes.decode("utf-8")
        else:
            node_value = None

        return {"token": node_value, "nodeType": node_type, "range": node_range, "children": []}

    def get_tree_as_dict(self) -> TreeAsDict:
        depth = 0
        tree = []
        last_node_by_depth = {}
        index = 0
        while True:
            # creating new node
            node = self._get_current_node_as_dict()
            last_node_by_depth[depth] = node
            if depth > 0:
                last_node_by_depth[depth - 1]["children"].append(index)
            tree.append(node)
            index += 1
            # going deeper if we can
            if self._cursor.goto_first_child():
                depth += 1
                continue
            # trying to go right
            if self._cursor.goto_next_sibling():
                continue
            # if we are in the most deep right node
            # traverse up to find node with right sibling
            found_right_sibling = False
            while self._cursor.goto_parent():
                depth -= 1
                if self._cursor.goto_next_sibling():
                    found_right_sibling = True
                    break
            if found_right_sibling:
                continue
            # if we couldn't find any new node to traverse
            # end while loop
            break
        return {"tree": tree}
