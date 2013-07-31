package yang.graphics.programs.permutations;

import yang.graphics.programs.Basic3DProgram;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.NonConcurrentList;
import yang.util.Util;

public class ShaderPermutations extends Basic3DProgram {

	static final String LINE_END = ";\r\n";
	
	public SubShader[] mSubShaders;
	public NonConcurrentList<SubShader> mLinearSubShaderList;
	public SubShader[] mDataPassingShaders;
	public int mPassingDataCount = 0;
	public String mVSSource,mFSSource;
	
	public ShaderPermutations(GraphicsTranslator graphics) {
		mGraphics = graphics;
		mLinearSubShaderList = new NonConcurrentList<SubShader>();
	}
	
	public ShaderPermutations(SubShader[] subShaders) {
		initPermutations(subShaders);
	}
	
	public ShaderPermutations initPermutations(SubShader[] subShaders) {
		mSubShaders = subShaders;
		
		ShaderPermutationsParser parser = new ShaderPermutationsParser(this);
		for(int i=0;i<subShaders.length;i++) {
			subShaders[i].setVariables(parser,parser.mVSDeclarations,parser.mFSDeclarations);
			subShaders[i].setGraphics(mGraphics);
		}
		
		boolean hasVertexColor = parser.hasVariable(SubShader.VAR_VS_COLOR);
		if(hasVertexColor) {
			parser.addVarying("vec4", "color");
			parser.appendLn(SubShader.VAR_VS_MAIN, "color = "+parser.getVariable(SubShader.VAR_VS_COLOR));
		}
		
		StringBuilder result = new StringBuilder(512);
		result.append("#ANDROID precision mediump float;\r\n");
		for(ShaderDeclaration declaration:parser.mVSDeclarations.mDeclarations) {
			result.append(declaration.mDeclarationString);
			result.append(LINE_END);
		}
		result.append("\r\n");
		result.append("void main() {\r\n");
		result.append(parser.getVariable(SubShader.VAR_VS_MAIN));
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
		result.append(parser.getVariable(SubShader.VAR_FS_MAIN));
		result.append("\r\ngl_FragColor = ");
		result.append(parser.getVariable(SubShader.VAR_FRAGCOLOR,"vec4(1.0,1.0,1.0,1.0)"));
		result.append(LINE_END);
		result.append("}\r\n");
		mFSSource = result.toString();
		
		return this;
	}
	
	private void initHandles(SubShader[] subShaders) {
		for(SubShader subShader:mSubShaders) {
			subShader.initHandles(mProgram);
			if(subShader.passesData())
				mPassingDataCount++;
			SubShader[] innerShaders = subShader.getInnerShaders();
			do{
				innerShaders = subShader.getInnerShaders();
				
			}while(innerShaders!=null);
		}
	}
	
	public void refreshLinearization() {
		mPassingDataCount = 0;
		
	}
	
	@Override
	public void initHandles() {
		super.initHandles();
		int passingDataCount = 0;
		for(SubShader subShader:mSubShaders) {
			subShader.initHandles(mProgram);
			if(subShader.passesData())
				passingDataCount++;
		}
		mDataPassingShaders = new SubShader[passingDataCount];
		int c=0;
		for(SubShader subShader:mSubShaders) {
			if(subShader.passesData()) {
				passingDataCount++;
				mDataPassingShaders[c++] = subShader;
			}
		}
	}
	
	public void prepareDraw() {
		for(SubShader subShader:mDataPassingShaders) {
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
		return "--------VERTEX-SHADER--------\n\n"+Util.stringToLineNumbersString(mVSSource)+"\n\n--------FRAGMENT-SHADER--------\n\n"+Util.stringToLineNumbersString(mFSSource)+"\n";
	}
	
}
