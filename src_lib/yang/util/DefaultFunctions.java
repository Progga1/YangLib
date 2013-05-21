package yang.util;

import yang.util.lookuptable.Function;

public class DefaultFunctions {

	public static Function EXP_SHRINKING = new Function() {
			public float evaluate(float x) {
				return (float)(Math.sin(Math.pow(1-x,3)*Math.PI));
			}
		};
	
	public static Function EXP_GROWING_SQR_SHRINKING = new Function() {
				public float evaluate(float x) {
					return (float)(Math.sqrt(1-Math.pow(x*2-1,6)) * Math.pow(1-x,1f/3)) * (1-x*x);
				}
			};
	
}
