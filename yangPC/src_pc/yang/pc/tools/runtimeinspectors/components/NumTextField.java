package yang.pc.tools.runtimeinspectors.components;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;

public class NumTextField extends Container {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField mTextField;

	public NumTextField() {
		mTextField = new JFormattedTextField();
		NumberFormat.getNumberInstance();
//		mTextField.setValue(new Float(0));
		mTextField.setText("0");
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		this.add(mTextField);
	}

	public void addActionListener(ActionListener listener) {
		mTextField.addActionListener(listener);
	}

	public void setDouble(double val) {
		mTextField.setText(""+val);
	}

	public void setFloat(float val) {
		mTextField.setText(""+val);
	}

	public double getDouble(double defaultVal) {
		double result;
		String text = mTextField.getText();
		if(text.equals(""))
			return defaultVal;
		try{
			result = Double.parseDouble(text);
		}catch(NumberFormatException ex) {
			return defaultVal;
		}
		return result;
	}

	public float getFloat(float defaultVal) {
		float result;
		String text = mTextField.getText();
		if(text.equals(""))
			return defaultVal;
		try{
			result = Float.parseFloat(text);
		}catch(NumberFormatException ex) {
			return defaultVal;
		}
		return result;
	}

	@Override
	public boolean hasFocus() {
		return mTextField.hasFocus();
	}

}
