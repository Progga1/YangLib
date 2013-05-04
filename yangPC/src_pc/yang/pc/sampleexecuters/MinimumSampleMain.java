package yang.pc.sampleexecuters;

import yang.pc.gles.GLESFrame;
import yang.samples.small.MinimumSampleSurface;

public class MinimumSampleMain {

	public static void main(String[] args) {		
		GLESFrame frame = new GLESFrame().init(640,480);
		frame.setSurface(new MinimumSampleSurface());
		frame.run();
	}
	
}
