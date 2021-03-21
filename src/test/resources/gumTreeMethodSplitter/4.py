def foo(a: int):

    def foo_1(b):

        def foo_2(c):
            return None

        return foo_2(b)


    def bar_1(b: int, c: int) -> int:

        def bar_2(d: int, e: int) -> int:
            return 42

        return bar_2(b, c)


    foo_1(a)
    bar_1(a, a)

    return None
