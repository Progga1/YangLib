package yang.pc.sampleexecuters;

import yang.pc.gles.YangGLESFrame;
import yang.samples.small.MinimumSample;

public class MinimumSampleMain {

	public static void main(String[] args) {		
		YangGLESFrame frame = new YangGLESFrame().init(800,600);
		frame.setSurface(new MinimumSample());
		frame.run();
	}
	
}
