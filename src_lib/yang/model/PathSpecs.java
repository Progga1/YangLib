package yang.model;

public class PathSpecs {

	public static String[] ASSET_PATHS = new String[]{""};

	public static void setAssetPath(String path) {
		ASSET_PATHS = new String[]{path};
	}

	public static void setAssetPaths(String... paths) {
		ASSET_PATHS = paths;
	}

	public static String getMainAssetPath() {
		return ASSET_PATHS[0];
	}

}
