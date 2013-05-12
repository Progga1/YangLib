package yang.math;

public class MathUtil {

	public static boolean pointInRect(float pointX,float pointY, float rectLeft,float rectTop,float rectRight,float rectBottom) {
		return pointX>rectLeft && pointX<rectRight && pointY<rectTop && pointY>rectBottom;
	}
	
	public static boolean circleCollision(float deltaX,float deltaY,float radiusSum) {
		return deltaX*deltaX + deltaY*deltaY <= radiusSum*radiusSum;
	}
	
}
