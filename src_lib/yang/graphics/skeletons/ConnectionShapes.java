package yang.graphics.skeletons;


public class ConnectionShapes {

	protected int mSampleCount;
	public float[] mPositions1;
	public float[] mScales1;
	public float[] mPositions2;
	public float[] mScales2;

	public ConnectionShapes(int samples) {
		mSampleCount = samples;
		mPositions1 = new float[samples*2];
		mPositions2 = new float[samples*2];
		mScales1 = new float[samples*2];
		mScales2 = new float[samples*2];
	}

	/**
	 * Sample count minus one
	 */
	public int getSegmentCount() {
		return mSampleCount-1;
	}

	public int getSampleCount() {
		return mSampleCount;
	}

	protected void scaleByDirection(float[] positions,float[] targetScales) {
		int k=2;
		for(int i=1;i<mSampleCount-1;i++) {
			float x = positions[k];
			float y = positions[k+1];
			float dx1 = x-positions[k-2];
			float dy1 = y-positions[k-1];
			float dx2 = positions[k+2]-x;
			float dy2 = positions[k+3]-y;
			float d1 = (float)Math.sqrt(dx1*dx1+dy1*dy1);
			float d2 = (float)Math.sqrt(dx2*dx2+dy2*dy2);
			dx1 /= d1;
			dy1 /= d1;
			dx2 /= d2;
			dy2 /= d2;

			float dx = (dx1+dx2)*0.5f;
			float dy = (dy1+dy2)*0.5f;
			float d = (float)Math.sqrt(dx*dx+dy*dy);

			dx /= d;
			dy /= d;

			targetScales[k] = -dy;
			targetScales[k+1] = dx;

			k += 2;
		}
		targetScales[0] = targetScales[2];
		targetScales[1] = targetScales[3];
		targetScales[k] = targetScales[k-2];
		targetScales[k+1] = targetScales[k-1];
	}

}
