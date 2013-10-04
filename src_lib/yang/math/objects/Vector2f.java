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
	
}
