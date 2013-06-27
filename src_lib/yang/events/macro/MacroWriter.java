package yang.events.macro;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import yang.events.eventtypes.YangEvent;
import yang.graphics.YangSurface;

public class MacroWriter {

	protected DataOutputStream mWriter;
	protected long startFrame;
	protected YangSurface mSurface;
	protected AbstractMacroIO mMacroIO;
	
	public MacroWriter(OutputStream writer,AbstractMacroIO macroIO) {
		mWriter = new DataOutputStream(writer);
		mSurface = macroIO.mSurface;
		mMacroIO = macroIO;
		startFrame = mSurface.mStepCount;
	}
	
	public void start() {
		startFrame = mSurface.mStepCount;
	}

	public void writeEvent(YangEvent event) {
		try {
			mWriter.writeLong(mSurface.mStepCount-startFrame);
			mMacroIO.writeEvent(mWriter,event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
