package jar.n.sas;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class CrsMatrixTest {

    private static final float INIT_MATRIX_DENSITY = 0.1f;

    private static final long[][] INIT_MATRIX_A = new long[][]{
            //0  1  2  3  4  5
            { 1, 0, 0, 0, 1, 0},// 0
            { 0, 0, 1, 1, 0, 0},// 1
            { 0, 0, 0, 0, 0, 0},// 2
    };
    private static final long[][] INIT_MATRIX_B = new long[][]{
            //0  1  2
            { 1, 0, 0},// 0
            { 0, 0, 1},// 1
            { 1, 1, 0},// 2
            { 0, 0, 0},// 3
            { 1, 0, 0},// 4
            { 0, 0, 0},// 5

    };
    private static final long[][] INIT_MATRIX_Cs = new long[][]{
            //0  1  2
            { 2, 0, 0},// 0
            { 1, 1, 1},// 1
            { 0, 0, 0},// 2
    };

    private static final long[][] INIT_MATRIX = new long[][]{
            //0  1  2  3  4  5  6  7  8  9
            { 0, 0, 0, 0, 0, 9, 0, 0, 0, 0 },// 0
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },// 1
            { 0, 0, 0, 4, 0, 0, 0, 0, 0, 0 },// 2
            { 0, 0, 0, 0, 6, 0, 0, 4, 0, 0 },// 3
            { 0, 0, 0, 0, 0, 0, 5, 0, 0, 0 } // 4
    };
    /*expected results:
     * values = [9,4,6,4,5]
     * colIndeces = [5,3,4,7,6]
     * rowPointers = [0,0,1,3,4]
     * */

    @Test
    public void valueOfTest() {

        SparseMatrix matrix = new CrsMatrix(INIT_MATRIX, INIT_MATRIX_DENSITY);
        assertEquals(9, matrix.get(5, 0));
        assertEquals(4, matrix.get(3, 2));
        assertEquals(6, matrix.get(4, 3));
        assertEquals(4, matrix.get(7, 3));
        assertEquals(5, matrix.get(6, 4));
        assertEquals(0, matrix.get(6, 0));
        assertEquals(0, matrix.get(5, 1));


    }

    @Test
    public void setTest(){
        SparseMatrix matrix = new CrsMatrix(INIT_MATRIX, INIT_MATRIX_DENSITY);
        matrix.set(5,0, 10);
        matrix.set(5,1, 10);
        assertEquals(10, matrix.get(5, 0));
    }

    @Test
    @Ignore
    public void setNewElementTest(){
        SparseMatrix matrix = new CrsMatrix(INIT_MATRIX, INIT_MATRIX_DENSITY);
        matrix.set(5,1, 10);
        assertEquals(10, matrix.get(5, 1));
    }


    @Test
    public void multiplyTest(){
        CrsMatrix a = new CrsMatrix(INIT_MATRIX_A, .4f);
        CrsMatrix b = new CrsMatrix(INIT_MATRIX_B, .4f);
        SparseMatrix c = a.multiply(b);
        assertEquals(2, c.get(0, 0));
        assertEquals(1, c.get(0, 1));
        assertEquals(1, c.get(1, 1));
        assertEquals(0, c.get(1, 2));
    }


    @Test
    public void transpose() {
        long[][] initMatrix = new long[][]{
                //0  1  2  3
                { 0, 3, 0, 7},//0
                { 0, 0, 8, 0},//1
                { 0, 0, 0, 0},//2
                { 9, 0,15,16},//3
        };

        CrsMatrix a = new CrsMatrix(initMatrix, .437f);
        CrsMatrix b = a.transpose();
        assertNotNull(b);
    }
}