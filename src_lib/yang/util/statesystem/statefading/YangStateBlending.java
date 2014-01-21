package yang.util.statesystem.statefading;

import yang.events.eventtypes.YangEvent;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class YangStateBlending<StateSystemType extends YangProgramStateSystem> extends YangStateFader<StateSystemType> {

	public YangStateBlending(float transitionTime) {
		super(transitionTime);
	}

	@Override
	protected boolean prepareStateDraw(YangProgramState<StateSystemType> state,float fade) {
		if(mFromState!=null)
			mGraphics2D.setColorFactor(1,1,1,fade);
		else
			mGraphics2D.setColorFactor(fade);
		return true;
	}

	@Override
	protected void refreshProgress(float deltaTime,float toWeight) {
		if(mFromState!=null) {
			mFromState.mFadeProgress = 1-toWeight;
			mFromState.proceed(deltaTime);
		}
		if(mToState!=null) {
			mToState.mFadeProgress = toWeight;
			mToState.proceed(deltaTime);
		}
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		if(mToState!=null)
			return event.handle(mToState);
		else
			return false;
	}

}
