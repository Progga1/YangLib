package yang.pc.tools.runtimeinspectors.components.utilobjects;

import yang.math.objects.Rect;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArrayBase;

public class PropertyRect extends PropertyNumArrayBase {

	private Rect mRect;

	public boolean mYDown = false;

	public PropertyRect() {
		super(4);
	}

	public PropertyRect setYDown(boolean yDownwards) {
		mYDown = yDownwards;
		return this;
	}

	@Override
	public void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mRect = new Rect(0,0,1,1);
		}
	}

	@Override
	public void setLinkable() {
		super.setLinkable();
		mTextFields[3].unlink();
	}

	@Override
	protected void refreshInValue() {
		mTextFields[0].setFloat(mRect.mLeft);
		mTextFields[2].setFloat(mRect.getWidth());
		if(mYDown) {
			mTextFields[1].setFloat(mRect.mTop);
			mTextFields[3].setFloat(mRect.getHeightYDown());
		}else{
			mTextFields[1].setFloat(mRect.mBottom);
			mTextFields[3].setFloat(mRect.getHeight());
		}
	}

	@Override
	public void refreshOutValue() {
		mRect.mLeft = mTextFields[0].getFloat();
		mRect.setWidthAnchorLeft(mTextFields[2].getFloat());
		if(mYDown) {
			mRect.mTop = mTextFields[1].getFloat();
			mRect.setHeightAnchorTop(-mTextFields[3].getFloat());
		}else{
			mRect.mBottom = mTextFields[1].getFloat();
			mRect.setHeightAnchorBottom(mTextFields[3].getFloat());
		}
	}

	@Override
	public Object getValueReference() {
		return mRect;
	}

	@Override
	public void setValueFrom(Object value) {
		mRect.set((Rect)value);
	}

	@Override
	public void setValueReference(Object reference) {
		mRect = (Rect)reference;
	}

	@Override
	public PropertyRect clone() {
		return new PropertyRect().setYDown(mYDown);
	}

}
