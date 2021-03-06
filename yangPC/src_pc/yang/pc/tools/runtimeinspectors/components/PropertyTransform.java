package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.model.TransformationData;
import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyQuaternion;

public class PropertyTransform extends PropertyChain {

	private TransformationData mTransform;
	private YangMatrix mMatrix = null;
	private PropertyVector3 mPositionComp;
	private PropertyVector3 mScaleComp;
	private PropertyQuaternion mOrientationComp;

	@Override
	protected void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mTransform = new TransformationData();
			updateTrafoRefs();
		}
	}

	@Override
	protected InspectorComponent[] createComponents() {
		mPositionComp = new PropertyVector3();
		mPositionComp.init(this, "Position", true);
		mScaleComp = new PropertyVector3();
		mScaleComp.init(this, "Scale", true);
		mScaleComp.setLinkable();
		mScaleComp.setMaxDigits(3);
		mPositionComp.setScrollFactor(0.005f);
		mScaleComp.setScrollFactor(0.005f);
		mOrientationComp = new PropertyQuaternion();
		mOrientationComp.init(this, "Orientation", true);

		return new InspectorComponent[]{mPositionComp,mScaleComp,mOrientationComp};
	}

	@Override
	protected void refreshInValue() {
		if(mMatrix!=null)
			mTransform.setByMatrix(mMatrix);
		super.refreshInValue();
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		if(mMatrix!=null)
			mTransform.getMatrix(mMatrix);
	}

	@Override
	public Object getValueReference() {
		return mTransform;
	}

	private void updateTrafoRefs() {
		mPositionComp.setValueReference(mTransform.mPosition);
		mScaleComp.setValueReference(mTransform.mScale);
		mOrientationComp.setValueReference(mTransform.mOrientation);
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof YangMatrix) {
			if(mTransform==null)
				mTransform = new TransformationData();
			mMatrix = (YangMatrix)reference;
		}else
			mTransform = (TransformationData)reference;
		updateTrafoRefs();
	}

	@Override
	public void setValueFrom(Object value) {
		if(value instanceof TransformationData)
			mTransform.set((TransformationData)value);
		else if(value instanceof YangMatrix)
			mTransform.setByMatrix((YangMatrix)value);
	}

}
