package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.model.TransformationData;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;

public class PropertyTransform extends PropertyChain {

	private TransformationData mTransform;
	private PropertyVector3 mPositionComp;
	private PropertyVector3 mScaleComp;
	private PropertyQuaternion mOrientationComp;

	@Override
	protected void postInit() {
		if(!isReferenced()) {
			mTransform = new TransformationData();
		}
		super.postInit();
	}

	@Override
	protected InspectorComponent[] createComponents() {
		mPositionComp = new PropertyVector3();
		mPositionComp.init(this, "Position", mTransform.mPosition);
		mScaleComp = new PropertyVector3();
		mScaleComp.init(this, "Scale", mTransform.mScale);
		mPositionComp.setMouseScrollFactor(0.005f);
		mScaleComp.setMouseScrollFactor(0.005f);
		mOrientationComp = new PropertyQuaternion();
		mOrientationComp.init(this, "Orientation", mTransform.mOrientation);

		return new InspectorComponent[]{mPositionComp,mScaleComp,mOrientationComp};
	}

	@Override
	protected void postValueChanged() {
		super.postValueChanged();
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
	}

	@Override
	public Object getValueReference() {
		return mTransform;
	}

	@Override
	public void setValueFrom(Object value) {
		mTransform.set((TransformationData)value);
	}

}
