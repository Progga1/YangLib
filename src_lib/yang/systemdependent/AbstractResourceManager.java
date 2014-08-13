package yang.systemdependent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import yang.graphics.textures.TextureData;

public abstract class AbstractResourceManager {

	private OnFileSelectedListener mFileSelectedListener;

	public abstract String[] getFileList(String directory) throws IOException;
	public abstract InputStream getAssetInputStream(String filename);
	public abstract File getSystemFile(String filename);
	public abstract File getExternalFile(String filename);

	public boolean assetExists(String filename) {
		return getAssetInputStream(filename)!=null;
	}

	public BufferedReader loadAssetTextFile(String filename) {
		final InputStream stream = getAssetInputStream(filename);
		return new BufferedReader(new InputStreamReader(stream));
	}

	public StringBuilder textFileToStringBuilder(String filename) {
		final StringBuilder result = new StringBuilder();
		String line;
		try {
			final BufferedReader reader = loadAssetTextFile(filename);
			while((line = reader.readLine())!=null)
				result.append(line).append('\n');
		} catch (final IOException e) {
			throw new RuntimeException("Error reading resource: '"+filename+"'");
		}
		return result;
	}

	public String textFileToString(String filename) {
		return textFileToStringBuilder(filename).toString();
	}

	public Properties loadPropertiesFile(String filename) {
		final Properties props = new Properties();
		try {
			props.load(getAssetInputStream(filename));
		} catch (final Exception e) {
			System.err.println("prop: '"+filename+"' not found");
			e.printStackTrace();
		}
		return props;
	}

	public boolean fileExistsInFileSystem(String filename) {
		return getSystemFile(filename).exists();
	}

	public boolean fileExistsInExternalFileSystem(String filename) {
		return getExternalFile(filename).exists();
	}

	public InputStream getSystemInputStream(String filename) {
		final File file = getSystemFile(filename);
		if(!file.exists())
			return null;
		else
			try {
				return new FileInputStream(file);
			} catch (final FileNotFoundException e) {
				return null;
			}
	}

	public OutputStream getSystemOutputStream(String filename) {
		final File file = getSystemFile(filename);
		final File parent = file.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();
		try {
			return new FileOutputStream(file);
		} catch (final FileNotFoundException e) {
			return null;
		}
	}

	public OutputStream getExternalOutputStream(String filename) {
		final File file = getExternalFile(filename);
		final File parent = file.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();
		try {
			return new FileOutputStream(file);
		}  catch (final FileNotFoundException e) {
			return null;
		}
	}

	public InputStream getExternalInputStream(String filename) {
		final File file = getExternalFile(filename);
		if(file==null)
			return null;
		else
			try {
				return new FileInputStream(file);
			} catch (final FileNotFoundException e) {
				return null;
			}
	}

	public boolean deleteExternalFile(String filename) {
		final File f = getExternalFile(filename);
		return f.delete();
	}

	public boolean deleteSystemFile(String filename) {
		final File f = getSystemFile(filename);
		return f.delete();
	}

	public OutputStream getAssetOutputStream(String filename) {
		throw new RuntimeException("Asset output stream not supported.");
	}

	public String getAssetFilename(String name, String[] paths, String[] extensions) {
		for(final String path:paths) {
			if(assetExists(path+name))
				return path+name;
			for(final String ext:extensions) {
				if(assetExists(path+name+ext))
					return path+name+ext;
			}
		}
		return null;
	}

	public boolean saveImage(OutputStream stream, String extension, ByteBuffer data,int width,int height,boolean flipY) {
		return false;
	}

	public boolean saveImage(String filename,TextureData image,boolean flipY) {
		int extId = filename.lastIndexOf('.');
		String format;
		if(extId<0) {
			format = "png";
			filename += ".png";
		}else
			format = filename.substring(extId+1);
//		new File(filename).mkdirs();
		return saveImage(getExternalOutputStream(filename),format,image.mData,image.mWidth,image.mHeight,flipY);
	}

	public void selectImageDialog(OnFileSelectedListener listener) {
		mFileSelectedListener = listener;
	}

	public void onFileSelected(String name) {
		if (mFileSelectedListener != null) mFileSelectedListener.onFileSelected(name);
	}

	public File createNonExistingExternalFile(String filename,String format,int minDigits,boolean forceEnum) {

		if(!forceEnum && !this.fileExistsInExternalFileSystem(filename)) {
			return new File(filename);
		}

		if(!format.startsWith("."))
			format = "."+format;
		if(filename.endsWith(format))
			filename = filename.substring(0,filename.length()-format.length()-1);
		String resFilename;
		int i=1;
		do{
			String digits = ""+i;
			while(digits.length()<minDigits)
				digits = "0"+digits;
			resFilename = filename+digits+format;
			i++;
		}while(fileExistsInExternalFileSystem(resFilename));

		return new File(resFilename);
	}

}
