public class PowerSet {
	public void powerSet(int[] arr) {
		if (arr.length < 1) return;
		
		int next = arr[0];
		int[] subArr = new int[arr.length - 1];
		for (int i = 1; i < arr.length; i++)
			subArr[i-1] = arr[i];
		
		System.out.println("With " + next + ":");
		powerSet(subArr);
		
		System.out.println("Or without " + next + ":");
		powerSet(subArr);
	}
}
