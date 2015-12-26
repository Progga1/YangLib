package yang.pc.tools.runtimeproperties;

public interface InspectionInterface {

	public String getName();
	public Object getProperty(String propertyName);
	public Object setProperty(String propertyName,Object value);

}
