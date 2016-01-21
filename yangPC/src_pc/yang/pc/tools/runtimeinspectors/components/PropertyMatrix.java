package yang.pc.tools.runtimeinspectors.components;

import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyMatrix extends PropertyNumArray {

	private int mRows,mCols;

	public PropertyMatrix(int rows,int columns) {
		super(rows*columns);
		mRows = rows;
		mCols = columns;
		mStride = 4-mCols;
		mColumnMajor = true;
		super.mMaxColumns = columns;
	}

	public PropertyMatrix() {
		this(4,4);
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof YangMatrix)
			super.setValueReference(((YangMatrix)reference).mValues);
		else
			super.setValueReference(reference);
	}

	@Override
	public PropertyMatrix clone() {
		return new PropertyMatrix(mRows,mCols);
	}

}
