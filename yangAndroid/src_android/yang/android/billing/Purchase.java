package yang.android.billing;

import org.json.JSONObject;

public class Purchase {

	public boolean mSubscription;
	public String mOrderId;
	public String mPackageName;
	public String mSku;
	public long mPurchaseTime;
	public int mPurchaseState;
	public String mDeveloperPayload;
	public String mToken;
	public String mPurchaseData;
	public String mSignature;

	public Purchase(boolean subscription, String purchaseData, String signature) {
		mSubscription = subscription;
		mSignature = signature;
		mPurchaseData = purchaseData;
		try {
			JSONObject o = new JSONObject(mPurchaseData);
			mOrderId = o.optString("orderId");
			mPackageName = o.optString("packageName");
			mSku = o.optString("productId");
			mPurchaseTime = o.optLong("purchaseTime");
			mPurchaseState = o.optInt("purchaseState");
			mDeveloperPayload = o.optString("developerPayload");
			mToken = o.optString("token", o.optString("purchaseToken"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
