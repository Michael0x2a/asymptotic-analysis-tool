public class NestedFor {
    public void NestedFor(int[] arr) {
        int i, j;
        for (i = 0; i < arr.length; i++) {
            for (j = 0; j < i; j++) {
                if (arr[j] < arr[minIndex])
                    minIndex = j;
                else {
                    System.out.println("Hello!");
                }
            }
            // Swap smallest found with element in position i
            temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
}
