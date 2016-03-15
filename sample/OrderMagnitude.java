public class OrderMagnitude {
	// Find the order of magnitude of a number
	public int orderMagnitude(int n) {
		if (n <= 1) return 0;
		return 1 + orderMagnitude(n/10);
	}
}
