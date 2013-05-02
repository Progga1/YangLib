package yang.model.map;

public class DefaultIntMap implements IntMap{

	private int[][] values;
	
	public DefaultIntMap(int[][] values) {
		this.values = values;
	}
	
	public int getValue(int x,int y) {
		return values[y][x];
	}
	
	public int getWidth() {
		return values[0].length;
	}
	
	public int getHeight() {
		return values.length;
	}
	
}
