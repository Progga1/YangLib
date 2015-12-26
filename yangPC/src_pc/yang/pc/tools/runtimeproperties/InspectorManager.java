package yang.pc.tools.runtimeproperties;

import java.util.HashMap;

import yang.pc.tools.runtimeproperties.components.PropertyCheckBox;
import yang.pc.tools.runtimeproperties.components.PropertyTextField;

public class InspectorManager {

	protected HashMap<Class<?>,Class<? extends InspectorComponent>> mTypes = new HashMap<Class<?>,Class<? extends InspectorComponent>>();

	public InspectorManager() {
		registerType(Boolean.class,PropertyCheckBox.class);
		registerType(String.class,PropertyTextField.class);
	}

	public void registerType(Class<?> type,Class<? extends InspectorComponent> component) {
		mTypes.put(type,component);
	}

	public Class<? extends InspectorComponent> getDefaultComponent(Class<?> type) {
		return mTypes.get(type);
	}

	public InspectorComponent createDefaultComponentInstance(Class<?> type) {
		Class<? extends InspectorComponent> compClass = getDefaultComponent(type);
		if(compClass==null)
			return null;
		InspectorComponent component;
		try {
			component = compClass.newInstance();
			return component;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public InspectorFrame createInspectionFrame() {
		InspectorFrame frame = new InspectorFrame(this);
		frame.setFramed();
		return frame;
	}

	public InspectorPanel createInspector(InspectorFrame frame) {
		return new InspectorPanel(frame);
	}

}
