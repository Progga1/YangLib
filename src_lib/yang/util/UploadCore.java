package yang.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadCore {

	private static UploadResponseListener listener;

	private static String serverUrl;
	private static String scriptUrl;

	public interface UploadResponseListener {
		public void uploadFinished(boolean successful, String response);
	}

	/**
	 * Initializes the core
	 * @param callback listener for the upload result
	 * @param serverAddress Address of the server, i.e. "foo.org"
	 * @param scriptAddress Address of the script in the server, i.e. "/some/path/script.php" 
	 */
	public static void init(UploadResponseListener callback, String serverAddress, String scriptAddress) {
		listener = callback;
		serverUrl = serverAddress;
		scriptUrl = scriptAddress;
	}

	public static void uploadFile(final byte[] fileBuffer, UploadResponseListener callback, String serverAddress, String scriptAddress) {
		init(callback, serverAddress, scriptAddress);
		uploadFile(fileBuffer);
	}

	public static void uploadFile(final byte[] fileBuffer) {
		Thread t = new Thread()  {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				DataOutputStream os = null;

				String urlServer = "http://"+serverUrl+scriptUrl;
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary =  "*****";

				try	{

					URL url = new URL(urlServer);
					connection = (HttpURLConnection) url.openConnection();

					// Allow Inputs & Outputs
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);

					// Enable POST method
					connection.setRequestMethod("POST");

					connection.setRequestProperty("Connection", "Keep-Alive");
					connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

					os = new DataOutputStream( connection.getOutputStream() );
					os.writeBytes(twoHyphens + boundary + lineEnd);
					os.writeBytes("Content-Disposition: form-data; name='data';filename='" + serverUrl +"'" + lineEnd);
					os.writeBytes(lineEnd);

					os.write(fileBuffer, 0, fileBuffer.length);

					os.writeBytes(lineEnd);
					os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

					// Responses from the server
					String responseMessage = connection.getResponseMessage();

					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					String responseMsg = rd.readLine(); //we expect only one line as response anyways
					rd.close();

					if (listener != null) listener.uploadFinished(responseMessage.equalsIgnoreCase("OK"), responseMsg);

					os.flush();
					os.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t.start();

		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
