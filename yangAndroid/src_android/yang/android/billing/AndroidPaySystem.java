package yang.android.billing;

import java.util.ArrayList;
import java.util.List;

import yang.systemdependent.AbstractPaySystem;
import yang.util.YangList;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;

public abstract class AndroidPaySystem extends AbstractPaySystem<SkuDetails> implements ServiceConnection {

	public static final int BILLING_RESPONSE_RESULT_OK					= 0;
	public static final int BILLING_RESPONSE_RESULT_USER_CANCELED		= 1;
	public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE	= 3;
	public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE	= 4;
	public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR		= 5;
	public static final int BILLING_RESPONSE_RESULT_ERROR				= 6;
	public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED	= 7;
	public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED		= 8;

	private static final String BUNDLE_PURCHASE_DATA_LIST	= "INAPP_PURCHASE_DATA_LIST";
	private static final String BUNDLE_PURCHASE_DATA		= "INAPP_PURCHASE_DATA";
	private static final String BUNDLE_INAPP_SIGNATURE		= "INAPP_DATA_SIGNATURE";
	private static final String BUNDLE_INAPP_SIGNATURE_LIST	= "INAPP_DATA_SIGNATURE_LIST";
	private static final String BUNDLE_CONTINUATION_TOKEN	= "INAPP_CONTINUATION_TOKEN";
	private static final String BUNDLE_SKU_LIST				= "ITEM_ID_LIST";
	private static final String BUNDLE_SKU_DETAILS_LIST		= "DETAILS_LIST";
	private static final String BUNDLE_BUY_INTENT			= "BUY_INTENT";
	private static final String BUNDLE_RESPONSE_CODE		= "RESPONSE_CODE";

	public static final String TYPE_INAPP	= "inapp";
	public static final String TYPE_SUBS	= "subs";

	protected Activity mActivity;
	protected Context mContext;
	protected IInAppBillingService mService;
	protected boolean mConnected;
	protected boolean mSubsSupported;
	protected PayServiceCallbacks mListener;
	protected int mRequestCode;
	private boolean mPurchasingItemType;
	private Handler mHandler;

	public AndroidPaySystem(Activity activity, PayServiceCallbacks listener, int requestCode) {
		mActivity = activity;
		mContext = activity.getApplicationContext();
		mListener = listener;
		mRequestCode = requestCode;
		mHandler = new Handler();
	}

	@Override
	public void start() {
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		//check if the service exists
		if (mContext.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty()) {
			mListener.onStartFinished(false);
		} else {
			mContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void stop() {
		mConnected = false;
		if (mService != null) {
			mContext.unbindService(this);
		}
	}

	@Override
	public boolean isConnected() {
		return mConnected;
	}

	@Override
	protected void queryPurchases() {
		(new Thread(new Runnable() {
            @Override
			public void run() {
            	final List<Purchase> purchases = new YangList<Purchase>();
        		final boolean success = queryPurchases(false, purchases) && queryPurchases(true, purchases);
        		mHandler.post(new Runnable() {
					@Override
					public void run() {
						mListener.onPurchasesLoaded(purchases, success);
					}
				});
            }
        })).start();
	}

	private boolean queryPurchases(boolean subs, List<Purchase> purchases) {
		boolean signatureCorrect = true;
		String continueToken = null;
		String type = TYPE_INAPP;
		if (subs) type = TYPE_SUBS;

		do {
			try {
				Bundle ownedItems = mService.getPurchases(3, mContext.getPackageName(), type, continueToken);
				//get responseCode
				int responseCode = getResponseCode(ownedItems.get(BUNDLE_RESPONSE_CODE));
				if (responseCode != BILLING_RESPONSE_RESULT_OK) {
					System.err.println("invalid responseCode: "+responseCode);
					return false;
				}

				//check if all required fields exist
				if (!ownedItems.containsKey(BUNDLE_PURCHASE_DATA_LIST) || !ownedItems.containsKey(BUNDLE_INAPP_SIGNATURE_LIST)) {
					System.err.println("response missing fields");
					return false;
				}
				//get fields
				ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(BUNDLE_PURCHASE_DATA_LIST);
				ArrayList<String> signatureList = ownedItems.getStringArrayList(BUNDLE_INAPP_SIGNATURE_LIST);

				int size = purchaseDataList.size();
				for (int i = 0; i < size; i++) {
					String purchaseData = purchaseDataList.get(i);
					String signature = signatureList.get(i);

					if (verify(purchaseData, signature)) {
						purchases.add(new Purchase(subs, purchaseData, signature));
					} else {
						signatureCorrect = false;
					}
				}

				//get token to continue
				continueToken = ownedItems.getString(BUNDLE_CONTINUATION_TOKEN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (!TextUtils.isEmpty(continueToken));

		return signatureCorrect;
	}

	@Override
	protected void querySkuDetails(final ArrayList<String> skus) {
		(new Thread(new Runnable() {
            @Override
			public void run() {
            	final YangList<SkuDetails> details = new YangList<SkuDetails>();
        		final boolean success = querySkuDetails(false, skus, details) && querySkuDetails(false, skus, details);
        		mHandler.post(new Runnable() {
					@Override
					public void run() {
						mListener.onSkuDetailsLoaded(details, success);
					}
				});
            }
        })).start();
	}

	private boolean querySkuDetails(boolean subs, ArrayList<String> skus, List<SkuDetails> details) {
		Bundle querySkus = new Bundle();
		String type = TYPE_INAPP;
		if (subs) type = TYPE_SUBS;
		querySkus.putStringArrayList(BUNDLE_SKU_LIST, skus);
		try {
			Bundle skuDetails = mService.getSkuDetails(3, mContext.getPackageName(), type, querySkus);

			if (!skuDetails.containsKey(BUNDLE_SKU_DETAILS_LIST)) {
				int responseCode = getResponseCode(skuDetails.get(BUNDLE_RESPONSE_CODE));
				if (responseCode != BILLING_RESPONSE_RESULT_OK) {
					System.err.println("invalid responseCode: "+responseCode);
					return false;
				} else {
					System.err.println("detail list is empty");
					return false;
				}
			}
			ArrayList<String> responseList = skuDetails.getStringArrayList(BUNDLE_SKU_DETAILS_LIST);
			for (String skuDetail : responseList) {
				details.add(new SkuDetails(subs, skuDetail));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private int getResponseCode(Object response) {
		int responseCode = BILLING_RESPONSE_RESULT_ERROR;
		if (response == null) responseCode = BILLING_RESPONSE_RESULT_OK;
		else if (response instanceof Integer) responseCode = ((Integer) response).intValue();
		else if (response instanceof Long) responseCode = (int)((Long) response).longValue();
		return responseCode;
	}

	@Override
	public void purchase(SkuDetails item) {
		//can't purchase subscription if subscriptions are not possible
		if (!mSubsSupported && item.mSubscription) {
			mListener.onPurchaseFinished(null, false);
			return;
		}

		try {
			//get the buy intent
			Bundle buyIntentBundle = mService.getBuyIntent(3, mContext.getPackageName(), item.mSku, item.mType, getUserPayload());
			mPurchasingItemType = item.mType==TYPE_SUBS;
			int response = getResponseCode(buyIntentBundle.get(BUNDLE_RESPONSE_CODE));
			if (response != BILLING_RESPONSE_RESULT_OK) {
				mListener.onPurchaseFinished(null, false);
				return;
			}
			//start the buy intent
			PendingIntent pendingIntent = buyIntentBundle.getParcelable(BUNDLE_BUY_INTENT);
			mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(), mRequestCode, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
		} catch (Exception e) {
			mListener.onPurchaseFinished(null, false);
			e.printStackTrace();
		}
	}

	public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != mRequestCode) return false;

		if (data == null) {
			System.err.println("empty data received");
			mListener.onPurchaseFinished(null, false);
			return true;
		}

		int responseCode = getResponseCode(data.getExtras().get(BUNDLE_RESPONSE_CODE));
		String purchaseData = data.getStringExtra(BUNDLE_PURCHASE_DATA);
		String dataSignature = data.getStringExtra(BUNDLE_INAPP_SIGNATURE);

		if (resultCode == Activity.RESULT_OK && responseCode == BILLING_RESPONSE_RESULT_OK) {
            if (purchaseData == null || dataSignature == null) {
                System.err.println("null purchase data or signature");
                mListener.onPurchaseFinished(null, false);
                return true;
            }

            Purchase purchase = null;
            try {
                purchase = new Purchase(mPurchasingItemType, purchaseData, dataSignature);

                // Verify signature
                if (!verify(purchaseData, dataSignature)) {
                    System.err.println("signature verification failed");
                    mListener.onPurchaseFinished(purchase, false);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("parsing purchase data failed");
                mListener.onPurchaseFinished(null, false);
                return true;
            }

            mListener.onPurchaseFinished(purchase, true);
        } else if (resultCode == Activity.RESULT_OK) {
        	System.err.println("resultCode was ok but purchase failed");
            mListener.onPurchaseFinished(null, false);
        } else if (resultCode == Activity.RESULT_CANCELED) {
        	System.err.println("user canceled purchase, responseCode:"+responseCode);
        	mListener.onPurchaseFinished(null, false);
        } else {
        	System.err.println("purchase failed: resultCode:"+resultCode+" responseCode:"+responseCode);
        	mListener.onPurchaseFinished(null, false);
        }
        return true;
	}

	/**
	 * Consumes the given purchase. Runs in a separate thread.
	 * @param p purchase to consume
	 * @param notifyListener Whether the list
	 * @return
	 */
	protected void consume(final Purchase p, final boolean notifyListener) {
		if (p.mSubscription) {
			System.err.println("can't consume subscription");
			return;
		}

		//run in a new thread
		(new Thread(new Runnable() {
            @Override
			public void run() {
            	try {
            		//consume
        			int responseCode = mService.consumePurchase(3, mContext.getPackageName(), p.mToken);
        			if (responseCode == BILLING_RESPONSE_RESULT_OK) {
        				if (notifyListener) {
        					mHandler.post(new Runnable() {
        						@Override
        						public void run() {
        							mListener.onPurchaseConsumed(p);
        						}
        					});
        				}
        			} else {
        				System.err.println("error consuming sku:"+p.mSku+" code:"+responseCode);
        			}

        		} catch (Exception e) {
        			e.printStackTrace();
        		}

            }
        })).start();
	}

	private boolean verify(String purchaseData, String signature) {
		//TODO verify signature, probably native code?
		return true;
	}

	private String getUserPayload() {
		//TODO add some unique user id
		return null;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mService = null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = IInAppBillingService.Stub.asInterface(service);
		String packageName = mContext.getPackageName();

		try {
			//check if payments are supported
			int response = mService.isBillingSupported(3, packageName, TYPE_INAPP);
			if (response != BILLING_RESPONSE_RESULT_OK) {
				mListener.onStartFinished(false);
				return;
			}
			//check if subscriptions are supported
			response = mService.isBillingSupported(3, packageName, TYPE_SUBS);
			if (response == BILLING_RESPONSE_RESULT_OK) {
				mSubsSupported = true;
			}
			mConnected = true;

		} catch (Exception e) {
			mListener.onStartFinished(false);
			e.printStackTrace();
			return;
		}

		mListener.onStartFinished(true);
	}

}
