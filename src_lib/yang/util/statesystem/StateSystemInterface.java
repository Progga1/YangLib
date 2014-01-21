package yang.util.statesystem;

import yang.util.statesystem.statefading.YangStateFade;

public interface StateSystemInterface {

	public void setState(YangProgramState state);
	public void setStateNoStart(YangProgramState state);
	public void fadeState(int layer,YangStateFade fader);
	public YangProgramState getCurrentState(int layer);

}
