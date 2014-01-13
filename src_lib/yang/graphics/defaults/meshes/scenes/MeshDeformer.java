package yang.graphics.defaults.meshes.scenes;

import yang.util.Util;

public class MeshDeformer {

	public LimbObject mLimb;
	public MeshObject mMesh;

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

	@Override
	public String toString() {
		String result = mLimb+"-"+mMesh+"\n";
		if(mIndices==null || mWeights==null)
			return result;
		else
			return result+Util.arrayToString(mIndices," ",0)+"\n"+Util.arrayToString(mWeights," ",0);
	}

	public void copyFrom(int[] indices, float[] weights) {
		System.arraycopy(indices, 0, mIndices, 0, mIndices.length);
		System.arraycopy(weights, 0, mWeights, 0, mWeights.length);
	}

}
