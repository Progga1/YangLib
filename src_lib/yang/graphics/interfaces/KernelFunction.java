package yang.graphics.interfaces;

public abstract class KernelFunction {

	public static float PI = 3.1415926535f;
	public int mRadius;
	public int mSize;
	public float[][] mWeights;
	protected boolean mAutoNormalize;
	
	protected abstract float computeWeight(float centerDistance);
	
	public KernelFunction() {
		mAutoNormalize = true;
	}
	
	public KernelFunction init(int radius) {
		mRadius = radius;
		mSize = 2*radius+1;
		mWeights = new float[mSize][mSize];
		float dSize = 1f/(mSize-1);
		float sum = 0;
		for(int y=0;y<mSize;y++) {
			float[] weightLine = mWeights[y];
			float normY = y*dSize*2-1;
			for(int x=0;x<mSize;x++) {
				float normX = x*dSize*2-1;
				float distance = (float)Math.sqrt(normX*normX + normY*normY);
				weightLine[x] = computeWeight(distance);
				sum += weightLine[x];
			}
		}
		//Normalize
		if(mAutoNormalize) {
			float dSum = 1/sum;
			for(int y=0;y<mSize;y++) {
				float[] weightLine = mWeights[y];
				for(int x=0;x<mSize;x++) {
					weightLine[x] *= dSum;
				}
			}
		}
		return this;
	}

	public float getCenterWeight() {
		return mWeights[mRadius][mRadius];
	}
	
}
