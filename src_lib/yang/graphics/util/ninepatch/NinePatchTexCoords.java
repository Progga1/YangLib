package yang.graphics.util.ninepatch;

public class NinePatchTexCoords {

	public float[] mTexCoords = new float[32];
	private float mLeft,mTop,mRight,mBottom;
	private float mBorderLeft,mBorderTop,mBorderRight,mBorderBottom;
	private float mTexWidth,mTexHeight;
	private float mBiasX,mBiasY;
	
	public NinePatchTexCoords() {
		
	}
	
	public NinePatchTexCoords init(float left,float top,float right,float bottom, float biasX,float biasY) {
		mBiasX = biasY;
		mBiasY = biasY;
		mLeft = left+biasX;
		mTop = top+biasY;
		mRight = right-biasX;
		mBottom = bottom-biasY;
		mTexWidth = 1;
		mTexHeight = 1;
		return this;
	}
	
	public NinePatchTexCoords init(float left,float top,float right,float bottom) {
		return init(left,top,right,bottom, (right-left)*0.003f,(top-bottom)*0.003f);
	}
	
	public void refreshTexCoords() {
		mTexCoords[0] = mLeft;
		mTexCoords[1] = mBottom;
		mTexCoords[2] = mLeft+mBorderLeft;
		mTexCoords[3] = mBottom;
		mTexCoords[4] = mRight-mBorderRight;
		mTexCoords[5] = mBottom;
		mTexCoords[6] = mRight;
		mTexCoords[7] = mBottom;
		
		mTexCoords[8] = mLeft;
		mTexCoords[9] = mBottom-mBorderBottom;
		mTexCoords[10] = mLeft+mBorderLeft;
		mTexCoords[11] = mBottom-mBorderBottom;
		mTexCoords[12] = mRight-mBorderRight;
		mTexCoords[13] = mBottom-mBorderBottom;
		mTexCoords[14] = mRight;
		mTexCoords[15] = mBottom-mBorderBottom;
		
		mTexCoords[16] = mLeft;
		mTexCoords[17] = mTop+mBorderTop;
		mTexCoords[18] = mLeft+mBorderLeft;
		mTexCoords[19] = mTop+mBorderTop;
		mTexCoords[20] = mRight-mBorderRight;
		mTexCoords[21] = mTop+mBorderTop;
		mTexCoords[22] = mRight;
		mTexCoords[23] = mTop+mBorderTop;
		
		mTexCoords[24] = mLeft;
		mTexCoords[25] = mTop;
		mTexCoords[26] = mLeft+mBorderLeft;
		mTexCoords[27] = mTop;
		mTexCoords[28] = mRight-mBorderRight;
		mTexCoords[29] = mTop;
		mTexCoords[30] = mRight;
		mTexCoords[31] = mTop;
		
	}
	
	public NinePatchTexCoords setBorder(float left,float top,float right,float bottom) {
		mBorderLeft = left/mTexWidth;
		mBorderTop = top/mTexHeight;
		mBorderRight = right/mTexWidth;
		mBorderBottom = bottom/mTexHeight;
		refreshTexCoords();
		return this;
	}
	
	public NinePatchTexCoords setBorder(float size) {
		setBorder(size,size,size,size);
		return this;
	}
	
	public NinePatchTexCoords setBorder(float horizontal,float vertical) {
		return setBorder(horizontal,vertical,horizontal,vertical);
	}

	public NinePatchTexCoords cloneWithOffset(float offsetX, float offsetY) {
		NinePatchTexCoords result = new NinePatchTexCoords();
		result.init(mLeft+offsetX, mTop+offsetY, mRight+offsetX, mBottom+offsetY, mBiasX,mBiasY);
		result.setBorder(mBorderLeft, mBorderTop, mBorderRight, mBorderBottom);
		return result;
	}
	
}
