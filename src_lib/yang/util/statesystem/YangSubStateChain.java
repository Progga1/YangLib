package yang.util.statesystem;

import yang.events.eventtypes.YangEvent;

public class YangSubStateChain<StateMachineType extends YangProgramStateSystem> extends YangProgramState<YangProgramStateSystem> {

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
		mStates[layer] = state;
	}

	@Override
	protected void step(float deltaTime) {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i])
				mStates[i].proceed(deltaTime);
		}
	}

	@Override
	protected void preDraw() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i])
				mStates[i].preDraw();
		}
	}

	@Override
	protected void draw() {
		for(int i=0;i<mStateCount;i++) {
			if(mStatesActive[i])
				mStates[i].draw();
		}
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		for(int i=0;i<mStateCount;i++) {
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
