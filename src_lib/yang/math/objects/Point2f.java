package yang.math.objects;


public class Point2f {

	public float mX;
	public float mY;

	public Point2f() {
		set(0,0);
	}

	public Point2f(Point2f point) {
		set(point);
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

	public void add(float addX, float addY) {
		mX += addX;
		mY += addY;
	}

	@Override
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

	/**
	 * Mixes the old and the new position
	 * @param amountOfNew between 0 and 1 the higher, the faster the new position will be set
	 * @param newX new position X
	 * @param newY new position Y
	 */
	public void mixIn(final float amountOfNew, final float newX, final float newY) {
		final float inv = 1 - amountOfNew;
		set(newX*amountOfNew + mX*inv,newY*amountOfNew + mY*inv);
	}

	public float angleTo(float mHeadX, float mHeadY) {
		float dy = (mHeadY-mY);
		float dx = (mHeadX-mX);
		float baseAngle = (float)(Math.atan(dy/dx));
		if(dx < 0 ) baseAngle += Math.PI;

		return baseAngle;
	}

	public void sub(Point2f toSub) {
		mX -= toSub.mX;
		mY -= toSub.mY;
	}

	/** higher weight means faster interpolation to <b>otherPoint</b> [0-1] */
	public void interpolate(Point2f otherPoint, float weight) {
		mX += (otherPoint.mX-mX)*weight;
		mY += (otherPoint.mY-mY)*weight;
	}

	public void setLerp(Point2f point1, Point2f point2, float weight) {
		mX = point1.mX*(1-weight) + point2.mX*weight;
		mY = point1.mY*(1-weight) + point2.mY*weight;
	}



}
