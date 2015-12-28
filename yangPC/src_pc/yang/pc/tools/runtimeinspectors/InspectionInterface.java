package yang.pc.tools.runtimeinspectors;

public interface InspectionInterface {

	public String getName();
	public Object getReferencedProperty(String propertyName,InspectorComponent sender);
	public void readProperty(String propertyName,InspectorComponent target);
	public Object setProperty(String propertyName,InspectorComponent component);

}
