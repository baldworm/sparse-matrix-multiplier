package jar.n.sas;

public interface SparseMatrix {

    long get(int col, int row);

    void set(int col, int row, long value);
}
