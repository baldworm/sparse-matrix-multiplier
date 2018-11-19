package jar.n.sas;

import java.util.PrimitiveIterator;
import java.util.Random;

public class TestUtils {
    public static long[][] generateSparsedArray(int size, float dencity) {
        long[][] sparsedArray = new long[size][size];


        PrimitiveIterator.OfInt intStreamIterator = new Random().ints(0, size).iterator();
        for(int k = 0; k < dencity*size*size; k++) {
            int i = intStreamIterator.nextInt();
            int j = intStreamIterator.nextInt();
            int value = intStreamIterator.nextInt();
            sparsedArray[i][j] = value;
        }
        return sparsedArray;
    }
}
