package yang.graphics.util;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import yang.graphics.camera.CameraTransformations;
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

	public void sort(ShortBuffer indices, FloatBuffer positions,YangMatrix modelViewTransform) {

		Arrays.fill(mDepthIndices,(short)-1);


		int endIndex = indices.position();
		int endPosition = positions.position();

		final float BOUNDS_FACTOR = 1.2f;

		float[] matrix = modelViewTransform.mValues;

		float minZ = Float.MAX_VALUE;
		float maxZ = -Float.MAX_VALUE;
		positions.position(0);
		for(int i=0;i<endPosition;i++) {
			float x = positions.get();
			float y = positions.get();
			float z = positions.get();
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
			float dRange = 1f/range / BOUNDS_FACTOR;
			for(int i=0;i<endPosition;i++) {
				mZValues[i] = ((mZValues[i]-minZ)*dRange) * (indexCount-1);
			}
		}

		indices.position(0);
		for(int i=0;i<endIndex;i+=3) {
			short id1 = indices.get();
			short id2 = indices.get();
			short id3 = indices.get();
			float avrgZ = (mZValues[id1]+mZValues[id2]+mZValues[id3])*0.33333f;	//TODO remove 1/3
			int index = ((int)avrgZ)*3;
			while(mDepthIndices[index]>-1) {
				index+=3;
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

	public void sort(ShortBuffer indices,FloatBuffer positions,CameraTransformations camera,YangMatrix modelTransform) {
		mTempModelViewTransform.set(camera.getViewTransformReference());
		if(modelTransform!=null)
			mTempModelViewTransform.multiplyRight(modelTransform);
		sort(indices,positions,mTempModelViewTransform);
	}

}
