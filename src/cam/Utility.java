package cam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Map;

import org.bukkit.Location;

public abstract class Utility {
	
	public static int Random(int min, int max) {
		int result = (int) (Math.random() * (max - min + 1)) + min;
		return result;
	}
	
	public static boolean IsNear(Location first, Location second, int minDistance, int maxDistance) {
		double relX = first.getX() - second.getX();
		double relY = first.getY() - second.getY();
		double relZ = first.getZ() - second.getZ();
		double dist = relX * relX + relY * relY + relZ * relZ;
		
		if (dist < maxDistance * maxDistance && dist > minDistance * minDistance)
			return true;
		
		return false;
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

	public static void StreamToFile(InputStream resource, File file) throws Exception {
		OutputStream outputStream = new FileOutputStream(file);
		
		Copy(resource, outputStream);
	}
	
	public static void FileToFile(File source, File dest) throws Exception {
		InputStream inputStream = new FileInputStream(source);
		OutputStream outputStream = new FileOutputStream(dest);
		
		Copy(inputStream, outputStream);
	}
	
	private static void Copy(InputStream inputStream, OutputStream outputStream) throws Exception {
		int read = 0;
		byte[] bytes = new byte[1024];
	 
		while ((read = inputStream.read(bytes)) != -1)
			outputStream.write(bytes, 0, read);
	 
		inputStream.close();
		outputStream.close();
	}
}
