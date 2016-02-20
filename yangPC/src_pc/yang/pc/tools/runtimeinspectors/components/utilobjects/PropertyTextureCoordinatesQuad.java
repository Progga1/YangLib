package yang.pc.tools.runtimeinspectors.components.utilobjects;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.objects.Rect;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyBooleanCheckBox;
import yang.pc.tools.runtimeinspectors.components.PropertyComboBox;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyTextureCoordinatesQuad extends PropertyChain {

	public TextureCoordinatesQuad mTexCoords;
	public PropertyRect mPropRect;
	public PropertyNumArray mPropBias;
	public PropertyBooleanCheckBox mPropFlipX;
	public PropertyBooleanCheckBox mPropFlipY;
	public PropertyComboBox mPropRotation;
	public Rect mLTWH;

	@Override
	protected InspectorComponent[] createComponents() {
		mLTWH = new Rect();

		mPropRect = new PropertyRect();
		mPropRect.init(this,"Left top width height", true);
		mPropRect.setValueReference(mLTWH);
		mPropRect.setYDown(true);

		mPropBias = new PropertyNumArray(2);
		mPropBias.init(this, "Bias X Y", false);
		mPropBias.setLinkable();
		mPropBias.setLinkingActive(true);
		mPropBias.setScrollFactor(0.001f);

		mPropFlipX = new PropertyBooleanCheckBox();
		mPropFlipY = new PropertyBooleanCheckBox();
		mPropFlipX.init(this, "Flip X", false);
		mPropFlipY.init(this, "Flip Y", false);

		mPropRotation = new PropertyComboBox("None","CW90","180","CCW90");
		mPropRotation.init(this,"Rotation",false);

		return new InspectorComponent[]{mPropRect,mPropBias,mPropFlipX,mPropFlipY,mPropRotation};
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof TextureCoordinatesQuad) {
			mTexCoords = (TextureCoordinatesQuad)reference;
		}else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mTexCoords.init(mLTWH,mPropBias.getFloat(0),mPropBias.getFloat(1));
		mTexCoords.setFlipped(mPropFlipX.getBool(),mPropFlipY.getBool());
		mTexCoords.setRotation(mPropRotation.getInt());
	}

	@Override
	protected void refreshInValue() {
		mLTWH.set(mTexCoords.mLeft,mTexCoords.getBottom(),mTexCoords.getRight(),mTexCoords.mTop);
		mPropBias.setFloat(0,mTexCoords.mBiasX);
		mPropBias.setFloat(1,mTexCoords.mBiasY);
		mPropFlipX.setBool(mTexCoords.isFlippedX());
		mPropFlipY.setBool(mTexCoords.isFlippedY());
		mPropRotation.setInt(mTexCoords.getRotation());
		super.refreshInValue();
	}

	@Override
	public PropertyTextureCoordinatesQuad clone() {
		return new PropertyTextureCoordinatesQuad();
	}

}
