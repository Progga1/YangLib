package yang.graphics.font;

public class MarkInfo {

	public int mPos;
	public int mLength;
	
	public MarkInfo(int pos,int length) {
		mPos = pos;
		mLength = length;
	}
	
	@Override
	public String toString() {
		return "pos="+mPos+",len="+mLength;
	}
	
}
