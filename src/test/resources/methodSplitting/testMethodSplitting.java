class Class1 {
    void functionReturningVoid() {}

    int functionReturningInt() {
        return 0;
    }

    String[] functionReturningStrings() {
        return "";
    }

    Class1 functionReturningClass() {
        return Class1();
    }

    void functionInClass1() {}

    class Class2 {
        void functionInClass2() {}
    }

    void functionWithNoParameters() {}

    void functionWithOneParameter(int p1) {}

    void functionWithThreeParameters(Class p1, String[][] p2, int[]... p3) {}
}