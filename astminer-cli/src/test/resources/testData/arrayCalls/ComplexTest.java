public class ComplexTest {

    private static final int SIZE = 4;

    private int[][] array;

    public ComplexTest() {
        array = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                array[i][j] = 0;
            }
        }
    }
}
