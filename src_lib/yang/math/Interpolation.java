package yang.math;

import yang.math.objects.Vector3f;

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

	public static float linInterpolate(float[] array, float position,int stride,int offset) {
		if(array.length==1)
			return array[0];
		int intPos = (int)position;
		float weight = position-intPos;
		int col = intPos*stride+offset;
		return array[col]*(1-weight) + array[(col+stride)%array.length]*weight;
	}


	public static float bilinInterpolate(float[][] array, float row,float column) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)row;
		float weight = row-intLine;
		return linInterpolate(array[intLine],column)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column)*weight;
	}

	public static float bilinInterpolate(float[][] array, float row,float column,int columnStride,int columnOffset) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)row;
		float weight = row-intLine;
		return linInterpolate(array[intLine],column,columnStride,columnOffset)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column,columnStride,columnOffset)*weight;
	}

	public static void bilinInterpolateVector(float[][] array, float row,float column,Vector3f targetVector) {
		int intRow = (int)row;
		float rowWeight = row-intRow;
		int intCol = (int)column;
		float colWeight = column-intCol;
		int colId = intCol*3;
		float[] line1 = array[intRow];
		float[] line2 = array[intRow+1];
		targetVector.mX = (line1[colId]*(1-colWeight)+line1[colId]*(colWeight))*(1-rowWeight) + (line2[colId]*(1-colWeight)+line2[colId]*(colWeight))*rowWeight;
		targetVector.mY = (line1[colId+1]*(1-colWeight)+line1[colId+1]*(colWeight))*(1-rowWeight) + (line2[colId+1]*(1-colWeight)+line2[colId+1]*(colWeight))*rowWeight;
		targetVector.mZ = (line1[colId+2]*(1-colWeight)+line1[colId+2]*(colWeight))*(1-rowWeight) + (line2[colId+2]*(1-colWeight)+line2[colId+2]*(colWeight))*rowWeight;
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
