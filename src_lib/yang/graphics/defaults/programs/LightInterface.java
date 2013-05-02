package yang.graphics.defaults.programs;

public interface LightInterface {

	public void setLightDirection(float[] dir);
	public void setLightDirection(float x,float y,float z);
	public void setLightProperties(float minLight,float maxLight,float addLight,float lightFactor);
	
}
