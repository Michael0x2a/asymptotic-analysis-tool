public class ArraySum {
	public int sum(int[] arr) {
		int s = 0;
		for (int i : arr)
			s += i;
		return s;
	}
}
