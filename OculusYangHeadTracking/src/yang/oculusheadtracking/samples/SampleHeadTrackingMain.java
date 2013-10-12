package yang.oculusheadtracking.samples;

import yang.model.App;
import yang.model.PathSpecs;
import yang.oculusheadtracking.OculusSensor;
import yang.pc.gles.YangGLESFrame;
import yang.samples.statesystem.SampleStateSystem;

public class SampleHeadTrackingMain {

	public static void main(String[] args) {
		PathSpecs.ASSET_PATH = "../yangAndroid/assets/";
		App.sensor = new OculusSensor();
		YangGLESFrame frame = new YangGLESFrame().init(1280,800);
		frame.setSurface(new SampleStateSystem());
		frame.mSurface.handleArgs(args,0);
		frame.run();
	}
	
}
