package yang.pc.tools.runtimeinspectors.interfaces;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public interface InspectionInterface extends NameInterface {

	public Object getReferencedProperty(String propertyName,InspectorComponent sender);
	public void readProperty(String propertyName,InspectorComponent target);
	public void setProperty(String propertyName,InspectorComponent component);

}
