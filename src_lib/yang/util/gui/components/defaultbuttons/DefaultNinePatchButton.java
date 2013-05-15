package yang.util.gui.components.defaultbuttons;

import yang.graphics.translator.Texture;
import yang.graphics.util.ninepatch.NinePatchGrid;

public class DefaultNinePatchButton extends DefaultCaptionButton {

	public Texture mTexture;
	public NinePatchGrid mNinePatch;
	public NinePatchGrid mNinePatchPressed;
	public NinePatchGrid mNinePatchDisabled;
	
	public DefaultNinePatchButton setTexture(Texture texture) {
		mTexture = texture;
		return this;
	}
	
	public DefaultNinePatchButton setNinePatch(NinePatchGrid ninePatch) {
		mNinePatch = ninePatch;
		mNinePatchPressed = ninePatch;
		mNinePatchDisabled = ninePatch;
		return this;
	}
	
	public DefaultNinePatchButton setNinePatchPressed(NinePatchGrid ninePatch) {
		mNinePatchPressed = ninePatch;
		return this;
	}
	
	public DefaultNinePatchButton setNinePatchDisabled(NinePatchGrid ninePatch) {
		mNinePatchDisabled = ninePatch;
		return this;
	}
	
	@Override
	public void draw() {
		mGraphics2D.bindTexture(mTexture);
		NinePatchGrid uNinePatch;
		if(!mEnabled)
			uNinePatch = mNinePatchDisabled;
		else if(mPressedTime>0)
			uNinePatch = mNinePatchPressed;
		else
			uNinePatch = mNinePatch;
		uNinePatch.draw(mProjLeft,mProjBottom,mProjLeft+mProjWidth,mProjBottom+mProjHeight);
		drawCaption();
	}
	
}
