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
		void uploadFinished(boolean successful, String response);
	}

	/**
	 * Initializes the core
	 * @param callback listener for the upload result
	 * @param serverAddress Address of the server, i.e. "foo.org"
	 * @param scriptAddress Address of the script in the server, i.e. "/some/path/script.php" 
	 */
	public static void init(final UploadResponseListener callback, final String serverAddress, final String scriptAddress) {
		listener = callback;
		serverUrl = serverAddress;
		scriptUrl = scriptAddress;
	}

	public static void uploadFile(final byte[] fileBuffer, final UploadResponseListener callback, final String serverAddress, final String scriptAddress, final String param) {
		init(callback, serverAddress, scriptAddress);
		uploadFile(fileBuffer, param);
	}

	public static void uploadFile(final byte[] fileBuffer, final String param) {
		final Thread t = new Thread()  {
			@Override
			public void run() {
				final HttpURLConnection connection;
				final DataOutputStream os;

				final String urlServer = "http://"+serverUrl+scriptUrl;
				final String charset = "UTF-8";
				final String lineEnd = "\r\n";
				final String twoHyphens = "--";
				final String boundary = Long.toHexString(System.currentTimeMillis());

				try	{

					final URL url = new URL(urlServer);
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

					// Send normal param.
					os.writeBytes(twoHyphens + boundary + lineEnd);
					os.writeBytes("Content-Disposition: form-data; name='param'" + lineEnd);
					os.writeBytes("Content-Type: text/plain; charset=" + charset + lineEnd);
					os.writeBytes(lineEnd + param + lineEnd);
					os.flush();

					// Send binary data
					os.writeBytes(twoHyphens + boundary + lineEnd);
					os.writeBytes("Content-Disposition: form-data; name='data'; filename='foo' " + lineEnd);
					os.writeBytes(lineEnd);
					os.flush();

					os.write(fileBuffer, 0, fileBuffer.length);
					os.flush();

					os.writeBytes(lineEnd);
					os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
					os.flush();
					os.close();

					// Responses from the server
					final String responseMessage = connection.getResponseMessage();

					final InputStream is = connection.getInputStream();
					final BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					String line;
					String responseMsg = "";
					while ((line = rd.readLine()) != null) {
						responseMsg += line;
					}
					rd.close();

					if (listener != null) listener.uploadFinished(responseMessage.equalsIgnoreCase("OK"), responseMsg);

				} catch (final Exception ex) {
					ex.printStackTrace();
					if (listener != null) listener.uploadFinished(false, ex.getMessage());
				}
			}
		};
		t.start();
	}
}
