package yang.pc.tools.fontcreator;

public class LetterBox {
	public int l,r,t,b;
	public int w,h;
	public char c;
	public int k;
	
	public KernBox kBoxes[];
	public int outX1;
	public int outY1;
	public int outX2;
	public int outY2;
	
	LetterBox(char symbol, int left, int top, int right, int bottom, int kernBoxes) {
		c = symbol;
		l = left;
		t = top;
		r = right;
		b = bottom;
		
		w = r-l;
		h = b-t;
		
		k = kernBoxes;
		kBoxes = new KernBox[k];
	}
}
