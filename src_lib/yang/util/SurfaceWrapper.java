package yang.util;

import yang.events.eventtypes.YangEvent;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.surface.YangSurface;

public class SurfaceWrapper<SurfaceType extends YangSurface> {

	public GraphicsTranslator mGraphics;
	public SurfaceType mInnerSurface;
	public TextureRenderTarget mTexTarget;

	public SurfaceWrapper(SurfaceType surface) {
		mInnerSurface = surface;
	}

	public void init(GraphicsTranslator graphics,int mSurfWidth,int mSurfHeight) {
		mGraphics = graphics;
		mInnerSurface.setGraphics(graphics);
		mInnerSurface.onSurfaceCreated(false);
		mTexTarget = mGraphics.createRenderTarget(mSurfWidth,mSurfHeight,new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR));
	}

	public void draw() {
		mInnerSurface.drawContent(true);
	}

	public void handleEvent(YangEvent event) {
		event.handle(mInnerSurface);
	}

}
