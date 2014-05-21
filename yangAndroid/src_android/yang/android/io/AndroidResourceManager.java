package yang.android.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import yang.systemdependent.AbstractResourceManager;
import android.content.Context;
import android.os.Environment;

public class AndroidResourceManager extends AbstractResourceManager {

	private final Context mContext;

	public AndroidResourceManager(Context context) {
		mContext = context;
	}

	@Override
	public InputStream getAssetInputStream(String filename){
		InputStream is = null;
		try {
			is = mContext.getAssets().open(filename);
		} catch (final Exception e) {
			return null;
		}
		return is;
	}

	@Override
	public String[] getFileList(String directory) throws IOException {
		if(directory.endsWith(File.separator))
			directory = directory.substring(0,directory.length()-1);
		return mContext.getAssets().list(directory);
	}

	@Override
	public File getSystemFile(String filename) {
		return new File(mContext.getFilesDir(), filename);
	}

	@Override
	public File getExternalFile(String filename) {
		return new File(getExternalStorageDir(), filename);
	}

	private File getExternalStorageDir() {
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			final File externalDir = new File(Environment.getExternalStorageDirectory(), mContext.getPackageName());
			if (!externalDir.exists()) externalDir.mkdir();
			return externalDir;
		}
		throw new RuntimeException("external storage not available");
	}

//	@Override
//	public boolean saveImage(String filename,ByteBuffer data) {
//		final File folder = new File(Environment.getExternalStorageDirectory() + directory);
//		boolean success = true;
//		if (!folder.exists()) {
//			success = folder.mkdirs();
//		}
//
//		if (!success) {
//			return false;
//		}
//		File f = new File(folder.getAbsolutePath() + filename + ".jpg");
//
//		try {
//			f.createNewFile();
//			FileOutputStream fo = new FileOutputStream(f);
//			fo.write(data);
//			fo.close();
//
//		} catch (IOException e) {
//			return false;
//		}
//	}
}
