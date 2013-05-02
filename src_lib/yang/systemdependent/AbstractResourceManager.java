package yang.systemdependent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

public abstract class AbstractResourceManager {
	
	public abstract String[] getFileList(String directory) throws IOException;
	public abstract InputStream getInputStream(String filename);

	public BufferedReader loadTextFile(String filename) {
		InputStream stream = getInputStream(filename);
		return new BufferedReader(new InputStreamReader(stream));
	}

	public StringBuilder textFileToStringBuilder(String filename) {
		StringBuilder result = new StringBuilder();
		String line; 
		try {
			BufferedReader reader = loadTextFile(filename);
			while((line = reader.readLine())!=null)
				result.append(line).append('\n');
		} catch (IOException e) {
			throw new RuntimeException("Error reading resource: '"+filename+"'");
		}
		return result;
	}

	public String textFileToString(String filename) {
		return textFileToStringBuilder(filename).toString();
	}

	public Properties loadPropertiesFile(String filename) {
		Properties props = new Properties();
		try {
			props.load(getInputStream(filename));
		} catch (Exception e) {
			System.err.println("prop: '"+filename+"' not found");
			e.printStackTrace();
		}
		return props;
	}

	public OutputStream getOutputStream(String filename) throws FileNotFoundException {
		throw new RuntimeException("Output stream not supported");
	}

	public boolean deleteFile(String filename) {		
		throw new RuntimeException("Delete file not supported");
	}

	public void savePropertiesFile(String filename, Properties props) {
		throw new RuntimeException("Save properties not supported");
	}
}
