def fun_with_no_class():
    pass

class Class1:

    def fun_in_class1(self):
        pass

    class Class2:

        def fun_in_class2(self):
            pass

def function_with_no_parameters():
    pass

def function_with_one_parameter(p1):
    pass

def function_with_one_typed_parameter(p1: int):
    pass

def function_with_complex_parameter(p1: List[int]):
    pass

def function_with_three_parameters(p1, p2 = 4, p3: int = 3):
    pass

class Class3:
    def fun_with_parameter_in_class(self, p1):
        pass

    def fun_with_typed_parameter_in_class(self, p1: int):
        pass

def function_containing_function():
    def function_inside_function():
        pass
    pass

class Class4:
    def some_method(self):
        def function_inside_method():
            pass
    def second_method(self):
        def second_function_inside_method():
            def fun_inside_fun_inside_method():
                pass