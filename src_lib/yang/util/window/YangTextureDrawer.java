package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.Texture;
import yang.model.callback.Drawable;

public class YangTextureDrawer implements Drawable,RawEventListener {

	public Texture mTexture;
	public float mWidth,mHeight;
	public boolean mFlipX = false;
	public boolean mFlipY = false;
	public DefaultGraphics<?> mGraphics;

	public YangTextureDrawer(DefaultGraphics<?> graphics) {
		mGraphics = graphics;
		setDimensions(1,1);
	}

	public void setDimensions(float width,float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void draw() {
		mGraphics.mTranslator.bindTexture(mTexture);
		final float xFac = mFlipX?-0.5f:0.5f;
		final float yFac = mFlipY?-0.5f:0.5f;
		mGraphics.setWhite();
		mGraphics.drawRect(-mWidth*xFac, -mHeight*yFac, mWidth*xFac, mHeight*yFac);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return true;
	}

}
