package yang.pc.tools.runtimeinspectors;

public interface InspectionInterface {

	public String getName();
	public Object getProperty(String propertyName);
	public Object setProperty(String propertyName,Object value);

}
