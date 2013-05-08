package yang.util.statesystem;

import yang.events.eventtypes.YangInputEvent;
import yang.graphics.defaults.DefaultSurface;

public class YangProgramStateSystem extends DefaultSurface {

	private YangProgramState<?> mCurrentState;
	
	protected YangProgramStateSystem(boolean init2dGraphics, boolean init3dGraphics) {
		super(init2dGraphics, init3dGraphics);
	}
	
	@SuppressWarnings("unchecked")
	public <ThisType extends YangProgramStateSystem> void setState(YangProgramState<ThisType> newState) {
		if(!newState.isInitialized())
			newState.init((ThisType)this);
		if(mCurrentState!=null)
			mCurrentState.stop();
		mCurrentState = newState;
		mCurrentState.start();
	}
	
	public YangProgramState<?> getCurrentState() {
		return mCurrentState;
	}
	
	public void step(float deltaTime) {
		super.step(deltaTime);
		if(mCurrentState!=null)
			mCurrentState.proceed(deltaTime);
	}
	
	public void draw() {
		if(mCurrentState!=null) {
			mCurrentState.drawFrame();
		}
	}
	
	@Override
	public void rawEvent(YangInputEvent event) {
		if(mCurrentState!=null)
			event.handle(mCurrentState);
	}
	
}
