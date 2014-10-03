package yang.math.objects;

public class Point2i {

	public int x;
	public int y;

	public Point2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void negate() {
		x = -x;
		y = -y;
	}

	public void add(Point2i p) {
		x += p.x;
		y += p.y;

	}
}
