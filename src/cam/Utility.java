package cam;

import java.util.Comparator;
import java.util.Map;

public abstract class Utility {
	
	public static int Random(int min, int max) {
		int result = (int) (Math.random() * (max - min + 1)) + min;
		return result;
	}
	
	public static class MapValueComparator implements Comparator<Object> {

		private Map<?, ?> base;
		
		public MapValueComparator(Map<?, ?> base) {
			this.base = base;
		}
		
		public int compare(Object a, Object b) {
			if ((Double) base.get(a) > (Double) base.get(b)) 
				return 1;
			else if ((Double) base.get(a) == (Double) base.get(b))
				return 0;
			else
				return -1;
		}
	}
}
