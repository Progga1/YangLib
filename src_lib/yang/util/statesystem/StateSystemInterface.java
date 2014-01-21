package yang.util.statesystem;

public interface StateSystemInterface {

	public void setState(YangProgramState state);
	public void setStateNoStart(YangProgramState state);
	public YangProgramState getCurrentState(int layer);

}
