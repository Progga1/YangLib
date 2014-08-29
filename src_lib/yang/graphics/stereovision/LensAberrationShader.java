package yang.graphics.stereovision;

import yang.graphics.translator.AbstractGFXLoader;

public class LensAberrationShader extends LensDistortionShader {

	public static String FRAGMENT_SHADER =
			"#ANDROID precision mediump float;\r\n" +

			"const vec2 lensCenter = vec2(0.5,0.5);\r\n" +
			"const vec2 coeffBlue = vec2(1.014, 0.0);\r\n" +
			"const vec2 coeffRed = vec2(0.996, 0.0-0.004);\r\n" +
			"const vec2 coeffGreen = vec2(1.0, 0.0);\r\n" +
			"\r\n" +
			"uniform sampler2D texSampler;\r\n" +
			"uniform vec2 scale;\r\n" +
			"uniform vec2 scaleToLens;\r\n" +
			"uniform vec4 lensParameters;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"\r\n" +
			"vec2 HmdWarp(vec2 in01, vec2 in02)\r\n" +
			"{\r\n"+
			"  vec2 theta = (in01 - lensCenter) * scaleToLens;\r\n" +
			"  float rSq = theta.x * theta.x + theta.y * theta.y;\r\n" +
			"  vec2 theta1 = theta * (in02.x + in02.y * rSq)\r\n" +
			"                  * (lensParameters.x +\r\n" +
			"                     lensParameters.y * rSq +\r\n" +
			"                     lensParameters.z * rSq * rSq +\r\n" +
			"                     lensParameters.w * rSq * rSq * rSq);\r\n" +
			"  theta1 = (scale * theta1) + lensCenter;\r\n" +
			"  return theta1;\r\n" +
			"}\r\n" +
			"\r\n" +
			"void main()\r\n" +
			"{\r\n" +
			"	  vec2 newPos = texCoord;\r\n" +
			"	  vec2 tcRed;\r\n" +
			"	  vec2 tcGreen;\r\n" +
			"	  vec2 tcBlue;\r\n" +
			"\r\n" +
			"	  tcBlue = HmdWarp(texCoord, coeffBlue);\r\n" +
			"      tcRed = HmdWarp(texCoord, coeffRed);\r\n" +
			"	  tcGreen = HmdWarp(texCoord, coeffGreen);\r\n" +
			"\r\n" +
			"	    gl_FragColor.r = texture2D(texSampler, tcRed).r;\r\n" +
			"	    gl_FragColor.g = texture2D(texSampler, tcGreen).g;\r\n" +
			"	    gl_FragColor.b = texture2D(texSampler, tcBlue).b;\r\n" +
			"		gl_FragColor.a = 1.0;\r\n" +
			"}\r\n";

	public LensAberrationShader() {
		mScaleToLens = 1.66f;
	}

	@Override
	public void initHandles() {
		super.initHandles();
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}

	@Override
	public void activate() {
		super.activate();
	}

}
