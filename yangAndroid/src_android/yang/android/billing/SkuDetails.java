package yang.android.billing;

import org.json.JSONObject;

public class SkuDetails {

	public boolean mSubscription;
	public String mSku;
	public String mType;
	public String mPrice;
	public String mTitle;
	public String mDescription;
	public String mRawDetails;

	public SkuDetails(boolean subscription, String skuDetails) {
		mSubscription = subscription;
		mRawDetails = skuDetails;
		try {
			JSONObject o = new JSONObject(mRawDetails);
			mSku = o.optString("productId");
			mType = o.optString("type");
			mPrice = o.optString("price");
			mTitle = o.optString("title");
			mDescription = o.optString("description");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}