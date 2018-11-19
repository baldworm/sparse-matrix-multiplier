package jar.n.sas;

import java.util.ArrayList;
import java.util.Iterator;

public class CrsMatrix implements SparseMatrix, SparseMatrixSupport{

    private long[] values;
    private int[] columnIndices;
    private int[] rowPointers;

    public CrsMatrix(long[] values, int[] columnIndices, int[] rowPointers) {
        this.values = values;
        this.columnIndices = columnIndices;
        this.rowPointers = rowPointers;
    }

    public CrsMatrix(long[][] initial, float density) {
        int trueValuesCount = (int) (initial.length * initial[0].length * density);
        columnIndices = new int[trueValuesCount];
        rowPointers = new int[initial.length];
        values = new long[trueValuesCount];

        int nextElementIndex = 0;
        int nextRowPointer = 0;

        int columnsNum = initial[0].length;
        for (int row = 0; row < initial.length; row++) {
            for (int column = 0; column < columnsNum; column++){
                long value = initial[row][column];
                if (value != 0){
                    values[nextElementIndex] = value;
                    columnIndices[nextElementIndex] = column;
                    nextElementIndex ++;
                }
            }
            rowPointers[nextRowPointer++] = nextElementIndex;
        }
    }


    @Override
    public long get(int col, int row) {
        int columnIndex = findColumnIndex(col, row);
        if (columnIndex != -1) {
            return values[columnIndex];
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
            values[columnIndex] = value;
        }


    }

    private int findColumnIndex(int col, int row){
        int from = row >= 1 ? rowPointers[row - 1] : 0;
        int to = rowPointers[row];
        for (int columnIndex = from; columnIndex < to; columnIndex++){
            if (columnIndices[columnIndex] == col){
                return columnIndex;
            }
        }
        return -1;
    }

    @Override
    public SparseMatrix multiply(SparseMatrix matrix) {
        CrsMatrix transposed = ((CrsMatrix)matrix).transpose();
        int size = this.columnIndices.length + transposed.columnIndices.length; // todo: make auto allocating
        long[] cValues = new long[size];
        int[] cColumnIndices = new int[size];
        int[] cRowPointers = new int[((CrsMatrix) matrix).rowPointers.length];

        int nextElementIndex = 0;
        int nextRowPointer = 0;

        for (int rowA = 0; rowA < this.rowPointers.length; rowA++) {
            int fromIndexA = rowA >= 1 ? this.rowPointers[rowA - 1]: 0;
            int toIndexA = this.rowPointers[rowA]; //could be cached


            for (int rowB = 0; rowB < transposed.rowPointers.length; rowB++){
                int fromIndexB = rowB >= 1 ? transposed.rowPointers[rowB - 1] : 0;
                int toIndexB = transposed.rowPointers[rowB]; //could be cached

                Iterator<PairIndex> twoColumnMultiplierIterator =
                        new TwoNonZeroColumnsMultiplierIterator(this.columnIndices, fromIndexA, toIndexA,
                                                          transposed.columnIndices, fromIndexB, toIndexB);
                long value = 0;
                while(twoColumnMultiplierIterator.hasNext()) {

                    PairIndex columnPairIndex = twoColumnMultiplierIterator.next();
                    long result = this.values[columnPairIndex.indexA] * transposed.values[columnPairIndex.indexB];
                    if (result != 0) {
                        value += result;
                    }
                }
                if (value != 0) {
                    cValues[nextElementIndex] = value;
                    cColumnIndices[nextElementIndex] = rowB;
                    nextElementIndex ++;
                }
            }
            cRowPointers[nextRowPointer++] = nextElementIndex;

        }
        return new CrsMatrix(cValues, cColumnIndices, cRowPointers);
    }




    public CrsMatrix transpose() {

        ArrayList<ArrayList<Long>> tempVectorValues = new ArrayList<>(this.columnIndices.length);
        ArrayList<ArrayList<Integer>> tempVectorColumns = new ArrayList<>(this.columnIndices.length);

        for (int i = 0; i < this.columnIndices.length; i++) {
            tempVectorValues.add(new ArrayList<>());
            tempVectorColumns.add(new ArrayList<>());
        }


        for (int i = 0; i < this.columnIndices.length; i++) {

            int currentColumn = this.columnIndices[i];
            tempVectorValues.get(currentColumn).add(this.values[i]);
            tempVectorColumns.get(currentColumn).add(getCurrentRow(i));

        }

        int size = this.columnIndices.length;
        long[] transposedValues = new long[size];
        int[] transposedColumnIndices = new int[size];
        int[] transposedRowPointers = new int[tempVectorColumns.size()];

        int currentIndex = 0;

        for (int i = 0; i < tempVectorColumns.size(); i++) {

            int currentSize = tempVectorColumns.get(i).size();
            for (int j = 0; j < currentSize; j++) {
                transposedValues[currentIndex] = tempVectorValues.get(i).get(j);
                transposedColumnIndices[currentIndex] = tempVectorColumns.get(i).get(j);
                currentIndex++;
            }
            int previousRowPointer = i>0 ? transposedRowPointers[i - 1] : 0;
            transposedRowPointers[i] = previousRowPointer + currentSize;
        }

        return new CrsMatrix(transposedValues, transposedColumnIndices, transposedRowPointers);
    }

    private Integer getCurrentRow(int columnIndexIndex) {
        for (int i = 0; i < rowPointers.length; i++) {
            if (rowPointers[i] > columnIndexIndex) {
                return i;
            }
        }
        throw new RuntimeException("Shouldnt be here, check your code");
    }

    private class TwoNonZeroColumnsMultiplierIterator implements Iterator<PairIndex> {

        private int currentColumnIndexA;
        private int currentColumnIndexB;

        private int[] columnsA;
        private int[] columnsB;
        private int aTo;
        private int bTo;

        public TwoNonZeroColumnsMultiplierIterator(int[] columnsA, int aFrom, int aTo,
                                                   int[] columnsB, int bFrom, int bTo) {

            this.columnsA = columnsA;
            this.columnsB = columnsB;
            this.aTo = aTo;
            this.bTo = bTo;


            currentColumnIndexA = aFrom;
            currentColumnIndexB = bFrom;

        }

        @Override
        public boolean hasNext() {
            return hasNextColumns(currentColumnIndexA, currentColumnIndexB);
        }

        private boolean hasNextColumns(int indexA, int indexB) {
            long valueA = columnsA[indexA];
            long valueB = columnsB[indexB];

            if (valueA == valueB) {
                return true;
            } else if (valueA < valueB) {
                currentColumnIndexA++;
            } else {
                currentColumnIndexB++;
            }
            if (currentColumnIndexA > aTo || currentColumnIndexB > bTo){
                return false;
            }
            return hasNextColumns(currentColumnIndexA, currentColumnIndexB);
        }

        @Override
        public PairIndex next() {
            return new PairIndex(currentColumnIndexA, currentColumnIndexB);
        }
    }

    private class PairIndex {
        int indexA;
        int indexB;

        PairIndex(int indexA, int indexB) {
            this.indexA = indexA;
            this.indexB = indexB;
        }
    }
}
