package yang.gdx.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import yang.systemdependent.AbstractResourceManager;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class GdxResourceManager extends AbstractResourceManager {

	private static final String LOCAL_ASSET_PATH = "."+File.separatorChar+"bin"+File.separatorChar;
	public static final String ASSET_PATH = ".." + File.separatorChar + "ATouchOfNinjaAndroid" + File.separatorChar + "assets" + File.separatorChar;
	
	@Override
	public InputStream getInputStream(String filename) {
		return Gdx.files.internal(filename).read();
	}

	@Override
	public void savePropertiesFile(String filename, Properties props) {
		if (Gdx.app.getType().equals(ApplicationType.Android)) {
			throw new RuntimeException("unsupported method: GdxResourceManager.savePropertiesFile()");
		} else {
			try {
				OutputStream os = getOutputStream(filename);
				props.store(os, null);
				os.close();
			} catch (Exception e) {
				System.err.println("failed to save property file: " + filename);
			}
		}
	}

	@Override
	public String[] getFileList(String directory) throws IOException {
		if(directory.endsWith(File.separator)) 
			directory = directory.substring(0,directory.length()-1);
	    FileHandle[] files = null;

		if (Gdx.app.getType() == ApplicationType.Android) {
			files = Gdx.files.internal(directory).list();
		} else {
			//FileHandle limitation on pc
			files = Gdx.files.internal(LOCAL_ASSET_PATH+directory).list();
		}
		String[] result = new String[files.length];
		for(int i=0;i<result.length;i++) {
			result[i] = files[i].name();
		}
		return result;
	}
	
	@Override
	public OutputStream getOutputStream(String filename) throws FileNotFoundException {
		if (Gdx.app.getType().equals(ApplicationType.Android)) {
			throw new RuntimeException("unsupported method: GdxResourceManager.getOutputStream()");
		} else {
			try {
				OutputStream osReal = Gdx.files.local(ASSET_PATH+filename).write(false);
				OutputStream osCopy = Gdx.files.local(LOCAL_ASSET_PATH+filename).write(false);
			
				return new MultiOutputStream(osReal, osCopy);
				
			} catch (Exception e) {
				System.err.println("failed to save property file: " + filename);
			}
		}
		
		throw new RuntimeException("failed to open the outputstreams");
	}
	
	@Override
	public boolean deleteFile(String filename) {
		if (Gdx.app.getType().equals(ApplicationType.Android)) return false;
		try {	
			Gdx.files.local(ASSET_PATH+filename).delete();
			Gdx.files.local(LOCAL_ASSET_PATH+filename).delete();

			return true;
		} catch (Exception e) {
			System.err.println("Ninja: Error while deleting a file");
			e.printStackTrace();
		}

		return false;
	}
	
	private class MultiOutputStream extends FilterOutputStream {

		private OutputStream mOS1;
		private OutputStream mOS2;
		
		public MultiOutputStream(OutputStream out1, OutputStream out2) {
			super(null);

			mOS1 = out1;
			mOS2 = out2;
		}

		@Override
		public void write(int b) throws IOException {
			mOS1.write((byte)b);
			mOS2.write((byte)b);
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			mOS1.write(b);
			mOS2.write(b);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			mOS1.write(b, off, len);
			mOS2.write(b, off, len);
		}
		
		@Override
		public void flush() throws IOException {
			mOS1.flush();
			mOS2.flush();
		}
		
		@Override
		public void close() throws IOException {
			mOS1.close();
			mOS2.close();
		}
		
	}
}
