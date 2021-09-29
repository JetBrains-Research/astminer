SHADOW_LIST = ["{", "}", "(", ")", "[", "]", ",", ";"]


class NextSiblingAvailable(Exception):
    pass


def get_node_as_dict(cursor, file_bytes):
    node_type = cursor.node.type
    if len(cursor.node.children) == 0:
        node_value = file_bytes[cursor.node.start_byte : cursor.node.end_byte].decode("utf-8")
    else:
        node_value = None

    return {"token": node_value, "nodeType": node_type, "children": []}


def get_tree_as_dict(cursor, original_file_bytes):
    depth = 0
    tree = []
    last_node_by_indent = {}
    index = 0
    while True:
        if cursor.node.type not in SHADOW_LIST:
            node = get_node_as_dict(cursor, original_file_bytes)
            last_node_by_indent[depth] = node
            if depth > 0:
                last_node_by_indent[depth - 1]["children"].append(index)
            tree.append(node)
            index += 1
        if cursor.goto_first_child():
            depth += 1
            continue
        if cursor.goto_next_sibling():
            continue
        try:
            while cursor.goto_parent():
                depth -= 1
                if cursor.goto_next_sibling():
                    raise NextSiblingAvailable
        except NextSiblingAvailable:
            continue
        break
    return {"tree": tree}
