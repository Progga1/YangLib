package yang.pc.tools.runtimeinspectors.components.utilobjects;

import yang.graphics.font.DrawableString;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyComboBox;
import yang.pc.tools.runtimeinspectors.components.PropertyTextField;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyDrawableString extends PropertyChain {

	private DrawableString mDrawableString;

	private PropertyTextField mPropText;
	private PropertyComboBox mPropAnchorHorizontal;
	private PropertyComboBox mPropAnchorVertical;
	private PropertyNumArray mPropLenCapacity;

	@Override
	protected InspectorComponent[] createComponents() {
		if(!isReferenced()) {
			throw new RuntimeException("Drawable string property must be referenced.");
		}
		mPropText = new PropertyTextField();
		mPropText.init(this,"Text",true);
		mPropAnchorHorizontal = new PropertyComboBox("Left","Center","Right");
		mPropAnchorHorizontal.init(this,"Horizontal anchor",false);
		mPropAnchorVertical = new PropertyComboBox("Top","Middle","Bottom");
		mPropAnchorVertical.init(this,"Vertical anchor",false);
		mPropLenCapacity = new PropertyNumArray(2);
		mPropLenCapacity.init(this,"Length | capacity",false);
		mPropLenCapacity.setMaxDigits(0);
		mPropLenCapacity.setReadOnly(true);

		return new InspectorComponent[]{mPropText,mPropAnchorHorizontal,mPropAnchorVertical,mPropLenCapacity};
	}

	@Override
	public void setValueReference(Object drawableString) {
		if(drawableString instanceof DrawableString)
			mDrawableString = (DrawableString)drawableString;
		else
			super.setValueReference(drawableString);
	}

	@Override
	public void refreshInValue() {
		mPropText.setString(mDrawableString.getOriginalString());
		mPropAnchorHorizontal.setInt(mDrawableString.getHorizonalAnchorInt());
		mPropAnchorVertical.setInt(mDrawableString.getVerticalAnchorInt());
		mPropLenCapacity.setFloat(0,mDrawableString.getLength());
		mPropLenCapacity.setFloat(1,mDrawableString.getCapacity());
		super.refreshInValue();
	}

	@Override
	public void refreshOutValue() {
		super.refreshOutValue();
		mDrawableString.setString(mPropText.getString());
		mDrawableString.setAnchorsInt(mPropAnchorHorizontal.getInt(),mPropAnchorVertical.getInt());
	}

	@Override
	public Object getValueReference() {
		return mDrawableString;
	}

	@Override
	public String getString() {
		return mDrawableString.getOriginalString();
	}

}
