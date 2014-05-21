package yang.graphics.interfaces;

import yang.graphics.textures.TextureData;

public interface ScreenshotCallback {

	public TextureData getScreenshotTarget(int originalWidth, int originalHeight);
	public void onScreenshot(TextureData data);

}
