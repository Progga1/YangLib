package yang.math;

public class MathFunc {

	public static float abs(float x) {
		if(x<0)
			return -x;
		else 
			return x;
	}
	
	public static double abs(double x) {
		if(x<0)
			return -x;
		else 
			return x;
	}
	
	public static float sqr(float x) {
		return x*x;
	}

	public static int sign(float x) {
		if(x>=0)
			return 1;
		else
			return -1;
	}
	
	public static int signZero(float x) {
		if(x==0)
			return 0;
		if(x>0)
			return 1;
		else
			return -1;
	}
	
	public static float random(float minX,float maxX) {
		return (float)(minX+Math.random()*(maxX-minX));
	}
	
	public static double random(double minX,double maxX) {
		return minX+Math.random()*(maxX-minX);
	}
	
	public static int random(int upperBound) {
		return (int)(Math.random()*upperBound);
	}
	
	public static int random(int lowerBound,int upperBound) {
		return (int)(Math.random()*(upperBound-lowerBound)+lowerBound);
	}
	
}
