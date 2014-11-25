package yang.android.billing;

import java.util.List;

public interface PayServiceCallbacks {

	public void onStartFinished(boolean success);
	public void onPurchasesLoaded(List<Purchase> purchases, boolean success);
	public void onSkuDetailsLoaded(List<SkuDetails> details, boolean success);
	public void onPurchaseConsumed(Purchase purchase);
	public void onPurchaseFinished(Purchase purchase, boolean success);
}
