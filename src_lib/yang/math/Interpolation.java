package yang.math;

/**
 * row major
 */
public class Interpolation {

	public static float linInterpolate(float[] array, float position) {
		if(array.length==1)
			return array[0];
		int intPos = (int)position;
		float weight = position-intPos;
		return array[intPos]*(1-weight) + array[(intPos+1)%array.length]*weight;
	}

	public static float bilinInterpolate(float[][] array, float line,float column) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)line;
		float weight = line-intLine;
		return linInterpolate(array[intLine],column)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column)*weight;
	}

	public static float linInterpolate(float[] array, float position,int stride,int offset) {
		if(array.length==1)
			return array[0];
		int intPos = (int)position;
		float weight = position-intPos;
		int col = intPos*stride+offset;
		return array[col]*(1-weight) + array[(col+stride)%array.length]*weight;
	}

	public static float bilinInterpolate(float[][] array, float line,float column,int columnStride,int columnOffset) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)line;
		float weight = line-intLine;
		return linInterpolate(array[intLine],column,columnStride,columnOffset)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column,columnStride,columnOffset)*weight;
	}

	public static float bilinInterpolateNormalized(float[][] array, float x,float y) {
		x *= (array[0].length-1);
		y *= (array.length-1);
		return bilinInterpolate(array,y,x);
	}

	public static float bilinInterpolateNormalized(float[][] array, float x,float y,int stride,int offset) {
		x *= array[0].length;
		y *= array.length;
		return bilinInterpolate(array,y,x,stride,offset);
	}

}
