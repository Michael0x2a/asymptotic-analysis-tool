public class Test {
    public static int test(int a) {
        if (a == 1) {
            return 2;
        } else {
            return 3 + test(a / 2) + test(a / 2);
        }
    }

    public int foo() {
        System.out.println("Hello!");
    }
}