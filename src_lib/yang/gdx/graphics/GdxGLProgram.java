package yang.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import yang.graphics.defaults.DefaultPCGLProgram;
import yang.util.Util;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;


public class GdxGLProgram extends DefaultPCGLProgram {

	private int mProgram;
	private FloatBuffer mFb;
	
	public GdxGLProgram() {
		ByteBuffer buf = ByteBuffer.allocateDirect(16*4);
		buf.order(ByteOrder.nativeOrder());
		mFb = buf.asFloatBuffer();
	}
	
	protected static void printCompileMessage(boolean error,int shaderType,String message,int length,Object sender) {
		String shaderName;
		switch (shaderType) {
		case GL20.GL_VERTEX_SHADER:
			shaderName = "VERTEX SHADER";
			break;
		case GL20.GL_FRAGMENT_SHADER:
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
	
	private static IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
	private static IntBuffer errorLogLength = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
	
	public static int compileShader(int shaderType, String shaderCode,Object sender) {

		GdxGraphicsTranslator.clearError();
		int shaderId = Gdx.gl20.glCreateShader(shaderType);
		GdxGraphicsTranslator.checkError("Create shader");
		Gdx.gl20.glShaderSource(shaderId, shaderCode);
		GdxGraphicsTranslator.checkError("Set shader source");
		Gdx.gl20.glCompileShader(shaderId);
		GdxGraphicsTranslator.checkError("Compile shader");

		//Errors/Warnings
		buffer.rewind();
		Gdx.gl20.glGetShaderiv(shaderId,GL20.GL_COMPILE_STATUS, buffer);
		boolean isError = buffer.get(0) == GL20.GL_FALSE;
		
		errorLogLength.rewind();
		Gdx.gl20.glGetShaderiv(shaderId, GL20.GL_INFO_LOG_LENGTH, errorLogLength);
		int logLength = errorLogLength.get(0);
		if (logLength>1) {
			printCompileMessage(isError,shaderType,Gdx.gl20.glGetShaderInfoLog(shaderId),logLength,sender);
		}
		
		if(isError)
			return -1;
		else
			return shaderId;
	}

	@Override
	public int getAttributeLocation(String attribute) {
		GdxGraphicsTranslator.clearError();
		int result = Gdx.gl20.glGetAttribLocation(mProgram,attribute);
		GdxGraphicsTranslator.checkError("Attribute ("+attribute+")");
		return result;
	}
	
	@Override
	public int getUniformLocation(String uniform) {
		GdxGraphicsTranslator.clearError();
		int result = Gdx.gl20.glGetUniformLocation(mProgram,uniform);
		GdxGraphicsTranslator.checkError("Uniform ("+uniform+")");
		return result;
	}
	
	private void init(int vertexShaderHandle, int fragmentShaderHandle) {
		GdxGraphicsTranslator.clearError();
		mProgram = Gdx.gl20.glCreateProgram();
		GdxGraphicsTranslator.checkError("Create program");
		Gdx.gl20.glAttachShader(mProgram, vertexShaderHandle);
		GdxGraphicsTranslator.checkError("Vertex shader attach");
		Gdx.gl20.glAttachShader(mProgram, fragmentShaderHandle);
		GdxGraphicsTranslator.checkError("Fragment shader attach");
		Gdx.gl20.glLinkProgram(mProgram);
		GdxGraphicsTranslator.checkError("Link program");
	}

	@Override 
	protected void derivedCompile(String vertexShaderCode, String fragmentShaderCode,Object sender) {
		GdxGraphicsTranslator.clearError();
		int vertexShaderHandle = compileShader(GL20.GL_VERTEX_SHADER, vertexShaderCode,sender);
		GdxGraphicsTranslator.checkError("Vertex shader compile");
		int fragmentShaderHandle = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderCode,sender);
		GdxGraphicsTranslator.checkError("Fragment shader compile");
		init(vertexShaderHandle, fragmentShaderHandle);
	}

	@Override
	public void activate() {
		Gdx.gl20.glUseProgram(mProgram);
	}

	@Override
	public void setUniformMatrix(int handle,float[] matrix) {
		BufferUtils.copy(matrix, mFb, matrix.length, 0);
		Gdx.gl20.glUniformMatrix4fv(handle, 1, false, mFb);
	}

	@Override
	public void setUniform2f(int handle, float v1, float v2) {
		Gdx.gl20.glUniform2f(handle, v1, v2);
	}
	
	@Override
	public void setUniform3f(int handle, float v1, float v2, float v3) {
		Gdx.gl20.glUniform3f(handle, v1,v2,v3);
	}
	
	@Override
	public void setUniform4f(int handle,float v1, float v2, float v3, float v4) {
		Gdx.gl20.glUniform4f(handle, v1,v2,v3,v4);
	}
	
	@Override
	public void setUniform4f(int handle, float[] values) {
		Gdx.gl20.glUniform4f(handle, values[0],values[1],values[2],values[3]);
	}
	
	@Override
	public void setUniformFloat(int handle,float value) {
		Gdx.gl20.glUniform1f(handle, value);
	}
	
	@Override
	public void setUniformInt(int handle,int value) {
		Gdx.gl20.glUniform1i(handle, value);
	}
	
	@Override
	protected String evaluateMacro(String key, String value) {
		if (Gdx.app.getType() == ApplicationType.Android) {
			if(key.equals("ANDROID"))
				return value;
			else
				return null;
		}
		return null;
	}
	
}
