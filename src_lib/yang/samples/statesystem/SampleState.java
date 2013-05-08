package yang.samples.statesystem;

import yang.events.Keys;
import yang.util.statesystem.YangProgramState;

public abstract class SampleState extends YangProgramState<SampleStateSystem> {

	@Override
	public void keyDown(int code) {
		
	}

	@Override
	public void keyUp(int code) {
		if(code==Keys.ESC)
			mStateSystem.setState(mStateSystem.mMainMenu);
	}
	
}
