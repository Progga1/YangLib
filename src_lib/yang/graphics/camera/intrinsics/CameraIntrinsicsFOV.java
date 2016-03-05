package yang.graphics.camera.intrinsics;

import yang.math.MathConst;
import yang.pc.tools.runtimeinspectors.DefPropertyNames;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorFrame;
import yang.pc.tools.runtimeinspectors.InspectorPanel;
import yang.pc.tools.runtimeinspectors.components.PropertyMatrix;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyFieldOfView;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyPrincipalPoint;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.util.YangList;

public class CameraIntrinsicsFOV extends CameraIntrinsics {

	public static float fovToFocalLength(float fov,float pixels) {
		return pixels*0.5f/(float)Math.tan(fov/2f);
	}

	public static float focalLengthToFOV(float focalLen,float pixels) {
		return (float)(2*Math.atan(pixels*0.5f/focalLen));
	}

	public static float focalLengthToFactor(float focalLen,float pixels) {
		return (pixels*0.5f)/focalLen;
	}

	//TODO more "privacy"
	//Properties
	public String mName;
	public float mNear = 0.1f;
	public float mFar = 5f;
	public float mProjFacX = -1;
	public float mProjFacY = 0.3f;
	public float mProjShiftX = 0;
	public float mProjShiftY = 0;
	public float mProjRatioX = 1;
	public float mImageWidth = 1;
	public float mImageHeight = 1;
	protected YangList<IntrinsicsListener> mListeners = new YangList<IntrinsicsListener>();

	public CameraIntrinsicsFOV() {
		mName = "Intrinsics";
	}

	public void refreshProjection() {
		for(IntrinsicsListener listener:mListeners) {
			listener.onIntrinsicsChanged(this);
		}
	}

	public float getProjFacX() {
		return mProjFacX;
	}

	public float getProjFacY() {
		return mProjFacY;
	}

	public float getProjShiftX() {
		return mProjShiftX;
	}

	public float getProjShiftY() {
		return mProjShiftY;
	}

	public void updateIntrinsicsMatByFOV() {
		mIntrinsicsMatrix.set(0,0, fovToFocalLength(getFOVX(),mImageWidth));
		mIntrinsicsMatrix.set(1,1, fovToFocalLength(getFOVY(),mImageHeight));
		mIntrinsicsMatrix.set(0,2, getPrincipalPointX());
		mIntrinsicsMatrix.set(1,2, getPrincipalPointY());
	}

	public void updateFOVByIntrinsicsMat() {
		mProjFacX = focalLengthToFactor(getFocalLengthX(),mImageWidth);
		mProjFacY = focalLengthToFactor(getFocalLengthY(),mImageHeight);
		mProjRatioX = mProjFacX/mProjFacY;
		mProjShiftX = getProjShiftX(mImageWidth,mIntrinsicsMatrix.get(0,2));
		mProjShiftY = getProjShiftY(mImageHeight,mIntrinsicsMatrix.get(1,2));
	}

	@Override
	public void setFocalLength(float focalLengthX,float focalLengthY) {
//		setFovByFac(UtilAR.focalLengthToFactor(focalLengthX,mImageWidth),UtilAR.focalLengthToFactor(focalLengthY,mImageHeight));
		setFov(focalLengthToFOV(focalLengthX,mImageWidth),focalLengthToFOV(focalLengthY,mImageHeight));
	}

	public void setProjection(float near, float far, float fovy, float ratio) {
		mNear = near;
		mFar = far;
		mProjRatioX = ratio;
		setFovy(fovy);
	}

	public void setFov(float fovx,float fovy) {
		mProjFacX = (float)Math.tan(fovx);
		mProjFacY = (float)Math.tan(fovy);
		mProjRatioX = mProjFacX/mProjFacY;
		updateIntrinsicsMatByFOV();
		refreshProjection();
	}

	public void setFovy(float fovy) {
		mProjFacY = (float)Math.tan(fovy);
		mProjFacX = mProjFacY*mProjRatioX;
		updateIntrinsicsMatByFOV();
		refreshProjection();
	}

	public void setFovy(float fovy,float ratioX) {
		mProjRatioX = ratioX;
		setFovy(fovy);
	}

	public void setFovx(float fovx,float ratioX) {
		mProjFacX = (float)Math.tan(fovx);
		mProjFacY = mProjFacX/ratioX;
		updateIntrinsicsMatByFOV();
		refreshProjection();
	}

	public void setFovByFac(float xFac,float yFac) {
		mProjFacX = xFac;
		mProjFacY = yFac;
		mProjRatioX = mProjFacX/mProjFacY;
		updateIntrinsicsMatByFOV();
		refreshProjection();
	}

	public float getProjShiftX(float imgWidth,float principalPointX) {
		return 0.5f-principalPointX/imgWidth;
	}

	public float getProjShiftY(float imgHeight,float principalPointY) {
		return -(0.5f-principalPointY/imgHeight);
	}

	public float getImageWidth() {
		return mImageWidth;
	}

	public float getImageHeight() {
		return mImageHeight;
	}

	public void setImageParameters(float imageWidth,float imageHeight) {
		mImageWidth = imageWidth;
		mImageHeight = imageHeight;
		updateIntrinsicsMatByFOV();
		refreshProjection();
	}

	public void setImageParameters(float imageWidth,float imageHeight,float principalPointX, float principalPointY) {
		mProjShiftX = getProjShiftX(imageWidth,principalPointX);
		mProjShiftY = getProjShiftY(imageHeight,principalPointY);
		setImageParameters(imageWidth,imageHeight);
	}

	@Override
	public void setPrincipalPoint(float x,float y) {
		setImageParameters(mImageWidth,mImageHeight,x,y);
	}

	public void setPrincipalPointNorm(float x,float y) {
		setPrincipalPoint(x*mImageWidth,y*mImageHeight);
	}

	@Override
	public float getPrincipalPointX() {
		return (-mProjShiftX+0.5f)*mImageWidth;
	}

	@Override
	public float getPrincipalPointY() {
		return (mProjShiftY+0.5f)*mImageHeight;
	}

	public void setProjRatioX(float ratio) {
		setFovByFac(mProjFacY*ratio,mProjFacY);
	}

	public float getRatioStretchX() {
		return 1;
	}

	public float getRatioX() {
		return mProjRatioX;
	}

	public float getProjXFac() {
		return mProjFacX;
	}

	public float getProjYFac() {
		return mProjFacY;
	}

	public float getFOVShiftX() {
		return mProjShiftX*mProjRatioX;
	}

	public float getFOVShiftY() {
		return mProjShiftY;
	}

	public float getFOVX() {
		return (float)(Math.atan(mProjFacX));
	}

	public float getFOVY() {
		return (float)(Math.atan(mProjFacY));
	}

	public float getNear() {
		return mNear;
	}

	public float getFar() {
		return mFar;
	}

	public void set(CameraIntrinsicsFOV template) {
		mProjFacX = template.mProjFacX;
		mProjFacY = template.mProjFacY;
		mProjRatioX = template.mProjRatioX;
		mProjShiftX = template.mProjShiftX;
		mProjShiftY = template.mProjShiftY;
		mImageWidth = template.mImageWidth;
		mImageHeight = template.mImageHeight;
	}

	public void addListener(IntrinsicsListener listener) {
		if(!mListeners.contains(listener))
			mListeners.add(listener);
	}

}
