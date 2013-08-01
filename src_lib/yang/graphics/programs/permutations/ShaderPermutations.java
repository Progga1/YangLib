package yang.graphics.programs.permutations;

import yang.graphics.programs.Basic3DProgram;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.NonConcurrentList;
import yang.util.Util;

public class ShaderPermutations extends Basic3DProgram {

	static final String LINE_END = ";\r\n";
	
	public NonConcurrentList<SubShader> mLinearSubShaderList;
	public SubShader[] mDataPassingShaders;
	public int mPassingDataCount = 0;
	public String mVSSource,mFSSource;
	
	public ShaderPermutations(GraphicsTranslator graphics) {
		mGraphics = graphics;
		mLinearSubShaderList = new NonConcurrentList<SubShader>();
		mPassingDataCount = 0;
	}
	
	public ShaderPermutations(GraphicsTranslator graphics,SubShader[] subShaders) {
		this(graphics);
		addSubShaders(subShaders);
		initPermutations();
	}
	
	public void addSubShaders(SubShader[] subShaders) {
		if(subShaders!=null)
			linearize(subShaders);
	}
	
	public void addSubShader(SubShader subShader) {
		if(subShader!=null)
			linearize(new SubShader[]{subShader});
	}
	
	public ShaderPermutations initPermutations() {
		
		ShaderPermutationsParser parser = new ShaderPermutationsParser(this);
		for(SubShader subShader:mLinearSubShaderList) {
			subShader.setGraphics(mGraphics);
			subShader.setVariables(parser,parser.mVSDeclarations,parser.mFSDeclarations);
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
	
	private void linearize(SubShader[] subShaders) {
		for(SubShader subShader:subShaders) {
			if(subShader==null)
				continue;
			mLinearSubShaderList.add(subShader);
			if(subShader.passesData())
				mPassingDataCount++;
			SubShader[] innerShaders = subShader.getInnerShaders();
			if(innerShaders!=null)
				linearize(innerShaders);
		}
	}
	
	@Override
	public void initHandles() {
		super.initHandles();
		
		mDataPassingShaders = new SubShader[mPassingDataCount];
		int c=0;
		for(SubShader subShader:mLinearSubShaderList) {
			subShader.initHandles(mProgram);
			if(subShader.handlesOK() && subShader.passesData()) {
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
