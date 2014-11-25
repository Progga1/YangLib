package yang.systemdependent;

import java.util.ArrayList;



public class AbstractPaySystem<T> {

	public AbstractPaySystem() {

	}

	public void start() {

	}

	public void stop() {

	}

	public boolean isConnected() {
		return false;
	}

	protected void queryPurchases() {

	}

	protected void querySkuDetails(ArrayList<String> skus) {

	}

	public void purchase(T item) {

	}

}
