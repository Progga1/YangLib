package yang.math.objects;

public class Point2f {

	public float mPosX;
	public float mPosY;
	
	public Point2f() {
		set(0,0);
	}
	
	public Point2f(float posX,float posY) {
		set(posX,posY);		
	}
	
	public Point2f set(float posX,float posY) {
		mPosX = posX;
		mPosY = posY;
		return this;
	}
	
	public Point2f set(Point2f point) {
		mPosX = point.mPosX;
		mPosY = point.mPosY;
		return this;
	}
	
	public void add(Vector2f vector) {
		mPosX += vector.mPosX;
		mPosY += vector.mPosY;
	}
	
	public String toString() {
		return "x="+mPosX+", y="+mPosY;
	}

	public float getDistance(Point2f point) {
		return (float)Math.sqrt((point.mPosX-mPosX)*(point.mPosX-mPosX) + (point.mPosY-mPosY)*(point.mPosY-mPosY));
	}
	
	public float getSqrDistance(Point2f point) {
		return (point.mPosX-mPosX)*(point.mPosX-mPosX) + (point.mPosY-mPosY)*(point.mPosY-mPosY);
	}
	
	public float getDistance(float x,float y) {
		return (float)Math.sqrt((x-mPosX)*(x-mPosX) + (y-mPosY)*(y-mPosY));
	}
	
	public float getSqrDistance(float x,float y) {
		return (x-mPosX)*(x-mPosX) + (y-mPosY)*(y-mPosY);
	}
	
}
