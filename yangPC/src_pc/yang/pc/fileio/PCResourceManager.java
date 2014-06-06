package yang.pc.fileio;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import yang.model.PathSpecs;
import yang.systemdependent.AbstractResourceManager;
import yang.systemdependent.OnFileSelectedListener;

public class PCResourceManager extends AbstractResourceManager {

	@Override
	public File getSystemFile(String filename) {
		return new File(filename);
	}

	@Override
	public File getExternalFile(String filename) {
		return new File(filename);
	}

	public File getAssetFile(String filename) {
		File file;
		final String[] paths = PathSpecs.ASSET_PATHS;
		int i = 0;
		do {
			file = new File(paths[i] + filename);
			i++;
		} while (i < paths.length && !file.exists());
		if (!file.exists())
			return null;
		else
			return file;
	}

	@Override
	public InputStream getAssetInputStream(String filename) {
		final File file = getAssetFile(filename);
		if (file == null)
			return null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (final Exception e) {
			return null;
		}
		return fis;
	}

	// Optional override
	@Override
	public BufferedReader loadAssetTextFile(String filename) {
		try {
			final File file = getAssetFile(filename);
			return new BufferedReader(new FileReader(file));
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Optional override
	@Override
	public String textFileToString(String filename) {
		String shader = null;
		try {
			final File file = getAssetFile(filename);
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
		// final File[] files = new
		// File(PathSpecs.ASSET_PATH+directory).listFiles();
		final File[] files = getAssetFile(directory).listFiles();
		final String[] result = new String[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = files[i].getName();
		}
		return result;
	}

	@Override
	public OutputStream getAssetOutputStream(String filename) {
		return getExternalOutputStream(PathSpecs.ASSET_PATHS[0] + filename);
	}

	@Override
	public boolean saveImage(OutputStream stream, String extension, ByteBuffer data,int width,int height,boolean flipY) {
//		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//		byte[] image = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
//		int c = width*height*4;
//		for (int idx=0; idx<c; idx += 4) {
//			image[idx] = data.get(idx + 3);
//			image[idx + 1] = data.get(idx + 2);
//			image[idx + 2] = data.get(idx + 1);
//			image[idx + 3] = data.get(idx);
//		}

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		byte[] image = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		int w4 = width*4;
		for(int y=0;y<height;y++) {
			int tarBase = y*w4;
			int dstBase = (flipY?(height-1-y):y)*w4;
			for (int x=0; x<w4; x += 4) {
				int tarX = tarBase+x;
				int dstX = dstBase+x;
				image[tarX] = data.get(dstX+3);
				image[tarX+1] = data.get(dstX+2);
				image[tarX+2] = data.get(dstX+1);
				image[tarX+3] = data.get(dstX);
			}
		}

//		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		int[] image = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
//		data.rewind();
//		data.asIntBuffer().get(image,0,width*height);

		try {
			ImageIO.write(img, extension, stream);
			stream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	final static String[] allowedImageExt = new String[]{".jpg",".jpeg",".png", ".bmp"};

	@Override
	public void selectImageDialog(OnFileSelectedListener listener) {
		super.selectImageDialog(listener);
		JFileChooser fc = new JFileChooser(new File(PathSpecs.getMainAssetPath()));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "images";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) return true;
				String ext = file.getName();
				for (String allowedExt : allowedImageExt) {
					if (ext.toLowerCase().endsWith(allowedExt)) return true;
				}
				return false;
			}
		});

		int val = fc.showOpenDialog(null);
		switch (val) {
			case JFileChooser.APPROVE_OPTION:
				this.onFileSelected(fc.getSelectedFile().getAbsolutePath());
			default:
				this.onFileSelected(fc.getSelectedFile().getAbsolutePath());
		}
	}
}
