package yang.systemdependent;

public interface OnFileSelectedListener {

	/**
	 * Called when selecting a file.
	 * @param file absolute path to the selected file
	 */
	public void onFileSelected(String file);

}
