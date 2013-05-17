package yang.android.sampleexecuters;

import yang.android.graphics.YangActivity;
import yang.samples.statesystem.SampleStateSystem;
import android.os.Bundle;

public class SampleStateSystemActivity extends YangActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.defaultInit(true);
		super.setSurface(new SampleStateSystem());
	}

}
