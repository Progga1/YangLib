package yang.pc.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import yang.model.PathSpecs;
import yang.systemdependent.AbstractResourceManager;


public class PCResourceManager extends AbstractResourceManager {

	@Override
	public File getSystemFile(String filename) {
		return new File(filename);
	}

	@Override
	public File getExternalFile(String filename) {
		return new File(filename);
	}

	@Override
	public InputStream getAssetInputStream(String filename) {
		final File file = new File(PathSpecs.ASSET_PATH+filename);
		if(!file.exists())
			return null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (final Exception e) {
			return null;
		}
		return fis;
	}

	//Optional override
	@Override
	public BufferedReader loadAssetTextFile(String filename) {
		try {
			final File file = new File(PathSpecs.ASSET_PATH, filename);
			return new BufferedReader(new FileReader(file));
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Optional override
	@Override
	public String textFileToString(String filename) {
		String shader = null;
		try {
			final File file = new File(PathSpecs.ASSET_PATH, filename);
			final BufferedReader r = new BufferedReader(new FileReader(file));

			final char[] buf = new char[(int) file.length()];
			r.read(buf);

			shader = new String(buf);

			r.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return shader;
	}

	@Override
	public String[] getFileList(String directory) throws IOException {
		final File[] files = new File(PathSpecs.ASSET_PATH+directory).listFiles();
		final String[] result = new String[files.length];
		for(int i=0;i<result.length;i++) {
			result[i] = files[i].getName();
		}
		return result;
	}

	@Override
	public OutputStream getAssetOutputStream(String filename) {
		return getExternalOutputStream(PathSpecs.ASSET_PATH+filename);
	}

}
