package yang.util.statesystem;

import yang.events.eventtypes.YangEvent;

public class YangSubStateChain<StateMachineType extends YangProgramStateSystem> extends YangProgramState<StateMachineType> {

	public YangProgramState<StateMachineType>[] mStates;
	public boolean[] mStatesActive;

	private int mStateCount;

	@SuppressWarnings("unchecked")
	public YangSubStateChain(int capacity) {
		mStates = new YangProgramState[capacity];
		mStatesActive = new boolean[capacity];
		mStateCount = capacity;
	}

	public void setState(int layer,YangProgramState<StateMachineType> state) {
		if(state!=null && !state.isInitialized())
			state.init(mStateSystem);
		if(mStates[layer]!=null)
			mStates[layer].stop();
		mStates[layer] = state;
		if(state!=null && !state.mFirstFrame)
			state.start();
		mStatesActive[layer] = state!=null;
	}

	public void deactivateAllStates() {
		for(int i=0;i<mStateCount;i++) {
			mStatesActive[i] = false;
		}
	}

	@Override
	protected void step(float deltaTime) {
		boolean blocked = false;
		for(int i=mStateCount-1;i>=0;i--) {
			if(mStatesActive[i]) {
				if(blocked) {
					if(!mStates[i].mBlocked) {
						mStates[i].mBlocked = true;
						mStates[i].onBlock();
					}
				}else{
					if(mStates[i].mBlocked) {
						mStates[i].mBlocked = false;
						mStates[i].onUnblock();
					}
					mStates[i].proceed(deltaTime);
					if(mStates[i].mBlockSteps)
						blocked = true;
				}
			}
		}
	}

	@Override
	protected void preDraw() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i])
				mStates[i].preDrawFrame();
		}
	}

	@Override
	protected void draw() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i])
				mStates[i].drawFrame();
		}
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		for(int i=mStateCount-1;i>=0;i--) {
			if(mStatesActive[i]) {
				YangProgramState<?> state = mStates[i];
				if(!state.mFirstFrame) {
					event.handle(state);
					if(state.mBlockEvents)
						return true;
				}
			}
		}
		return true;
	}

	@Override
	public void resume() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i]) {
				mStates[i].resume();
			}
		}
	}

	@Override
	public void stop() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i]) {
				mStates[i].stop();
			}
		}
	}

	@Override
	public void pause() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i]) {
				mStates[i].pause();
			}
		}
	}

}
