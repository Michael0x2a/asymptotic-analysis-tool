package sample;

public class ArraySum {

	public int sum(int[] array) {
		int sum = 0;
		for (int i : array) {
			sum += i;
		}
		return sum;
	}

}
