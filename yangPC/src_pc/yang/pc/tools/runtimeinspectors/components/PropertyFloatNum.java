package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public class PropertyFloatNum extends InspectorComponent implements ActionListener {

	public NumTextField mNumTextField;
	public double mDefaultValue;

	private boolean mDoubleMode = false;

	public PropertyFloatNum(double defaultVal) {
		mDefaultValue = defaultVal;
	}

	public PropertyFloatNum() {
		this(0);
	}

	@Override
	protected void postInit() {
		mNumTextField = new NumTextField();
		mNumTextField.addActionListener(this);
	}

	@Override
	protected void postSetValue(Object value) {
		mDoubleMode = value instanceof Double;
		if(mDoubleMode) {
			mNumTextField.setDouble((Double)value);
		}else{
			mNumTextField.setFloat((Float)value);
		}
	}

	@Override
	protected Object getValue() {
		if(mDoubleMode) {
			return mNumTextField.getDouble(0);
		}else{
			return mNumTextField.getFloat(0);
		}
	}

	@Override
	public Component getComponent() {
		return mNumTextField;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		setChanged();
	}

}
