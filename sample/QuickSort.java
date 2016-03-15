public class QuickSort {
	// BROKEN: do not use
    public void sort(int[] array) {
    	if (array.length < 2) return;
    	int pivot = array[0];
    	// Count how many elements are in each partition
    	int numLess = 0;
    	int numGreater = 0;
    	
        for (int i = 0; i < array.length; i++) {
        	if (array[i] < pivot) {
        		numLess = numLess + 1;
        	} else if (array[i] > pivot) {
        		numGreater = numGreater + 1;
        	}
        }
        
        int[] half1 = new int[numLess];
        int[] half2 = new int[numGreater];
        int i = 0;
        int j = 0;
        
        for (int k = 0; k < array.length; k++) {
        	if (array[k] < pivot) {
        		half1[i] = array[k];
        		i = i + 1;
        	} else if (array[k] > pivot) {
        		half2[j] = array[k];
        		j = j + 1;
        	}
        }
        
        sort(half1);
        sort(half2);
        merge(array, half1, pivot, half2);
        
    }

    private void merge(int[] output, int[] half1, int pivot, int[] half2) {
	    int i = 0;
	    for (int k = 0; k < half1.length; k++) {
	    	output[i] = half1[k];
	    	i = i + 1;
	    }
	    output[i] = pivot;
	    for (int k = 0; k < half2.length; k++) {
	    	output[i] = half2[k];
	    	i = i + 1;
	    }
	}
}
