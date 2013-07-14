package yang.model.state;

import yang.graphics.listeners.DrawListener;
import yang.graphics.translator.Texture;

public class GraphicsState {

	public DrawListener mDrawListener;
	public boolean mCulling,mZBuffer,mFlushDisabled,mForceWireFrames;
	public int mCullFunc;
	public Texture[] mCurrentTextures;
	
}
