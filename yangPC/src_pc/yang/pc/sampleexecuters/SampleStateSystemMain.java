package yang.pc.sampleexecuters;

import yang.model.PathSpecs;
import yang.pc.gles.YangGLESFrame;
import yang.samples.statesystem.SampleStateSystem;

public class SampleStateSystemMain {

	public static void main(String[] args) {
		PathSpecs.setAssetPath("../../yangLib/yangAndroid/assets/");
		final YangGLESFrame frame = new YangGLESFrame().init(1280,800);
		frame.setSurface(new SampleStateSystem());
		frame.mSurface.handleArgs(args,0);
		frame.run();
	}

}
