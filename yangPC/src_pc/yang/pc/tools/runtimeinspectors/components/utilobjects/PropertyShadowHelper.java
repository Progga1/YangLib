package yang.pc.tools.runtimeinspectors.components.utilobjects;

import yang.graphics.defaults.programs.helpers.ShadowHelper;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyTexture;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyShadowHelper extends PropertyChain {

	protected ShadowHelper mShadowHelper;

	public PropertyTexture mRenderTargetProp;
	public PropertyFloatNum mBiasProp;
	public PropertyNumArray mLightDirProp;

	@Override
	protected InspectorComponent[] createComponents() {
		mRenderTargetProp = new PropertyTexture();
		mRenderTargetProp.init(this,"Depth map",true);
		mBiasProp = new PropertyFloatNum();
		mBiasProp.init(this,"Bias",false);
		mBiasProp.setScrollFactor(0.001f);
		mLightDirProp = new PropertyNumArray(4);
		mLightDirProp.init(this,"Light direction",true);
		mLightDirProp.setReadOnly(true);

		return new InspectorComponent[]{mRenderTargetProp,mBiasProp,mLightDirProp};
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof ShadowHelper) {
			mShadowHelper = (ShadowHelper)reference;
			mRenderTargetProp.setValueReference(mShadowHelper.mDepthMap);
			mLightDirProp.setValueReference(mShadowHelper.mLightDirection);
		}else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mShadowHelper.setBias(mBiasProp.getFloat());
	}

	@Override
	protected void refreshInValue() {
		mBiasProp.setFloat(mShadowHelper.getBias());
		super.refreshInValue();
	}

	@Override
	public PropertyShadowHelper clone() {
		return new PropertyShadowHelper();
	}
}
