package yang.pc.sampleexecuters;

import yang.model.PathSpecs;
import yang.pc.gles.YangGLESFrame;
import yang.samples.statesystem.SampleStateSystem;

public class StateSystemSampleMain {

	public static void main(String[] args) {
		PathSpecs.ASSET_PATH = "../../yangLib/yangAndroid/assets/";
		YangGLESFrame frame = new YangGLESFrame().init(800,600);
		frame.setSurface(new SampleStateSystem());
		frame.run();
	}
	
}
