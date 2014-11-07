package yang.pc;

import yang.pc.gles.YangGLESFrame;
import yang.systemdependent.YangSystemCalls;

public class PCSystemCalls extends YangSystemCalls {

	private YangGLESFrame mFrame;

	public PCSystemCalls(YangGLESFrame frame) {
		mFrame = frame;
	}

	@Override
	public boolean reloadAfterPause() {
		return true;
	}

	@Override
	public void exit() {
		mFrame.close();
	}

	@Override
	public String getPlatfrom() {
		return "PC";
	}
}
