package yang.pc.tools.runtimeinspectors.components;

import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyMatrix extends PropertyNumArray {

	private int mRows,mCols,mOrigCols;

	public PropertyMatrix(int rows,int columns,int originalColumns,boolean rowMajor) {
		super(rows*columns);
		mRows = rows;
		mCols = columns;
		mOrigCols = originalColumns;
		mStride = originalColumns-mCols;
		mColumnMajor = !rowMajor;
		super.mMaxColumns = columns;
	}
	
	public PropertyMatrix(int rows,int columns,boolean rowMajor) {
		this(rows,columns,columns,rowMajor);
	}
	
	public PropertyMatrix(int rows,int columns) {
		this(rows,columns,false);
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
	
	public int getIndex(int row,int column) {
		return row*mCols + column;
	}

	public float getFloat(int row,int column) {
		return super.getFloat(getIndex(row,column));
	}

	public void setFloat(int row,int column,float value) {
		super.setFloat(getIndex(row,column),value);
	}

	public double getDouble(int row,int column) {
		return super.getDouble(getIndex(row,column));
	}

	public void setDouble(int row,int column,double value) {
		super.setDouble(getIndex(row,column),value);
	}
	
	@Override
	public PropertyMatrix clone() {
		return new PropertyMatrix(mRows,mCols,mOrigCols,mColumnMajor);
	}

}
