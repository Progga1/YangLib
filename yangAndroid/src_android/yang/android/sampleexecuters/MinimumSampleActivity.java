package yang.android.sampleexecuters;

import yang.android.graphics.YangActivity;
import yang.samples.small.MinimumSample;
import android.os.Bundle;

public class MinimumSampleActivity extends YangActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.defaultInit();
		super.setSurface(new MinimumSample());
	}

}
