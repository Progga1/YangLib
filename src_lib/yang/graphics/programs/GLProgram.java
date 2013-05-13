package yang.graphics.programs;

import yang.graphics.textures.TextureData;


public abstract class GLProgram {

	protected abstract void derivedCompile(String vertexShaderCode, String fragmentShaderCode,Object sender);
	public abstract void activate();
	public abstract int getAttributeLocation(String attribute);
	public abstract int getUniformLocation(String uniform);
	public abstract void setUniform2f(int handle, float v1, float v2);
	public abstract void setUniform3f(int handle, float v1, float v2, float v3);
	public abstract void setUniform4f(int handle,float v1, float v2, float v3, float v4);
	public abstract void setUniform4f(int handle,float[] values);
	public abstract void setUniformMatrix(int handle,float[] matrix);
	public abstract void setUniformFloat(int handle,float value);
	public abstract void setUniformInt(int handle,int value);
	protected abstract String evaluateMacro(String key,String value);
	
	private String preProcessCode(String shaderCode) {
		StringBuilder result = new StringBuilder();
		String[] lines = shaderCode.split("\n");
		for(String line:lines) {
			line = line.trim();
			if(line.startsWith("#")) {
				int keyPos = line.indexOf(' ');
				String key = line.substring(1,keyPos);
				String value =  line.substring(keyPos);
				String eval = null;
				if(key.equals("PREMULT"))
					eval = TextureData.USE_PREMULTIPLICATION?value:null;
				else if(key.equals("NOPREMULT"))
					eval = !TextureData.USE_PREMULTIPLICATION?value:null;
				else
					eval = evaluateMacro(key,value);
				if(eval!=null)
					result.append(eval+"\n");
			}else
				result.append(line+"\n");
		}
		return result.toString();
	}
	
	public final void compile(String vertexShaderCode, String fragmentShaderCode,Object sender) {
		
		String vertexPreProc = preProcessCode(vertexShaderCode);
		String fragmentPreProc = preProcessCode(fragmentShaderCode);
		derivedCompile(vertexPreProc,fragmentPreProc,sender);
	}
	
}
