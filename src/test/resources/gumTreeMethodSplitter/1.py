from typing import Dict, List, Union


def no_args_func():
    """
    :return: None
    """
    return None


def with_args_no_typed(a, b, c, d = 42):
    return a, b, c


def with_typed_args(a: int, z: str):
    return None


def with_typed_return_no_args() -> str:
    x: int = 42
    return "str"


def full_typed(filename: str) -> str:
    """
    :param filename: path to file
    :return: string with file content
    """
    with open(filename, 'rt') as f:
        content = f.read()
    return content


def func_dif_args_typed_return(a, b, /, c, d, *, e, f) -> int:
    """
    python doc
    """
    return 42


JsonNodeType = Dict[str, Union[str, List[int]]]

def complex_args_full_typed(node: JsonNodeType) -> JsonNodeType:
    return node
