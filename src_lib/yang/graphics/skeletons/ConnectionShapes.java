package yang.graphics.skeletons;


public class ConnectionShapes {

	protected int mSegmentCount;
	public float mShape1[];
	public float mShape2[];

	public ConnectionShapes(int segments) {
		mSegmentCount = segments;
		mShape1 = new float[segments*2];
		mShape2 = new float[segments*2];
	}

	public int getSegmentCount() {
		return mSegmentCount;
	}

}
