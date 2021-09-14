class TreeEnumerator:
    current_id: int

    def __init__(self):
        self.current_id = 0

    def enumerate_tree(self, node):
        node.id = self.current_id
        self.current_id += 1
        for child in node.children:
            self.enumerate_tree(child)


def get_node_as_json(node) -> dict:
    return {
        "token": node.value,
        "nodeType": node.type,
        "children": [x.id for x in node.children]
    }


def get_tree_as_json(root) -> dict:
    TreeEnumerator().enumerate_tree(root)
    return {"tree": [get_node_as_json(x) for x in root.pre_order()]}