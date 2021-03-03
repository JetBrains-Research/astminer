class A:
    def __init__(self):
        """
        outer init
        """
        pass


    def __add__(self, other):
        pass


    def foo(self, x):
        return x * x


    def foo_typed(self, x: int, y: int) -> int:
        return x * y


    class B:
        def __init__(self):
            """
            inner init
            """
            pass


        def __get__(self, instance, owner):
            pass


        def foo_typed(self, x: int, y: int) -> int:
            return x + y


        class C:

            def __init__(self):
                pass

            def bar_typed(self, x: int) -> int:
                return x
