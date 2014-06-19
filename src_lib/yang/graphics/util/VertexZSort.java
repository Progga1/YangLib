package yang.graphics.util;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import yang.graphics.camera.CameraProjection;
import yang.math.objects.YangMatrix;

public class VertexZSort {

	public short[] mDepthIndices;
	protected float[] mZValues;
	private int indexCount;
	private YangMatrix mTempModelViewTransform = new YangMatrix();

	public VertexZSort(int depths,int maxVertices) {
		mDepthIndices = new short[depths*3];
		indexCount = depths;
		mZValues = new float[maxVertices];
	}

	public void sort(ShortBuffer indices, FloatBuffer positions,FloatBuffer normals,float normShift,YangMatrix modelViewTransform) {

		Arrays.fill(mDepthIndices,(short)-1);

		int endIndex = indices.position();
		int endPosition = positions.position();

		float[] matrix = modelViewTransform.mValues;

		float minZ = Float.MAX_VALUE;
		float maxZ = -Float.MAX_VALUE;
		positions.position(0);
		if(normals!=null)
			normals.position(0);
		for(int i=0;i<endPosition;i++) {
			float x = positions.get();
			float y = positions.get();
			float z = positions.get();normShift = 0;
			if(normShift!=0) {
				x += normals.get()*normShift;
				y += normals.get()*normShift;
				z += normals.get()*normShift;
//				positions.position(i*3);
//				positions.put(x);
//				positions.put(y);
//				positions.put(z);
			}
//			float sX = matrix[12];
//			float sY = matrix[13];
//			float sZ = matrix[14];
//			float w = matrix[3]*x + matrix[7]*y + matrix[11]*z + matrix[15];
//			float xVal = matrix[0]*x + matrix[4]*y + matrix[8]*z + matrix[12];
//			float yVal = matrix[1]*x + matrix[5]*y + matrix[9]*z + matrix[13];
//			float zVal = matrix[2]*x + matrix[6]*y + matrix[10]*z + matrix[14] + 1;
//			xVal /= w;
//			yVal /= w;
//			zVal /= w;
//			zVal = (float)Math.sqrt(zVal*zVal + xVal*xVal + yVal*yVal);

			float zVal = matrix[2]*x + matrix[6]*y + matrix[10]*z + matrix[14];

			if(zVal<minZ)
				minZ = zVal;
			if(zVal>maxZ)
				maxZ = zVal;
			mZValues[i] = zVal;
		}

		float range = maxZ-minZ;

		if(range==0) {
			for(int i=0;i<endPosition;i++) {
				mZValues[i] = 0;
			}
		}else{
			float dRange = 1f/range;
			for(int i=0;i<endPosition;i++) {
				mZValues[i] = ((mZValues[i]-minZ)*dRange) * (indexCount-1);
			}
		}

		int maxIndex = mDepthIndices.length-3;
		indices.position(0);
		for(int i=0;i<endIndex;i+=3) {
			short id1 = indices.get();
			short id2 = indices.get();
			short id3 = indices.get();
			float avrgZ = (mZValues[id1]+mZValues[id2]+mZValues[id3])*0.33333f;
			int index = ((int)avrgZ)*3;
			if(index>maxIndex) {
				while(mDepthIndices[index]>-1)
					index-=3;
				maxIndex = index-3;
			}else{
				int startIndex = index;
				while(mDepthIndices[index]>-1) {
					index+=3;
				}
				if(index>=maxIndex)
					maxIndex = startIndex-3;
			}

			mDepthIndices[index] = id1;
			mDepthIndices[index+1] = id2;
			mDepthIndices[index+2] = id3;
		}

		indices.position(0);
		for(int i=0;i<mDepthIndices.length;i+=3) {
			if(mDepthIndices[i]>-1) {
				indices.put(mDepthIndices[i]);
				indices.put(mDepthIndices[i+1]);
				indices.put(mDepthIndices[i+2]);
			}
		}

//		indices.put(mDepthIndices);
	}

	public void sort(ShortBuffer indices,FloatBuffer positions,FloatBuffer normals,float normShift,CameraProjection camera,YangMatrix modelTransform) {
		mTempModelViewTransform.set(camera.getViewTransformReference());
		if(modelTransform!=null)
			mTempModelViewTransform.multiplyRight(modelTransform);
//		mTempModelViewTransform.multiplyLeft(camera.getProjectionTransformReference());
		sort(indices,positions,normals,normShift,mTempModelViewTransform);
	}

}
