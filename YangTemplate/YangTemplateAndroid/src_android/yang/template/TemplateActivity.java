package yang.template;

import samplesurface.SampleSurface;
import yang.android.graphics.YangActivity;
import android.os.Bundle;

public class TemplateActivity  extends YangActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.defaultInit();
		super.setSurface(new SampleSurface());
	}
}
