package yang.pc.tools.runtimeinspectors.interfaces;

public class NameHolder implements NameInterface {

	public String mName;

	public NameHolder(String name) {
		mName = name;
	}

	@Override
	public String getName() {
		return mName;
	}

}
