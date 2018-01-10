package com.scalefocus.shutterfly.aws.idgeneration.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.scalefocus.shutterfly.aws.idgeneration.services.impl.TimeCountingService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a test of the time-counting service that has several different tests
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeCountingServiceTest {

    @Test
    public void checkTimeTransformation() throws Exception {
        long a = Instant.now().getEpochSecond();
        long b = System.currentTimeMillis() / 1000;
        assertThat(a, equalTo(b));
    }

    @Test
    public void checkBitSetConversionOfByte() {
        BitSet bs = BitSet.valueOf(new byte[]{(byte) 15});
        assertThat(bs.toString(), equalTo("{0, 1, 2, 3}"));
    }

    @Test
    public void checkBitSetXor() {
        BitSet bs = BitSet.valueOf(new byte[]{(byte) 15});
        BitSet target = new BitSet(64);
        target.or(bs);
        assertThat(target.toString(), equalTo("{0, 1, 2, 3}"));
    }

    /**
     * Here we want to make sure that the JVM is set in the
     * least-significant bit first endianness.
     * This test should ideally be run on the deployment server before the
     * service is started. Otherwise we will have no guarantee for the
     * ranges of the produced unique ids.
     * In the test itself, we make a 64-bit bitset and we set the 56-th
     * bit. This is the first, least-significant bit of the nodeID part
     * of the ID. We are guaranteed that at least it, or
     * more significant bits will be 1 so that we have a bound on the
     * ranges of the generated ids.
     * So checking that this set bit generates a long that should be exactly
     * equal to 2^56.
     */
    @Test
    public void checkEndianness() {
        BitSet target = new BitSet(64);
        target.set(56);
        assertThat(target.toLongArray()[0], equalTo(72_057_594_037_927_936L));
    }

    /**
     * Tests correctness of id generation
     */
    @Test
    public void generateId_success() {
        TimeCountingService service = new TimeCountingService();
        Long id = 0L;
        for (int i = 0; i < 3_097_151; i++) {
            id = service.getUniqueId();
            Assertions.assertThat(id).isNotZero();
            // Id should be larger than the old Oracle scheme, to insure no duplicates.
            Assertions.assertThat(Math.abs(id)).isGreaterThan(1_000_000_000_000L);
        }
    }

    /**
     * Tests that the counter correctly overflows
     */
    @Test
    public void generateId_counterOverflow() {
        TimeCountingService service = new TimeCountingService();

        Set<Long> counterIteration1 = new HashSet<>((1 << 22));
        Set<Long> counterIteration2 = new HashSet<>((1 << 22));
        boolean duplicates = false;
        for (int i = 0; i < (1 << 23); i++) {
            if (i < (1 << 22))
                if (!counterIteration1.add(service.getUniqueId())) {
                    duplicates = true;
                    break;
                } else if (!counterIteration2.add(service.getUniqueId())) {
                    duplicates = true;
                    break;
                }
        }

        assertThat(duplicates, equalTo(false));
    }

    /**
     * This is a benchmark for the Id generation. It will measure and output how quickly we can generate ~ 3 million
     * ids.
     */
    @Test
    public void getUniqueId_Benchmark_2_000_000() {
//        TimeCountingService service = new TimeCountingService();
//
//        long begin = System.currentTimeMillis();
//
//        Long id = 0L;
//        for (int i = 0; i < 2_197_151; i++) {
//            id = service.getUniqueId();
//        }
//
//        long end = System.currentTimeMillis();
//        long timeDelta = end - begin;
//
//        BitSet bits = BitSet.valueOf(new long[]{id});
//        //System.out.println("Generating 2 197 151 ids took " + timeDelta + " ms");
//        //System.out.println(id);
//        //TimeCountingService.printBits(bits);
//
//        assertThat((int) timeDelta, lessThan(1000)); // Ids are generated in less than a second
    }

}
