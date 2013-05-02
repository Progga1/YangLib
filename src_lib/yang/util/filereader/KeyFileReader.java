package yang.util.filereader;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class KeyFileReader {

	private BufferedReader mReader;
	private ReaderCallback mCallback;
	
	public KeyFileReader(BufferedReader reader,ReaderCallback readerCallback) {
		mReader = reader;
	}
	
	public void read() {
		String line;
		try {
			while((line = mReader.readLine())!=null) {
				line = line.trim();
				if(!(line.startsWith("#"))) {
					String[] lineSplit = line.split("=");
					if(lineSplit.length>1) {
						mCallback.readKey(lineSplit[0], lineSplit[1]);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Read key file error");
		}
	}
}
