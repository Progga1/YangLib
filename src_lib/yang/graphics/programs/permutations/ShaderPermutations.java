package yang.graphics.programs.permutations;

import yang.graphics.programs.Basic3DProgram;
import yang.graphics.translator.AbstractGFXLoader;

public class ShaderPermutations extends Basic3DProgram {

	static final String LINE_END = ";\r\n";
	
	public SubShader[] mSubShaders;
	public String mVSSource,mFSSource;
	
	public ShaderPermutations(SubShader[] subShaders) {
		mSubShaders = subShaders;
		
		ShaderPermutationsParser parser = new ShaderPermutationsParser(this);
		for(int i=0;i<subShaders.length;i++) {
			subShaders[i].setVariables(parser,parser.mVSDeclarations,parser.mFSDeclarations);
		}
		
		StringBuilder result = new StringBuilder(512);
		result.append("#ANDROID precision mediump float;\r\n");
		for(ShaderDeclaration declaration:parser.mVSDeclarations.mDeclarations) {
			result.append(declaration.mDeclarationString);
			result.append(LINE_END);
		}
		result.append("\r\n");
		result.append("void main() {\r\n");
		result.append(parser.getVariable("VS_MAIN"));
		result.append("\r\n}\r\n");
		mVSSource = result.toString();
		
		result = new StringBuilder(512);
		result.append("#ANDROID precision mediump float;\r\n");
		for(ShaderDeclaration declaration:parser.mFSDeclarations.mDeclarations) {
			result.append(declaration.mDeclarationString);
			result.append(LINE_END);
		}
		result.append("\r\n");
		result.append("void main() {\r\n");
		result.append(parser.getVariable("FS_MAIN"));
		result.append("\r\ngl_FragColor=");
		result.append(parser.getVariable("COLOR","vec4(1.0,1.0,1.0,1.0)"));
		result.append(LINE_END);
		result.append("}\r\n");
		mFSSource = result.toString();
		
		System.out.println(this);
	}
	
	public void passData() {
		for(SubShader subShader:mSubShaders) {
			subShader.passData(mProgram);
		}
	}
	
	@Override
	public String getVertexShader(AbstractGFXLoader gfxLoader) {
		return mVSSource;
	}
	
	@Override
	public String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return mFSSource;
	}
	
	@Override
	public String toString() {
		return "--------VERTEX-SHADER--------\n\n"+mVSSource+"\n\n--------FRAGMENT-SHADER--------\n\n"+mFSSource+"\n";
	}
	
}
