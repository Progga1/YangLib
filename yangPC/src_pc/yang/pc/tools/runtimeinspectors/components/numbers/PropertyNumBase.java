package yang.pc.tools.runtimeinspectors.components.numbers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public abstract class PropertyNumBase extends InspectorComponent implements ActionListener{

	protected abstract NumTextField createNumTextField();

	public NumTextField mNumTextField;

	@Override
	protected void postInit() {
		mNumTextField = createNumTextField();
		mNumTextField.setBorder(InspectorGUIDefinitions.COMPONENT_PADDING_BORDER);
		mNumTextField.setActionListener(this);
	}

	public PropertyNumBase setMouseScrollFactor(float stepsPerPixel) {
		mNumTextField.setScrollFactor(stepsPerPixel);
		return this;
	}

	public void setCyclic(boolean cyclic) {
		mNumTextField.setCyclic(cyclic);
	}

	@Override
	public Component getComponent() {
		return mNumTextField;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		notifyValueUserInput();
	}

}
