package yang.model;


public abstract class Factory {

	private static Factory mFactory;
	
	protected abstract TransformationMatrix createTransformationMatrix();
	
	public static TransformationMatrix newTransformationMatrix(){
		return mFactory.createTransformationMatrix();
	}
	
	public static void init(Factory factory) {
		mFactory = factory;
	}
	
}
