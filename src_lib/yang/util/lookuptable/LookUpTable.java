package yang.util.lookuptable;

public class LookUpTable {

	public float startX;
	public float endX;
	public float stepSize;
	public Function function;
	public float[] values;
	private float xFac;
	
	public LookUpTable(float startX,float endX,float stepSize, Function function) {
		this.startX = startX;
		this.endX = endX;
		this.function = function;
		this.stepSize = stepSize;
		this.values = new float[(int)((endX-startX)/stepSize)];
		this.stepSize = (endX-startX)/(this.values.length-1);
		create();
	}
	
	private void create() {
		for(int i=0;i<values.length;i++) {
			float x = startX + (endX-startX)*i*stepSize;
			values[i] = function.evaluate(x);
		}
		this.xFac = 1f/(endX-startX)*values.length;
	}
	
	public float get(float x) {
		int i = (int)((x-startX)*xFac);
		if(i<0)
			i=0;
		if(i>=values.length)
			i=values.length-1;
		return values[i];
	}
	
}
