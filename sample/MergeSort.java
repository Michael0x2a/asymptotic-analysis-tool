
public class MergeSort {
	public void mergeSort(int[] arr) {
	    if (arr.length < 2) return;
	    // Wasteful allocations, but shouldn’t affect asymptotic runtime
	    int[] half1 = new int[arr.length / 2];
	    int[] half2 = new int[arr.length - half1.length];
	    int i = 0;
	    for (int j = 0; j < half1.length; j++)
	        half1[j] = arr[i++];
	    for (int j = 0; j < half2.length; j++)
	        half2[j] = arr[i++];
	    mergeSort(half1);
	    mergeSort(half2);
	    merge(arr, half1, half2);
	}

	private void merge(int[] output, int[] half1, int[] half2) {
	    int target = 0;  int i = 0;  int j = 0;
	    for (int k = i; k < half1.length; k++) {
	    	if (j < half2.length) {
	    		if (half1[i] <= half2[j])
		            output[target++] = half1[i++];
		        else
		            output[target++] = half2[j++];
	    	}
	    }    
	    for (int k = i; k < half1.length; k++)
	    	output[target++] = half1[k++];
	    for (int k = j; k < half2.length; k++)
	        output[target++] = half2[k++];
	}
}
