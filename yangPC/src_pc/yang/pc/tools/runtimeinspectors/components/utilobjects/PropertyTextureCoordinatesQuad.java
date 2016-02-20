package yang.pc.tools.runtimeinspectors.components.utilobjects;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.objects.Rect;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;

public class PropertyTextureCoordinatesQuad extends PropertyChain {

	public PropertyRect mPropRect;
	public TextureCoordinatesQuad mTexCoords;
	public Rect mLTWH;
	
	@Override
	protected InspectorComponent[] createComponents() {
		mLTWH = new Rect();
		
		mPropRect = new PropertyRect();
		mPropRect.init(this,"Left top width height", true);
		mPropRect.setValueReference(mLTWH);
		
		return new InspectorComponent[]{mPropRect};
	}
	
	public void setValueReference(Object reference) {
		if(reference instanceof TextureCoordinatesQuad) {
			mTexCoords = (TextureCoordinatesQuad)reference;
		}else
			super.setValueReference(reference);
	}
	
	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mTexCoords.init(mLTWH);
	}

	@Override
	protected void refreshInValue() {
		mLTWH.set(mTexCoords.mLeft,mTexCoords.getBottom(),mTexCoords.getRight(),mTexCoords.mTop);
		super.refreshInValue();
	}

	@Override
	public PropertyTextureCoordinatesQuad clone() {
		return new PropertyTextureCoordinatesQuad();
	}

}
