public class Test {
    public static int test(int[] a) {
        int sum = 0;
        for (int b : a) {
            foo(3);
        }
        return sum;
    }

    public int foo(int count) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += 1;
        }
        return sum;
    }
}