public class SelectionSort {
    public void selectionSort(int[] arr) {
        int i, j, minIndex, temp;  
        for (i = 0; i < arr.length; i++) {
            // Locate smallest element between positions i and arr.length - 1
            minIndex = i;
            for(j = i; j < arr.length; j++) {
                if(arr[j] < arr[minIndex])         
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
