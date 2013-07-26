package yang.android.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import yang.systemdependent.AbstractResourceManager;
import android.content.Context;

public class AndroidResourceManager extends AbstractResourceManager {

	private Context mContext;
	
	public AndroidResourceManager(Context context) {
		mContext = context;
	}
	
	@Override
	public InputStream getInputStream(String filename){
		InputStream is = null;
		try {
			is = mContext.getAssets().open(filename);
		} catch (Exception e) {
			return null;
		}
		return is;
	}
	
	@Override
	public Properties loadPropertiesFile(String filename) {
		Properties props = new Properties();
		try {
			props.load(mContext.getAssets().open(filename));
		} catch (Exception e) {
			System.err.println("prop: '"+filename+"' not found");
			System.exit(0);
		}
		return props;
	}

	@Override
	public String[] getFileList(String directory) throws IOException {
		if(directory.endsWith(File.separator)) 
			directory = directory.substring(0,directory.length()-1);
		return mContext.getAssets().list(directory);
	}
	
//	@Override
//	public OutputStream getFileSystemOutputStream(String filename) throws FileNotFoundException {
//		
//	}

}
