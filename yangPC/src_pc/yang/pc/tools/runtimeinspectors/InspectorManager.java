package yang.pc.tools.runtimeinspectors;

import java.util.HashMap;

import yang.graphics.model.FloatColor;
import yang.graphics.model.TransformationData;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.pc.tools.runtimeinspectors.components.PropertyCheckBox;
import yang.pc.tools.runtimeinspectors.components.PropertyColorNums;
import yang.pc.tools.runtimeinspectors.components.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.PropertyQuaternion;
import yang.pc.tools.runtimeinspectors.components.PropertyTextField;
import yang.pc.tools.runtimeinspectors.components.PropertyTransform;
import yang.pc.tools.runtimeinspectors.components.PropertyVector3;

public class InspectorManager {

	protected HashMap<Class<?>,Class<? extends InspectorComponent>> mTypes = new HashMap<Class<?>,Class<? extends InspectorComponent>>();

	public InspectorManager() {
		registerType(Boolean.class,PropertyCheckBox.class);
		registerType(String.class,PropertyTextField.class);
		registerType(Float.class,PropertyFloatNum.class);
		registerType(Double.class,PropertyFloatNum.class);
		registerType(Point3f.class,PropertyVector3.class);
		registerType(Vector3f.class,PropertyVector3.class);
		registerType(FloatColor.class,PropertyColorNums.class);
		registerType(Quaternion.class,PropertyQuaternion.class);
		registerType(TransformationData.class,PropertyTransform.class);
//		registerType(YangMatrix.class,PropertyTransform.class);
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
