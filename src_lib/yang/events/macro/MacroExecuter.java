package yang.events.macro;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import yang.events.eventtypes.YangEvent;
import yang.model.DebugYang;

public class MacroExecuter {

	protected DataInputStream mStream;
	protected AbstractMacroIO mMacroIO;
	protected long mStepCount;
	protected long mNextStep;
	public boolean mFinished;
	
	public MacroExecuter(InputStream stream,AbstractMacroIO macroIO) {
		mMacroIO = macroIO;
		restart(stream);
	}
	
	public void restart(InputStream stream) {
		mStream = new DataInputStream(stream);
		mStepCount = 0;
		mNextStep = -1;
		mFinished = false;
	}
	
	public void step() {
		if(mFinished)
			return;
		try {
			if(mNextStep<0)
				mNextStep = mStream.readLong();
			if(mStepCount>=mNextStep) {
				YangEvent event = mMacroIO.readEvent(mStream);
				mMacroIO.mEventQueue.putEvent(event);
				if(mStream.available()<=0)
					mFinished = true;
				else
					mNextStep = mStream.readLong();
			}
			mStepCount++;
		} catch (IOException e) {
			DebugYang.exception(e);
		}
	}
	
}
