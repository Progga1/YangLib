package yang.math;

public class Geometry {

	public static float getDistance(float posX1, float posY1, float posX2, float posY2) {
		return (float)Math.sqrt((posX2-posX1)*(posX2-posX1) + (posY2-posY1)*(posY2-posY1));
	}
	
	public static double getDistance(double posX1, double posY1, double posX2, double posY2) {
		return Math.sqrt((posX2-posX1)*(posX2-posX1) + (posY2-posY1)*(posY2-posY1));
	}
	
	public static float getDistance(float deltaX, float deltaY) {
		return (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public static double getDistance(double deltaX, double deltaY) {
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public static float getDistance(float deltaX, float deltaY, float deltaZ) {
		return (float)Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ);
	}
	
	public static float getDistance(float[] vector) {
		float result = 0;
		for(float comp:vector) {
			result += comp*comp;
		}
		return (float)Math.sqrt(result);
	}
	
	public static float getAngleDown(float distance,float dX,float dY) {
		if(distance==0)
			return 0;
		if(dX>0)
			return (float)Math.acos(-dY/distance);
		else
			return -(float)Math.acos(-dY/distance);
	}
	
	public static float getAngle(float dX,float dY) {
		if(dX==0 && dY==0)
			return 0;
		double distance = Math.sqrt(dX*dX + dY*dY);
		if(dY>0)
			return (float)Math.acos(dX/distance);
		else
			return -(float)Math.acos(dX/distance);
	}
	
	public static float getAngleDown(float deltaX,float deltaY) {
		return getAngleDown(getDistance(deltaX,deltaY),deltaX,deltaY);
	}
	
	public static float getAngleDown(float posX1, float posY1, float posX2, float posY2) {
		return getAngleDown(getDistance(posX1,posY1,posX2,posY2),posX2-posX1,posY2-posY1);
	}

	public static float rotateGetX(float x, float y, float anchorX, float anchorY, float angle) {
		return (float)((x-anchorX)*Math.cos(angle) + (y-anchorY)*Math.sin(angle) + anchorX);
	}
	
	public static float rotateGetY(float x, float y, float anchorX, float anchorY, float angle) {
		return (float)(-(x-anchorX)*Math.sin(angle) + (y-anchorY)*Math.cos(angle) + anchorY);
	}
	
	public static float cross2D(float x1,float y1,float x2,float y2) {
		return y1*x2 - x1*y2;
	}
	
}
