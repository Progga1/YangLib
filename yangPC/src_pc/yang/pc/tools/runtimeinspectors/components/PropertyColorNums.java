package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.model.FloatColor;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArrayBase;

public class PropertyColorNums extends PropertyNumArrayBase {

	private FloatColor mColor;

	public PropertyColorNums() {
		super(4);
	}

	@Override
	public void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mColor = new FloatColor(1);
		}
		setDefaultValue(1);
		setMaxDigits(3);
		setMinValue(0);
		setMaxValue(5);
		setScrollFactor(0.01f);
		setClickSteps(0.1f);
		setLinkable();
	}

	@Override
	public PropertyColorNums setLinkable() {
		super.setLinkable();
		mTextFields[3].unlink();
		return this;
	}



	@Override
	protected void refreshInValue() {
		mTextFields[0].setFloat(mColor.mValues[0]);
		mTextFields[1].setFloat(mColor.mValues[1]);
		mTextFields[2].setFloat(mColor.mValues[2]);
		mTextFields[3].setFloat(mColor.mValues[3]);
	}

	@Override
	public void refreshOutValue() {
		mColor.set(
				mTextFields[0].getFloat(),
				mTextFields[1].getFloat(),
				mTextFields[2].getFloat(),
				mTextFields[3].getFloat()
				);

	}

	@Override
	public Object getValueReference() {
		return mColor;
	}

	@Override
	public void setValueFrom(Object value) {
		mColor.set((FloatColor)value);
	}

	@Override
	public void setValueReference(Object reference) {
		mColor = (FloatColor)reference;
	}

}
