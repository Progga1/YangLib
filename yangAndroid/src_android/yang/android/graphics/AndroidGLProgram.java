package yang.android.graphics;

import java.nio.IntBuffer;

import yang.graphics.programs.GLProgram;
import yang.math.objects.Vector3f;
import yang.util.Util;
import android.opengl.GLES20;

public class AndroidGLProgram extends GLProgram {

	private int mProgram;

	protected static void printCompileMessage(boolean error,int shaderType,String message,int length,Object sender) {
		String shaderName;
		switch (shaderType) {
		case GLES20.GL_VERTEX_SHADER:
			shaderName = "VERTEX SHADER";
			break;
		case GLES20.GL_FRAGMENT_SHADER:
			shaderName = "FRAGMENT SHADER";
			break;
		default:
			shaderName = "SHADER";
		}
		if(error) {
			System.err.println("---!" + shaderName + " COMPILE ERROR in "+Util.getClassName(sender)+"!---\n" + message + "\n");
			System.exit(0);
		}else{
			System.out.println("----" + shaderName + " COMPILE WARNING in "+Util.getClassName(sender)+"----\n" + message + "\n");
		}
	}
	
	public static int compileShader(int shaderType, String shaderCode,Object sender) {
		AndroidGraphics.clearError();
		int shaderId = GLES20.glCreateShader(shaderType);
		assert AndroidGraphics.checkError("Create shader");
		GLES20.glShaderSource(shaderId, shaderCode);
		assert AndroidGraphics.checkError("Set shader source");
		GLES20.glCompileShader(shaderId);
		assert AndroidGraphics.checkError("Compile shader");
		
		//Errors/Warnings
		int[] buffer = new int[1];
		GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, buffer, 0);
		boolean isError = buffer[0] == GLES20.GL_FALSE;
		int errorLogLength[] = new int[1];
		GLES20.glGetShaderiv(shaderId, GLES20.GL_INFO_LOG_LENGTH, IntBuffer.wrap(errorLogLength));
		int logLength = errorLogLength[0];
		if (logLength>1) {
			printCompileMessage(isError,shaderType,GLES20.glGetShaderInfoLog(shaderId),logLength,sender);
		}
		
		if(isError)
			return -1;
		else
			return shaderId;
	}

	@Override
	public int getAttributeLocation(String attribute) {
		assert AndroidGraphics.clearError();
		int result = GLES20.glGetAttribLocation(mProgram,attribute);
		assert AndroidGraphics.checkError("Attribute ("+attribute+")");
		return result;
	}
	
	@Override
	public int getUniformLocation(String uniform) {
		AndroidGraphics.clearError();
		int result = GLES20.glGetUniformLocation(mProgram,uniform);
		AndroidGraphics.checkError("Uniform ("+uniform+")");
		return result;
	}
	
	private void init(int vertexShaderHandle, int fragmentShaderHandle,Object sender) {
		AndroidGraphics.clearError();
		mProgram = GLES20.glCreateProgram();
		AndroidGraphics.checkError("Create program");
		GLES20.glAttachShader(mProgram, vertexShaderHandle);
		AndroidGraphics.checkError("Vertex shader attach");
		GLES20.glAttachShader(mProgram, fragmentShaderHandle);
		AndroidGraphics.checkError("Fragment shader attach");
		GLES20.glLinkProgram(mProgram);
		AndroidGraphics.checkError("Link program ("+ Util.getClassName(sender)+")");
	}

	@Override 
	protected void derivedCompile(String vertexShaderCode, String fragmentShaderCode,Object sender) {
		AndroidGraphics.clearError();
		int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode,sender);
		AndroidGraphics.checkError("Vertex shader compile");
		int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode,sender);
		AndroidGraphics.checkError("Fragment shader compile");
		init(vertexShaderHandle, fragmentShaderHandle,sender);
	}

	@Override
	public void activate() {
		GLES20.glUseProgram(mProgram);
	}

	@Override
	public void setUniformMatrix(int handle,float[] matrix) {
		GLES20.glUniformMatrix4fv(handle, 1, false, matrix, 0);
	}
	
	@Override
	public void setUniform2f(int handle,float v1, float v2) {
		GLES20.glUniform2f(handle, v1,v2);
	}
	
	@Override
	public void setUniform3f(int handle,float v1, float v2, float v3) {
		GLES20.glUniform3f(handle, v1,v2,v3);
	}
	
	@Override
	public void setUniform3f(int handle, float[] values) {
		GLES20.glUniform3f(handle, values[0],values[1],values[2]);
	}
	
	@Override
	public void setUniform3f(int handle, Vector3f vector) {
		GLES20.glUniform3f(handle, vector.mX,vector.mY,vector.mZ);
	}

	@Override
	public void setUniform4f(int handle,float v1, float v2, float v3, float v4) {
		GLES20.glUniform4f(handle, v1,v2,v3,v4);
	}
	
	@Override
	public void setUniform4f(int handle, float[] values) {
		GLES20.glUniform4f(handle, values[0],values[1],values[2],values[3]);
	}
	
	@Override
	public void setUniformFloat(int handle,float value) {
		GLES20.glUniform1f(handle, value);
	}
	
	@Override
	public void setUniformInt(int handle,int value) {
		GLES20.glUniform1i(handle, value);
	}

	@Override
	protected String evaluateMacro(String key, String value) {
		if(key.equals("ANDROID"))
			return value;
		else
			return null;
	}

}
