package cam;

public abstract class Utility {
	
	public static int Random(int min, int max) {
		int result = (int) (Math.random() * (max - min + 1)) + min;
		return result;
	}
}
