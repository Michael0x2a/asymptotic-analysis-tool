public class QuickSort {
    public void sort(int[] inputArr) {
        int length = inputArr.length;
        quickSort(inputArr, 0, length - 1);
    }

    private void quickSort(int[] array, int lowerIndex, int higherIndex) {
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        int pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
        // Divide into two arrays
        for (int k = 0; k < array.length; k++) {
            if (k <= j) {
                for (int x = i; x < pivot; x++) {
                    if (array[x] < pivot) {
                        i += 1;
                    }
                }
                for (int y = j; y < pivot; y++) {
                    if (array[pivot - y] > pivot) {
                        j -= 1;
                    }
                }
                if (i <= j) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    //move index to next position on both sides
                    i += 1;
                    j -= 1;
                }

            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(array, lowerIndex, j);
        if (i < higherIndex)
            quickSort(array, i, higherIndex);
    }
}
