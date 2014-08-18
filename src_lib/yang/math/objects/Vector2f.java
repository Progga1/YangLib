package yang.math.objects;

public class Vector2f extends Point2f{

	public Vector2f() {
		set(0,0);
	}

	public Vector2f(float posX,float posY) {
		set(posX,posY);
	}

	public float dot(Vector2f vector) {
		return mX*vector.mX + mY*vector.mY;
	}

	public float getMagnitude() {
		return (float)Math.sqrt(mX*mX+mY*mY);
	}

	public float getMagnitudeSqr() {
		return mX*mX+mY*mY;
	}

	public Vector2f norm() {
		scale(1f/getMagnitude());
		return this;
	}

	/**
	 * returns the absolute angle between to vectors
	 * @param to
	 * @return
	 */
	public float angleAbsTo(Vector2f to) {
		final float lenA = this.getMagnitudeSqr();
		final float lenB = to.getMagnitudeSqr();

		final float num = (mX*to.mX + mY*to.mY);
		final float den = 1/(lenA*lenB);

		final float angle = (float) Math.acos( num * Math.sqrt(den));

//		if(num > 0)
			return angle;
//		else
//			return (float) Math.PI - angle;
	}

	public float cross(Vector2f b) {
		return mX*b.mY - mY*b.mX;
	}

	public void add(Vector2f direction, float magnitude) {
		mX += direction.mX * magnitude;
		mY += direction.mY * magnitude;
	}

	public void scale(float f) {
		mX *= f;
		mY *= f;
	}

	/** Normalized the vector and scales it to a new length */
	public void setMagnitude(float newLength) {
		scale(newLength/getMagnitude());
	}

	public void setFromTo(Point2f fromPoint, Point2f toPoint) {
		mX = toPoint.mX - fromPoint.mX;
		mY = toPoint.mY - fromPoint.mY;
	}

	public float setNormalized(Vector2f vector) {
		float magn = vector.getMagnitude();
		if(magn==0) {
			mX = 0;
			mY = 0;
		}else{
			mX = vector.mX/magn;
			mY = vector.mY/magn;
		}
		return magn;
	}



}
