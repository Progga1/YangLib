package yang.pc.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import yang.model.PathSpecs;
import yang.systemdependent.AbstractResourceManager;


public class PCResourceManager extends AbstractResourceManager {

	@Override
	public InputStream getInputStream(String filename) {
		File file = new File(PathSpecs.ASSET_PATH, filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fis;
	}
	
	@Override
	public InputStream getFileSystemInputStream(String filename) {
		File file = new File(filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fis;
	}

	//Optional override
	@Override
	public BufferedReader loadTextFile(String filename) {
		try {
			File file = new File(PathSpecs.ASSET_PATH, filename);
			return new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Optional override
	@Override
	public String textFileToString(String filename) {
		String shader = null;
		try {
			File file = new File(PathSpecs.ASSET_PATH, filename);
			BufferedReader r = new BufferedReader(new FileReader(file));

			char[] buf = new char[(int) file.length()];
			r.read(buf);

			shader = new String(buf);

			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shader;
	}

	@Override
	public void savePropertiesFile(String filename, Properties props) {
		try {
			FileOutputStream fos = getOutputStream(filename); 
			props.store(fos, null);
			fos.close();
		} catch (Exception e) {
			System.err.println("failed to save property file: " + filename);
		}
	}

	@Override
	public String[] getFileList(String directory) throws IOException {
		File[] files = new File(PathSpecs.ASSET_PATH+directory).listFiles();
		String[] result = new String[files.length];
		for(int i=0;i<result.length;i++) {
			result[i] = files[i].getName();
		}
		return result;
	}

	@Override
	public FileOutputStream getOutputStream(String filename) throws FileNotFoundException {
		return new FileOutputStream(PathSpecs.ASSET_PATH+filename);
	}
	
	@Override
	public FileOutputStream getFileSystemOutputStream(String filename) throws FileNotFoundException {
		return new FileOutputStream(filename);
	}

	@Override
	public boolean deleteFile(String filename) {		
		File f = new File(PathSpecs.ASSET_PATH+filename);
		return f.delete();	
	}

	@Override
	public boolean fileExists(String filename) {
		return new File(PathSpecs.ASSET_PATH+filename).exists();
	}

}
