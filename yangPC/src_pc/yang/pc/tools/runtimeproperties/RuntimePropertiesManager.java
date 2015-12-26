package yang.pc.tools.runtimeproperties;

import java.util.HashMap;

import yang.pc.tools.runtimeproperties.components.PropertyCheckBox;
import yang.pc.tools.runtimeproperties.components.PropertyTextField;

public class RuntimePropertiesManager {

	protected HashMap<Class<?>,Class<? extends RuntimePropertyComponent>> mTypes = new HashMap<Class<?>,Class<? extends RuntimePropertyComponent>>();

	public RuntimePropertiesManager() {
		registerType(Boolean.class,PropertyCheckBox.class);
		registerType(String.class,PropertyTextField.class);
	}

	public void registerType(Class<?> type,Class<? extends RuntimePropertyComponent> component) {
		mTypes.put(type,component);
	}

	public Class<? extends RuntimePropertyComponent> getDefaultComponent(Class<?> type) {
		return mTypes.get(type);
	}

	public RuntimePropertyComponent createDefaultComponentInstance(Class<?> type) {
		Class<? extends RuntimePropertyComponent> compClass = getDefaultComponent(type);
		if(compClass==null)
			return null;
		RuntimePropertyComponent component;
		try {
			component = compClass.newInstance();
			return component;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public RuntimePropertiesFrame createInspectionFrame() {
		RuntimePropertiesFrame frame = new RuntimePropertiesFrame(this);
		frame.setFramed();
		return frame;
	}

	public RuntimePropertiesInspector createInspector(RuntimePropertiesFrame frame) {
		return new RuntimePropertiesInspector(frame);
	}

}
