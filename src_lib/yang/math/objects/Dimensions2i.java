package yang.math.objects;

public class Dimensions2i {

	public int mWidth;
	public int mHeight;
	
	public Dimensions2i(int width,int height) {
		set(width,height);
	}
	
	public Dimensions2i() {
		this(0,0);
	}
	
	public void set(int width,int height) {
		mWidth = width;
		mHeight = height;
	}
	
}
