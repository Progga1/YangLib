package yang.graphics.util;

import yang.math.objects.Point2f;


/**
 * @author Xider
 *
 */
public class Camera2D {
	private static final float MAX_ZOOM = 30;
	private static final float MIN_ZOOM = 1;
	private static final float ZOOM_STEP = 1;
	private Point2f mPos;
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
		return mPos.mX;
	}

	public float getY(){
		return mPos.mY;
	}

	public void setPos(float x, float y) {
		mTarPos.set(x,y);
	}

	public void setPosInstant(float x, float y) {
		mPos.set(x,y);
		mTarPos.set(x,y);
	}

	public void setRotation(float rotation) {
		mTarRotation = rotation;
	}

	public void update(){
		mPos.interpolate(mTarPos, mAdaption);
		mZoom = (1-mAdaption) * mZoom + (mAdaption)*mTarZoom;
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
		mTarPos.mX += x;
		mTarPos.mY += y;
	}

	public void zoom(float factor) {
		mTarZoom += factor;
		limitZoom();
	}

	public boolean isAdjusting() {
		if(mTarPos.getDistance(mPos)>0.1f) return true;
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

	public void scrollInstant(float x, float y) {
		mPos.mX += x;
		mPos.mY += y;
		mTarPos.mX += x;
		mTarPos.mY += y;
	}

	public float getRotation() {
		return mTarRotation;
	}

	public void move(float deltaX, float deltaY) {
		mTarPos.mX += deltaX;
		mTarPos.mY += deltaY;
	}
}
