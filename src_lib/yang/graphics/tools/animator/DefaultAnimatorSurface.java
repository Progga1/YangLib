package yang.graphics.tools.animator;

import yang.events.eventtypes.YangEvent;
import yang.graphics.defaults.DefaultSurface;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.interfaces.InitializationCallback;
import yang.sound.SoundManager;

public class DefaultAnimatorSurface extends DefaultSurface {

	public SoundManager mSound;
	public Animator mAnimator;
	private InitializationCallback mInitCallback;
	public float mSpeedFactor = 1;
	
	public DefaultAnimatorSurface(SoundManager sound,InitializationCallback initCallback) {
		super(true,false);
		mSound = sound;
		mInitCallback = initCallback;
	}
	
	@Override
	public void draw() {
		if(mAnimator!=null)
			mAnimator.draw();
	}
	
	@Override
	public void step(float deltaTime) {
		super.step(deltaTime);
		mAnimator.step(deltaTime*mSpeedFactor);
	}
	
	@Override
	protected void postInitGraphics() {
		super.postInitGraphics();
		DrawableString.DEFAULT_PROPERTIES = new StringProperties(mGraphics2D,mGFXLoader.loadFont("belligerent"));
		mAnimator = new Animator(mGraphics2D);
		mAnimator.mSound = mSound;
		if(mInitCallback!=null)
			mInitCallback.initializationFinished();
	}
	
	@Override
	public boolean rawEvent(YangEvent event) {
		event.handle(mAnimator);
		return true;
	}
	
	@Override
	public void keyDown(int keyCode) {
		if(keyCode==65535)
			mSpeedFactor = 0.25f;
	}
	
	@Override
	public void keyUp(int keyCode) {
		if(keyCode==65535)
			mSpeedFactor = 1;
	}
	
}
