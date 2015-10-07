package yang.graphics.model;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class TransformationData {

	public Point3f mPosition = Point3f.ZERO.clone();
	public Quaternion mOrientation = Quaternion.IDENTITY.clone();
	public Vector3f mScale = Vector3f.ONE.clone();

	public void loadIdentity() {
		mPosition.setZero();
		mOrientation.setIdentity();
		mScale.setOne();
	}

	public void concatTransform(TransformationData rightTransform) {
		mPosition.add(rightTransform.mPosition);
		mOrientation.multRight(rightTransform.mOrientation);
		mScale.scale(rightTransform.mScale);
	}

	/**
	 * targetTransform = targetTransform * this
	 */
	public void applyToTransform(TransformationData targetTransform) {
		targetTransform.mPosition.add(mPosition);
		targetTransform.mOrientation.multRight(mOrientation);
		targetTransform.mScale.scale(mScale);
	}

	public void getMatrix(YangMatrix targetMatrix) {
		targetMatrix.setTranslation(mPosition);
		targetMatrix.multiplyQuaternionRight(mOrientation);
		targetMatrix.scale(mScale);
	}

	public void multMatrix(YangMatrix targetMatrix) {
		targetMatrix.translate(mPosition);
		targetMatrix.multiplyQuaternionRight(mOrientation);
		targetMatrix.scale(mScale);
	}

	public void scale(float factor) {
		mScale.scale(factor);
	}

	public void setScale(float scale) {
		mScale.set(scale);
	}

	public void setBounds(float minX,float maxX, float minY,float maxY, float minZ,float maxZ) {
		mOrientation.setIdentity();
		mPosition.set((minX+maxX)*0.5f,(minY+maxY)*0.5f,(minZ+maxZ)*0.5f);
		mScale.set(maxX-minX,maxY-minY,maxZ-minZ);
	}
	
	@Override
	public String toString() {
		return "t;r;s = "+mPosition+";"+mOrientation+";"+mScale;
	}

	public YangMatrix createTransform() {
		YangMatrix result = new YangMatrix();
		getMatrix(result);
		return result;
	}

}
