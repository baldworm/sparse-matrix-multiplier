package jar.n.sas;

import java.util.ArrayList;

public class CrsMatrixByArrayList implements SparseMatrix, SparseMatrixSupport{

    private ArrayList<Long> values;
    private ArrayList<Integer> columnIndices;
    private ArrayList<Integer> rowPointers;

    public CrsMatrixByArrayList(long[][] initial, float density) {
        int trueValuesCount = (int) (initial.length * initial[0].length * density);
        columnIndices = new ArrayList<>(trueValuesCount);
        rowPointers = new ArrayList<>(initial.length);
        values = new ArrayList<>(trueValuesCount);

        for (long[] initialRow : initial) {
            for (int column = 0; column < initial[0].length; column++) {
                long value = initialRow[column];
                if (value != 0) {
                    values.add(value);
                    columnIndices.add(column);
                }
            }
            rowPointers.add(columnIndices.size());
        }
    }


    @Override
    public long get(int col, int row) {
        int columnIndex = findColumnIndex(col, row);
        if (columnIndex != -1) {
            return values.get(columnIndex);
        }else {
            return 0;
        }

    }

    @Override
    public void set(int col, int row, long value) {
        if (value == 0){
            return;
        }

        int columnIndex = findColumnIndex(col, row);
        if (columnIndex != -1) {
            values.set(columnIndex, value);
        }


    }

    private int findColumnIndex(int col, int row){
        int from = row >= 1 ? rowPointers.get(row - 1) : 0;
        int to = rowPointers.get(row);
        for (int columnIndex = from; columnIndex < to; columnIndex++){
            if (columnIndices.get(columnIndex) == col){
                return columnIndex;
            }
        }
        return -1;
    }

    @Override
    public SparseMatrix multiply(SparseMatrix matrix) {
        CrsMatrixByArrayList transposed = CrsMatrixByArrayList.getTransposed((CrsMatrixByArrayList) matrix);
        ArrayList<Long> values = new ArrayList<>();
        ArrayList<Integer> columnIndices = new ArrayList<>();
        ArrayList<Integer> rowPointers = new ArrayList<>();

        for (int rowA = 0; rowA < this.rowPointers.size(); rowA++) {
            int fromIndexA = rowA >= 1 ? this.rowPointers.get(rowA - 1): 0;
            int toIndexA = this.rowPointers.get(rowA);
            for (int rowB = 0; rowB < transposed.rowPointers.size(); rowB++){
                int fromIndexB = rowB >= 1 ? transposed.rowPointers.get(rowB - 1): 0;
                int toIndexB = transposed.rowPointers.get(rowB);
            }

            //todo

        }

        return null;
    }


    public static CrsMatrixByArrayList getTransposed(CrsMatrixByArrayList matrix) {

        return null;
    }
}
