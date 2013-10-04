package yang.math.objects;

public class Point2f {

	public float mX;
	public float mY;
	
	public Point2f() {
		set(0,0);
	}
	
	public Point2f(float posX,float posY) {
		set(posX,posY);		
	}
	
	public Point2f set(float posX,float posY) {
		mX = posX;
		mY = posY;
		return this;
	}
	
	public Point2f set(Point2f point) {
		mX = point.mX;
		mY = point.mY;
		return this;
	}
	
	public void add(Vector2f vector) {
		mX += vector.mX;
		mY += vector.mY;
	}
	
	public String toString() {
		return "x="+mX+", y="+mY;
	}

	public float getDistance(Point2f point) {
		return (float)Math.sqrt((point.mX-mX)*(point.mX-mX) + (point.mY-mY)*(point.mY-mY));
	}
	
	public float getSqrDistance(Point2f point) {
		return (point.mX-mX)*(point.mX-mX) + (point.mY-mY)*(point.mY-mY);
	}
	
	public float getDistance(float x,float y) {
		return (float)Math.sqrt((x-mX)*(x-mX) + (y-mY)*(y-mY));
	}
	
	public float getSqrDistance(float x,float y) {
		return (x-mX)*(x-mX) + (y-mY)*(y-mY);
	}
	
}
