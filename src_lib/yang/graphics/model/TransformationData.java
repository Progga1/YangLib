package yang.graphics.model;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class TransformationData {

	public Point3f mTranslation = Point3f.ZERO.clone();
	public Quaternion mOrientation = Quaternion.IDENTITY.clone();
	public Vector3f mScale = Vector3f.ONE.clone();

	public void loadIdentity() {
		mTranslation.setZero();
		mOrientation.setIdentity();
		mScale.setOne();
	}

	public void concatTransform(TransformationData rightTransform) {
		mTranslation.add(rightTransform.mTranslation);
		mOrientation.multRight(rightTransform.mOrientation);
		mScale.scale(rightTransform.mScale);
	}

	/**
	 * targetTransform = targetTransform * this
	 */
	public void applyToTransform(TransformationData targetTransform) {
		targetTransform.mTranslation.add(mTranslation);
		targetTransform.mOrientation.multRight(mOrientation);
		targetTransform.mScale.scale(mScale);
	}

	public void setMatrix(YangMatrix targetMatrix) {
		targetMatrix.setTranslation(mTranslation);
		targetMatrix.multiplyQuaternionRight(mOrientation);
		targetMatrix.scale(mScale);
	}

	public void multMatrix(YangMatrix targetMatrix) {
		targetMatrix.translate(mTranslation);
		targetMatrix.multiplyQuaternionRight(mOrientation);
		targetMatrix.scale(mScale);
	}

	public void scale(float factor) {
		mScale.scale(factor);
	}

	public void setScale(float scale) {
		mScale.set(scale);
	}

	@Override
	public String toString() {
		return "t;r;s = "+mTranslation+";"+mOrientation+";"+mScale;
	}

	public YangMatrix createTransform() {
		YangMatrix result = new YangMatrix();
		setMatrix(result);
		return result;
	}

}
