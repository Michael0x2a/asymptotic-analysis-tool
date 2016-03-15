public class InsertionSort {
	public void insertionSort(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			for (int j = 1; j <= i; j++) {
				int next = i - j;
				if (arr[i] < arr[j]) {
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp; 
				}
			}
		}
		System.out.println(3);
		System.out.println("foo");
	}
}
