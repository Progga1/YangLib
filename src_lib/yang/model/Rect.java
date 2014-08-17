package yang.model;

public class Rect {

	public float mLeft,mRight,mTop,mBottom;

	public Rect(float left,float bottom,float right, float top) {
		set(left,bottom,right,top);
	}

	public Rect(Rect preface) {
		set(preface);
	}

	public Rect() {
		this(0,0,0,0);
	}

	public Rect(float width,float height) {
		this(-width*0.5f,-height*0.5f,width*0.5f,height*0.5f);
	}

	public Rect(float widthAndHeight) {
		this(-widthAndHeight*0.5f,-widthAndHeight*0.5f,widthAndHeight*0.5f,widthAndHeight*0.5f);
	}

	public float getWidth() {
		return mRight-mLeft;
	}

	public float getHeight() {
		return mTop-mBottom;
	}
	
	public void move(float deltaX, float deltaY) {
		mLeft += deltaX;
		mRight += deltaX;
		mBottom += deltaY;
		mTop += deltaY;
	}

	public void set(float left,float bottom,float right, float top) {
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
	}

	public void set(Rect preface) {
		mLeft = preface.mLeft;
		mRight = preface.mRight;
		mTop = preface.mTop;
		mBottom = preface.mBottom;
	}

	public void multiplyMiddle(float factor) {
		float midX = (mRight+mLeft)*0.5f;
		float midY = (mTop+mBottom)*0.5f;
		mLeft = (mLeft-midX)*factor+midX;
		mTop = (mTop-midY)*factor+midY;
		mRight = (mRight-midX)*factor+midX;
		mBottom = (mBottom-midY)*factor+midY;
	}

	public void setWidth(float width) {
		float midX = (mRight+mLeft)*0.5f;
		float factor = width/(mRight-mLeft);
		mLeft = (mLeft-midX)*factor+midX;
		mRight = (mRight-midX)*factor+midX;
	}

	public void setHeight(float height) {
		float midY = (mTop+mBottom)*0.5f;
		float factor = height/(mTop-mBottom);
		mTop = (mTop-midY)*factor+midY;
		mBottom = (mBottom-midY)*factor+midY;
	}

	public float getCenterX() {
		return (mLeft+mRight)*0.5f;
	}

	public float getCenterY() {
		return (mTop+mBottom)*0.5f;
	}

	@Override
	public String toString() {
		return "LTRB("+mLeft+","+mBottom+","+mRight+","+mTop+")";
	}

	public float normX(float x) {
		return (x-mLeft)/(mRight-mLeft);
	}

	public float normY(float y) {
		return (y-mBottom)/(mTop-mBottom);
	}

}
