package yang.util.statesystem;

import yang.util.statesystem.statefading.YangStateFader;

public interface StateSystemInterface {

	public void setState(YangProgramState state);
	public void setStateNoStart(int layer, YangProgramState state);
	public void fadeState(int layer,YangStateFader fader,YangProgramState toState);
	public YangProgramState getCurrentState(int layer);

}
