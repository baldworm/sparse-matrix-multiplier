package jar.n.sas;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Fork(value = 1)
@Measurement(iterations = 10, batchSize = 1, timeUnit = TimeUnit.HOURS)
public class SpeedTest {

    private static final int size = 10000;
    private static final float density = 0.01f;

    private static final long[][] INIT_MATRIX = TestUtils.generateSparsedArray(size, density);

    private static final CrsMatrix CRS_MATRIX = new CrsMatrix(INIT_MATRIX, density);
    private static final CrsMatrixByArrayList CRSNew_MATRIX = new CrsMatrixByArrayList(INIT_MATRIX, density);


    @Benchmark
    public void getElementTestNativeArray() {
        IntStream.range(0, size - 1).forEach((row)-> {
            IntStream.range(0, size - 1).forEach((col)-> {
                CRS_MATRIX.get(col, row);
            });
        });
    }

    @Benchmark
    public void getElementTestArrayList() {
        IntStream.range(0, size - 1).forEach((row)-> {
            IntStream.range(0, size - 1).forEach((col)-> {
                CRSNew_MATRIX.get(col, row);
            });
        });
    }

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }


}
