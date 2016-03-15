public class Test {
    public static int test(int[] a, int[] b) {
        int sum = 0;
        for (int i : a) {
            for (int j : b) {
                for (int k : a) {
                    sum += 1;
                }
            }
        }
        return sum;
    }

    public int foo() {
        System.out.println("Hello!");
    }
}