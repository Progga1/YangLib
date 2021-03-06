package yang.pc.tools.runtimeinspectors.subcomponents;



public class IntTextField extends NumTextField {

	private static final long serialVersionUID = 1L;

	public IntTextField() {
		super();
		setMaxDigits(0);
		setScrollFactor(1);
		setClickSteps(1);
	}

	public int getInt() {
		if(isMax())
			return Integer.MAX_VALUE;
		else if(isMin())
			return Integer.MIN_VALUE;
		else
			return (int)mCurValue;
	}

	public void setInt(int value,boolean updateText) {
		setDouble(value,updateText);
	}

	public void setInt(int value) {
		setInt(value,true);
	}

}
