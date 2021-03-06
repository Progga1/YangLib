package yang.android.sampleexecuters;

import yang.android.graphics.YangActivity;
import yang.model.DebugYang;
import yang.samples.statesystem.SampleStateSystem;
import android.os.Bundle;

public class SampleStateSystemActivity extends YangActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.defaultInit(true);

		DebugYang.DRAW_GFX_VALUES = true;
		super.setSurface(new SampleStateSystem());
	}

}
