package yang.graphics.interfaces;

import yang.graphics.textures.TextureData;
import yang.util.ImageCaptureData;

public interface ScreenshotCallback {

	public ImageCaptureData getScreenshotTarget(int originalWidth, int originalHeight, float minRatioX);
	public void onScreenshot(TextureData data);

}
