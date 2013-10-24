package yang.pc.gles;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2ES2;

import yang.graphics.defaults.DefaultPCGLProgram;
import yang.math.objects.Point3f;
import yang.util.Util;

import com.jogamp.common.nio.Buffers;

public class PCGLProgram extends DefaultPCGLProgram {

	private final GL2ES2 gl2;
	private int mProgram;
	private final IntBuffer intBuf = IntBuffer.allocate(1);

	public PCGLProgram(GL2ES2 gl2) {
		this.gl2 = gl2;
	}

	@Override
	public int getAttributeLocation(String attribute) {
		assert PCGL2ES2Graphics.clearError(gl2);
		final int result = gl2.glGetAttribLocation(mProgram, attribute);
		assert PCGL2ES2Graphics.checkError(gl2, "Attribute (" + attribute + ")");
		return result;
	}

	@Override
	public int getUniformLocation(String uniform) {
		assert PCGL2ES2Graphics.clearError(gl2);
		final int result = gl2.glGetUniformLocation(mProgram, uniform);
		assert PCGL2ES2Graphics.checkError(gl2, "Uniform (" + uniform + ")");
		return result;
	}

	protected void printCompileMessage(boolean error,int shaderType,ByteBuffer message,int length,Object sender) {
		final byte[] errorBytes = new byte[length];
		message.get(errorBytes);
		final String errorString = new String(errorBytes);
		final String upString = errorString.toUpperCase();
		if(upString.startsWith("NO ERRORS") || upString.startsWith("SUCCESS"))
			return;
		String shaderName;
		switch (shaderType) {
		case GL2ES2.GL_VERTEX_SHADER:
			shaderName = "VERTEX SHADER";
			break;
		case GL2ES2.GL_FRAGMENT_SHADER:
			shaderName = "FRAGMENT SHADER";
			break;
		default:
			shaderName = "SHADER";
		}
		if(error) {
			System.err.println("---!" + shaderName + " COMPILE ERROR in "+Util.getClassName(sender)+"!---\n" + errorString + "\n");
			System.exit(0);
		}else{
			System.out.println("----" + shaderName + " COMPILE WARNING in "+Util.getClassName(sender)+"----\n" + errorString + "\n");
		}
	}

	private int compileShader(int shaderType, String shaderCode, Object sender) {
		final int shaderId = gl2.glCreateShader(shaderType);

		intBuf.position(0);
		intBuf.put(shaderCode.length());
		gl2.glShaderSource(shaderId, 1, new String[] { shaderCode }, null, 0);
		gl2.glCompileShader(shaderId);

		//Errors/Warnings
		final int[] buffer = new int[1];
		gl2.glGetShaderiv(shaderId, GL2ES2.GL_COMPILE_STATUS, buffer, 0);
		final boolean isError = buffer[0] == GL2ES2.GL_FALSE;
		final int errorLogLength[] = new int[1];
		gl2.glGetShaderiv(shaderId, GL2ES2.GL_INFO_LOG_LENGTH, IntBuffer.wrap(errorLogLength));
		final int logLength = errorLogLength[0];
		if (logLength>1) {
			final ByteBuffer errorLog = ByteBuffer.allocateDirect(logLength);
			gl2.glGetShaderInfoLog(shaderId, logLength, null, errorLog);
			printCompileMessage(isError,shaderType,errorLog,logLength,sender);
		}

		if(isError)
			return -1;
		else
			return shaderId;
	}

	private void init(int vertexShaderHandle, int fragmentShaderHandle) {
		gl2.glAttachShader(mProgram, vertexShaderHandle);
		gl2.glAttachShader(mProgram, fragmentShaderHandle);
		gl2.glLinkProgram(mProgram);
	}

	@Override
	protected void derivedCompile(String vertexShaderCode, String fragmentShaderCode,Object sender) {
		mProgram = gl2.glCreateProgram();

		final int vertexShaderHandle = compileShader(GL2ES2.GL_VERTEX_SHADER, vertexShaderCode,sender);
		final int fragmentShaderHandle = compileShader(GL2ES2.GL_FRAGMENT_SHADER, fragmentShaderCode,sender);

		init(vertexShaderHandle, fragmentShaderHandle);

		final int[] statusLinker = new int[1];
		gl2.glGetProgramiv(mProgram, GL2ES2.GL_LINK_STATUS, statusLinker, 0);
		if (statusLinker[0] == GL2ES2.GL_FALSE) {
			final int infoLogLenght[] = new int[1];
			gl2.glGetShaderiv(mProgram, GL2ES2.GL_INFO_LOG_LENGTH, IntBuffer.wrap(infoLogLenght));
			final ByteBuffer infoLog = Buffers.newDirectByteBuffer(infoLogLenght[0]);
			gl2.glGetShaderInfoLog(mProgram, infoLogLenght[0], null, infoLog);
			final byte[] infoBytes = new byte[infoLogLenght[0]];
			infoLog.get(infoBytes);
			System.out.println("---!SHADER LINK ERROR in "+Util.getClassName(sender)+"!---\n" + new String(infoBytes)+"\n");
			System.exit(0);
		}

	}

	@Override
	public void setUniformMatrix4f(int handle, float[] matrix) {
		gl2.glUniformMatrix4fv(handle, 1, false, matrix, 0);
	}

	@Override
	public void setUniformMatrix3f(int handle, float[] matrix) {
		gl2.glUniformMatrix3fv(handle, 1, false, matrix, 0);
	}

	@Override
	public void setUniform2f(int handle, float v1, float v2) {
		gl2.glUniform2f(handle, v1,v2);
	}

	@Override
	public void setUniform3f(int handle, float v1, float v2, float v3) {
		gl2.glUniform3f(handle, v1,v2,v3);
	}

	@Override
	public void setUniform3f(int handle, float[] values) {
		gl2.glUniform3f(handle, values[0],values[1],values[2]);
	}

	@Override
	public void setUniform3f(int handle, Point3f point) {
		gl2.glUniform3f(handle, point.mX,point.mY,point.mZ);
	}

	@Override
	public void setUniform4f(int handle, float v1, float v2, float v3, float v4) {
		gl2.glUniform4f(handle, v1,v2,v3,v4);
	}

	@Override
	public void setUniform4f(int handle, float[] values) {
		gl2.glUniform4f(handle, values[0],values[1],values[2],values[3]);
	}

	@Override
	public void setUniformFloat(int handle, float value) {
		gl2.glUniform1f(handle, value);
	}

	@Override
	public void setUniformInt(int handle, int value) {
		gl2.glUniform1i(handle, value);
	}

	@Override
	public void activate() {
		gl2.glUseProgram(mProgram);

	}

}
