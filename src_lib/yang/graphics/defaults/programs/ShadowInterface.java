package yang.graphics.defaults.programs;

public interface ShadowInterface extends LightInterface{

	public static int DEPTH_TEXTURE_LEVEL = 1;
	
	public void setDepthMapProjection(float[] depthTransformMatrix);
	
}
