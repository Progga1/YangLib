package yang.graphics.util;

import javax.vecmath.Point2f;

import yang.graphics.defaults.Default2DGraphics;


/**
 * @author Xider
 *
 */
public class Camera2D {
	private static final float MAX_ZOOM = 30;
	private static final float MIN_ZOOM = 1;
	private static final float ZOOM_STEP = 1;
	public Point2f mPos;
	public Point2f mTarPos;
	private float mTarRotation;
	
	private float mZoom;
	private float mTarZoom;
	public float mAdaption;
	
	public Camera2D(float startX, float startY, float zoom){
		mPos = new Point2f(startX,startY);
		mTarPos = new Point2f(mPos);
		
		this.mZoom = zoom;
		this.setZoom(zoom);
		
		mTarRotation = 0;
		mAdaption = 0.1f;
	}
	
	public Camera2D() {
		this(0,0,1);
	}

	public float getZoom() {
		return mZoom;
	}

	public void setZoom(float zoom) {
		this.mTarZoom = zoom;
	}
	
	public float getX(){
		return mPos.x;
	}
	
	public float getY(){
		return mPos.y;
	}
	
	public void setPos(float x, float y) {
		mTarPos.set(x,y);
	}
	
	public void setRotation(float rotation) {
		mTarRotation = rotation;
	}
	
	public void render(Default2DGraphics g){
		g.setCamera(mPos.x, mPos.y, mZoom);
	}
	
	public void update(){		
		mPos.interpolate(mTarPos, mAdaption);
		mZoom = mAdaption * mZoom + (1-mAdaption)*mTarZoom;
	}
	
	public void zoomOut(){
		mTarZoom += ZOOM_STEP;
		limitZoom();
	}
	
	public void zoomIn(){
		mTarZoom -= ZOOM_STEP;		
	}
	
	private void limitZoom(){
		if(mTarZoom > MAX_ZOOM) mTarZoom = MAX_ZOOM;
		if(mTarZoom < MIN_ZOOM) mTarZoom = MIN_ZOOM;
	}

	public void scroll(float x, float y) {
		mTarPos.x += x;
		mTarPos.y += y;
	}

	public void zoom(float factor) {
		mTarZoom += factor;
		limitZoom();
	}

	public boolean isAdjusting() {
		if(mTarPos.distance(mPos)>0.1f) return true;
		return false;
	}

	public void set(float x, float y, float zoom, float rotation) {
		setPos(x,y);
		setZoom(zoom);
		setRotation(rotation);
	}
	
	public void set(float x, float y, float zoom) {
		set(x,y,zoom,0);
	}
	


	public float getRotation() {
		return mTarRotation;
	}

	public void move(float deltaX, float deltaY) {
		mTarPos.x += deltaX;
		mTarPos.y += deltaY;
	}
}
