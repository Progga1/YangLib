package yang.math;

public class MathUtil {

	public static boolean pointInRect(float pointX,float pointY, float rectLeft,float rectTop,float rectRight,float rectBottom) {
		return pointX>rectLeft && pointX<rectRight && pointY<rectTop && pointY>rectBottom;
	}
	
	public static boolean circleCollision(float deltaX,float deltaY,float radiusSum) {
		return deltaX*deltaX + deltaY*deltaY <= radiusSum*radiusSum;
	}
	
	public static float manhattanDist(float x1, float y1, float x2, float y2) {
		return Math.abs(x1-x2) + Math.abs(y1-y2);
	}
	
	public static float dist(float x1, float y1, float x2, float y2) {
		float dx = (x1-x2);
		float dy = (y1-y2);
		return MathFunc.sqrt(dx*dx + dy*dy);
	}
	
}
