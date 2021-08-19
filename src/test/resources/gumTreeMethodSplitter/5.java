class AnnotatedFunction {
    @Deprecated
    @SuppressWarnings("deprecated")
    String someDeprecatedFun() {
        return "Delete me i'm deprecated!"
    }
}

abstract class someAbstractClass {
    public abstract int function();
}