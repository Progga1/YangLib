package yang.graphics.defaults.meshes.scenes;

import yang.util.Util;

public class MeshDeformer {

	public LimbObject mLimb;
	public MeshObject mMesh;
	public int mMapIndex = -1;

	public int[] mIndices;
	public float[] mWeights;

	public MeshDeformer(LimbObject limb,MeshObject mesh) {
		mLimb = limb;
		mMesh = mesh;
	}

	public MeshDeformer init(int vertexCount) {
		mIndices = new int[vertexCount];
		mWeights = new float[vertexCount];
		return this;
	}

	public void copyFrom(int[] indices, float[] weights) {
		System.arraycopy(indices, 0, mIndices, 0, mIndices.length);
		System.arraycopy(weights, 0, mWeights, 0, mWeights.length);
	}

	public int getVertexCount() {
		if(mIndices==null)
			return 0;
		else
			return mIndices.length;
	}

	public String valuesToString() {
		if(mIndices==null || mWeights==null)
			return Util.arrayToString(mIndices," ",0)+"\n"+Util.arrayToString(mWeights," ",0);
		else
			return "<novalues>";
	}

	public String toStringCompletely() {
		return toString()+"\n"+valuesToString();
	}

	@Override
	public String toString() {
		return mMapIndex+": "+mLimb+"-"+mMesh;
	}

}
