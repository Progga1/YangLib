package yang.graphics.camera.intrinsics;

import yang.math.MathConst;
import yang.pc.tools.runtimeinspectors.DefPropertyNames;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorFrame;
import yang.pc.tools.runtimeinspectors.InspectorManager;
import yang.pc.tools.runtimeinspectors.InspectorPanel;
import yang.pc.tools.runtimeinspectors.components.PropertyMatrix;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyFieldOfView;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyPrincipalPoint;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.util.YangList;

public class CameraIntrinsicsFOV extends CameraIntrinsics implements InspectionInterface {

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
	public float mCurPrincipalImageWidth = 1;
	public float mCurPrincipalImageHeight = 1;
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
		refreshProjection();
	}

	public void setFovy(float fovy) {
		mProjFacY = (float)Math.tan(fovy);
		mProjFacX = mProjFacY*mProjRatioX;
		refreshProjection();
	}

	public void setFovy(float fovy,float ratioX) {
		mProjRatioX = ratioX;
		setFovy(fovy);
	}

	public void setFovx(float fovx,float ratioX) {
		mProjFacX = (float)Math.tan(fovx);
		mProjFacY = mProjFacX/ratioX;
		refreshProjection();
	}

	public void setFovByFac(float xFac,float yFac) {
		mProjFacX = xFac;
		mProjFacY = yFac;
		mProjRatioX = mProjFacX/mProjFacY;
		refreshProjection();
	}

	public float getProjShiftX(float imgWidth,float principalPointX) {
		return 0.5f-principalPointX/imgWidth;
	}

	public float getProjShiftY(float imgHeight,float principalPointY) {
		return -(0.5f-principalPointY/imgHeight);
	}

	public float getImageWidth() {
		return mCurPrincipalImageWidth;
	}

	public float getImageHeight() {
		return mCurPrincipalImageHeight;
	}

	public void setImageParameters(float imageWidth,float imageHeight,float principalPointX, float principalPointY) {
		mProjShiftX = getProjShiftX(imageWidth,principalPointX);
		mProjShiftY = getProjShiftY(imageHeight,principalPointY);
		mCurPrincipalImageWidth = imageWidth;
		mCurPrincipalImageHeight = imageHeight;
		refreshProjection();
	}

	public void setPrincipalPoint(float x,float y) {
		setImageParameters(mCurPrincipalImageWidth,mCurPrincipalImageHeight,x,y);
	}

	public void setPrincipalPointNorm(float x,float y) {
		setPrincipalPoint(x*mCurPrincipalImageWidth,y*mCurPrincipalImageHeight);
	}

	public float getPrincipalPointX() {
		return (-mProjShiftX+0.5f)*mCurPrincipalImageWidth;
	}

	public float getPrincipalPointY() {
		return (mProjShiftY+0.5f)*mCurPrincipalImageHeight;
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
		mCurPrincipalImageWidth = template.mCurPrincipalImageWidth;
		mCurPrincipalImageHeight = template.mCurPrincipalImageHeight;
	}
	
	//---INSPECTOR---

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getTypeName() {
		return "CameraIntrinsics";
	}
	
	@Override
	public Object getReferencedProperty(String propertyName,InspectorComponent sender) {
		if(propertyName==DefPropertyNames.MATRIX)
			return mIntrinsicsMatrix;
		else
			return null;
	}

	@Override
	public void readProperty(String propertyName,InspectorComponent target) {
		if(target.is(DefPropertyNames.NEAR))
			target.setFloat(mNear);
		else if(target.is(DefPropertyNames.FAR))
			target.setFloat(mFar);
		else if(target.is(DefPropertyNames.FOV_XY)) {
			float[] fovXY = (float[])target.getValue();
			fovXY[0] = getFOVX()*MathConst.TO_DEG;
			fovXY[1] = getFOVY()*MathConst.TO_DEG;
		}else if(target.is(DefPropertyNames.IMAGE_PARAMETERS)) {
			float[] fovShift = (float[])target.getValue();
			fovShift[0] = mCurPrincipalImageWidth;
			fovShift[1] = mCurPrincipalImageHeight;
			fovShift[2] = getPrincipalPointX();
			fovShift[3] = getPrincipalPointY();
		}
	}

	@Override
	public void setProperty(String propertyName,InspectorComponent value) {
		if(value.is(DefPropertyNames.FOV)) {
			setFovy(value.getFloat()/180*MathConst.PI);
		}else if(value.is(DefPropertyNames.NEAR)) {
			mNear = value.getFloat();
			refreshProjection();
		}else if(value.is(DefPropertyNames.FAR)) {
			mFar = value.getFloat();
			refreshProjection();
		}
		else if(value.is(DefPropertyNames.FOV_XY)) {
			float[] vals = (float[])value.getValue();
			setFov(vals[0]/180*MathConst.PI,vals[1]/180*MathConst.PI);
		}else if(value.is(DefPropertyNames.IMAGE_PARAMETERS)) {
			float[] vals = (float[])value.getValue();
			setImageParameters((int)vals[0],(int)vals[1],vals[2],vals[3]);
		}
	}

	public static InspectorPanel createInspector(InspectorFrame frame) {
		InspectorPanel inspector = frame.createInspector();
		inspector.registerProperty(DefPropertyNames.FOV_XY, new PropertyFieldOfView());
		inspector.registerProperty(DefPropertyNames.NEAR, Float.class);
		inspector.registerProperty(DefPropertyNames.FAR, Float.class);
		inspector.registerProperty(DefPropertyNames.IMAGE_PARAMETERS, new PropertyPrincipalPoint());
		inspector.registerPropertyReferenced(DefPropertyNames.MATRIX, new PropertyMatrix(3,3));
		
		return inspector;
	}

	public void addListener(IntrinsicsListener listener) {
		if(!mListeners.contains(listener))
			mListeners.add(listener);
	}
	
}
