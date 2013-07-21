package yang.util.lookuptable;

public class LookUpTable {

	public float mStartX;
	public float mEndX;
	public float mStepSize;
	public Function mFunction;
	public float[] mValues;
	private float mXFac;
	
	public LookUpTable(float startX,float endX,float stepSize, Function function) {
		this.mStartX = startX;
		this.mEndX = endX;
		this.mFunction = function;
		this.mValues = new float[(int)((endX-startX)/stepSize)];
		this.mStepSize = (endX-startX)/(this.mValues.length-1);
		create();
	}
	
	private void create() {
		for(int i=0;i<mValues.length;i++) {
			float x = mStartX + i*mStepSize;
			mValues[i] = mFunction.evaluate(x);
		}
		this.mXFac = 1f/(mEndX-mStartX)*mValues.length;
	}
	
	public float get(float x) {
		int i = (int)((x-mStartX)*mXFac);
		if(i<0)
			i=0;
		if(i>=mValues.length)
			i=mValues.length-1;
		return mValues[i];
	}
	
}
