package yang.pc;

import yang.systemdependent.YangSystemCalls;

public class PCSystemCalls extends YangSystemCalls {

	@Override
	public boolean reloadAfterPause() {
		return true;
	}

}
