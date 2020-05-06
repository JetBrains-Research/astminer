void functionWithNoClass() {}

class Class1 {

    void functionInClass1() {}

    class Class2 {
        void functionInClass2() {}
    };
};

class Class {};

void functionReturningVoid() {}

int functionReturningInt() {
    return 0;
}

string functionReturningString() {
    return "string"
}

Class functionReturningClass() {
    return Class()
}

void functionWithNoParameters() {}

void functionWithOneParameter(int p1) {}

void functionWithThreeParameters(int p1, int p2, int p3) {}
