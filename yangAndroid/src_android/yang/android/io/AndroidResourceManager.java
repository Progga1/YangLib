package yang.android.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import yang.systemdependent.AbstractResourceManager;
import android.content.Context;
import android.os.Environment;

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

	@Override
	public InputStream getFileSystemInputStream(String filename) throws FileNotFoundException {
		return new FileInputStream(new File(mContext.getFilesDir(), filename));
	}

	@Override
	public OutputStream getFileSystemOutputStream(String filename) throws FileNotFoundException {
		File newFile = new File(mContext.getFilesDir(), filename);
		File parent = newFile.getParentFile();
		if (!parent.exists()) parent.mkdirs();
		return new FileOutputStream(newFile);
	}

	@Override
	public boolean fileExistsInFileSystem(String filename) {
		return new File(mContext.getFilesDir(), filename).exists();
	}

	@Override
	public OutputStream getExternalFileSystemOutputStream(String filename) throws FileNotFoundException {
		File file = new File(getExternalStorageDir(), filename);
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		return new FileOutputStream(file);
	}

	@Override
	public InputStream getExteralFileSystemInputStream(String filename) throws FileNotFoundException {
		File file = new File(getExternalStorageDir(), filename);
		return new FileInputStream(file);
	}

	@Override
	public boolean fileExistsInExternalFileSystem(String filename) {
		return new File(getExternalStorageDir(), filename).exists();
	}

	private File getExternalStorageDir() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File externalDir = new File(Environment.getExternalStorageDirectory(), mContext.getPackageName());
			if (!externalDir.exists()) externalDir.mkdir();
			return externalDir;
		}
		throw new RuntimeException("external storage not available");
	}
}
