
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link ByteCounter}.
 *
 * @author Christian Ihle
 */
public class ByteCounterTest {

    /** The byte counter being tested. */
    private ByteCounter counter;

    /**
     * Creates a new byte counter before each test.
     */
    @Before
    public void createByteCounter() {
        counter = new ByteCounter();
    }

    /**
     * Tests that the number of bytes per second isn't calculated before
     * a full second has passed.
     */
    @Test
    public void testNoCalculationBeforeSecond() {
        final long bytesAdded = 1024;
        final long currentTime = 999;
        final long timeSpent = counter.updateTimeSpent(currentTime);
        assertEquals(999, timeSpent);
        counter.updateCounters(bytesAdded, timeSpent);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(999, counter.getTimeCounted());
    }

    /**
     * Tests that the number of bytes per second is calculated
     * when exactly one second has passed, and that the counters
     * are reset since no time is left.
     */
    @Test
    public void testCalculationAfterOneSecond() {
        final long bytesAdded = 1024;
        final long currentTime = 1000;
        final long timeSpent = counter.updateTimeSpent(currentTime);
        assertEquals(1000, timeSpent);
        counter.updateCounters(bytesAdded, timeSpent);
        assertEquals(1024, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests that the counters and calculations are correct when 3
     * updates are needed to fill a whole second.
     */
    @Test
    public void testWith3Updates() {
        final long bytesAdded = 1024;

        final long currentTime1 = 300;
        final long timeSpent1 = counter.updateTimeSpent(currentTime1);
        assertEquals(300, timeSpent1);
        counter.updateCounters(bytesAdded, timeSpent1);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(300, counter.getTimeCounted());

        final long currentTime2 = 750;
        final long timeSpent2 = counter.updateTimeSpent(currentTime2);
        assertEquals(450, timeSpent2);
        counter.updateCounters(bytesAdded, timeSpent2);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(2048, counter.getBytesCounted());
        assertEquals(750, counter.getTimeCounted());

        final long currentTime3 = 1000;
        final long timeSpent3 = counter.updateTimeSpent(currentTime3);
        assertEquals(250, timeSpent3);
        counter.updateCounters(bytesAdded, timeSpent3);
        assertEquals(3072, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests updates happening over 2 seconds, and checking that every
     * second the counters are reset and the speed is calculated.
     */
    @Test
    public void testCountersResetBetweenCalculations() {
        final long bytesAdded = 1024;

        final long currentTime1 = 500;
        final long timeSpent1 = counter.updateTimeSpent(currentTime1);
        assertEquals(500, timeSpent1);
        counter.updateCounters(bytesAdded, timeSpent1);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(500, counter.getTimeCounted());

        final long currentTime2 = 1000;
        final long timeSpent2 = counter.updateTimeSpent(currentTime2);
        assertEquals(500, timeSpent2);
        counter.updateCounters(bytesAdded, timeSpent2);
        assertEquals(2048, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());

        final long currentTime3 = 1500;
        final long timeSpent3 = counter.updateTimeSpent(currentTime3);
        assertEquals(500, timeSpent3);
        counter.updateCounters(bytesAdded, timeSpent3);
        assertEquals(2048, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(500, counter.getTimeCounted());

        final long currentTime4 = 2000;
        final long timeSpent4 = counter.updateTimeSpent(currentTime4);
        assertEquals(500, timeSpent4);
        counter.updateCounters(bytesAdded, timeSpent4);
        assertEquals(2048, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests that the calculations handle too much time spent.
     *
     * <p>What happens in the third update is that 1024 bytes is added
     * in 400ms, but since 900ms is already registered only the bytes
     * added in the first 100ms (256 bytes) is used in the calculation and
     * the rest is saved for the next update.</p>
     */
    @Test
    public void testHandlingOfTimeLeft() {
        final long bytesAdded = 1024;

        final long currentTime1 = 300;
        final long timeSpent1 = counter.updateTimeSpent(currentTime1);
        assertEquals(300, timeSpent1);
        counter.updateCounters(bytesAdded, timeSpent1);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(300, counter.getTimeCounted());

        final long currentTime2 = 900;
        final long timeSpent2 = counter.updateTimeSpent(currentTime2);
        assertEquals(600, timeSpent2);
        counter.updateCounters(bytesAdded, timeSpent2);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(2048, counter.getBytesCounted());
        assertEquals(900, counter.getTimeCounted());

        final long currentTime3 = 1300;
        final long timeSpent3 = counter.updateTimeSpent(currentTime3);
        assertEquals(400, timeSpent3);
        counter.updateCounters(bytesAdded, timeSpent3);
        assertEquals(2304, counter.getBytesPerSec());
        assertEquals(768, counter.getBytesCounted());
        assertEquals(300, counter.getTimeCounted());

        final long currentTime4 = 2000;
        final long timeSpent4 = counter.updateTimeSpent(currentTime4);
        assertEquals(700, timeSpent4);
        counter.updateCounters(bytesAdded, timeSpent4);
        assertEquals(1792, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * The speed should be the same for both seconds this time,
     * since the 1024 bytes added in middle should be split equally
     * between the first and the second calculation.
     */
    @Test
    public void testHandlingOfTimeLeftWithLargerUpdate() {
        final long bytesAdded = 1024;

        final long currentTime1 = 500;
        final long timeSpent1 = counter.updateTimeSpent(currentTime1);
        assertEquals(500, timeSpent1);
        counter.updateCounters(bytesAdded, timeSpent1);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(500, counter.getTimeCounted());

        final long currentTime2 = 1500;
        final long timeSpent2 = counter.updateTimeSpent(currentTime2);
        assertEquals(1000, timeSpent2);
        counter.updateCounters(bytesAdded, timeSpent2);
        assertEquals(1536, counter.getBytesPerSec());
        assertEquals(512, counter.getBytesCounted());
        assertEquals(500, counter.getTimeCounted());

        final long currentTime3 = 2000;
        final long timeSpent3 = counter.updateTimeSpent(currentTime3);
        assertEquals(500, timeSpent3);
        counter.updateCounters(bytesAdded, timeSpent3);
        assertEquals(1536, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * When it takes more than one second between updates,
     * only the bytes added last is used in the calculation. The extra
     * time and bytes are not saved for later.
     *
     * <p>In this case 1024 bytes added in 2.3 seconds gives 445 KB/s.</p>
     */
    @Test
    public void testLongPauseInUpdates() {
        final long bytesAdded = 1024;

        final long currentTime1 = 200;
        final long timeSpent1 = counter.updateTimeSpent(currentTime1);
        assertEquals(200, timeSpent1);
        counter.updateCounters(bytesAdded, timeSpent1);
        assertEquals(0, counter.getBytesPerSec());
        assertEquals(1024, counter.getBytesCounted());
        assertEquals(200, counter.getTimeCounted());

        final long currentTime2 = 2500;
        final long timeSpent2 = counter.updateTimeSpent(currentTime2);
        assertEquals(2300, timeSpent2);
        counter.updateCounters(bytesAdded, timeSpent2);
        assertEquals(445, counter.getBytesPerSec());
        assertEquals(0, counter.getTimeCounted());
        assertEquals(0, counter.getBytesCounted());
    }

    /**
     * Tests the handling of long pause when it's the first
     * update and just a little bit too long.
     */
    @Test
    public void testJustALittleBitTooLong() {
        final long bytesAdded = 1024;
        final long currentTime = 1100;
        final long timeSpent = counter.updateTimeSpent(currentTime);
        assertEquals(1100, timeSpent);
        counter.updateCounters(bytesAdded, timeSpent);
        assertEquals(930, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Another test of long pause, but with nice logical round numbers. :)
     */
    @Test
    public void testTwoSecondUpdate() {
        final long bytesAdded = 1024;
        final long currentTime = 2000;
        final long timeSpent = counter.updateTimeSpent(currentTime);
        assertEquals(2000, timeSpent);
        counter.updateCounters(bytesAdded, timeSpent);
        assertEquals(512, counter.getBytesPerSec());
        assertEquals(0, counter.getTimeCounted());
        assertEquals(0, counter.getBytesCounted());
    }

    /**
     * Tests a very slow file transfer, with 1 KB counted every 4 milliseconds
     * for exactly one second. Which gives a speed of 250 KB/s.
     */
    @Test
    public void testVerySlowTransfer() {
        final long bytesAdded = 1024;

        for (int i = 1; i <= 250; i++) {
            final long timeSpent = counter.updateTimeSpent(i * 4);
            assertEquals(4, timeSpent);
            counter.updateCounters(bytesAdded, timeSpent);

            if (i < 250) {
                assertEquals(1024 * i, counter.getBytesCounted());
                assertEquals(i * 4, counter.getTimeCounted());
            }
        }

        assertEquals(256000, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests a slow file transfer, with 1 KB counted every millisecond
     * for exactly one second. Which gives a speed of 1.000 KB/s.
     */
    @Test
    public void testSlowTransfer() {
        final long bytesAdded = 1024;

        for (int i = 1; i <= 1000; i++) {
            final long timeSpent = counter.updateTimeSpent(i);
            assertEquals(1, timeSpent);
            counter.updateCounters(bytesAdded, timeSpent);

            if (i < 1000) {
                assertEquals(1024 * i, counter.getBytesCounted());
                assertEquals(i, counter.getTimeCounted());
            }
        }

        assertEquals(1024000, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests a fast file transfer, with 1 KB counted 10 times every millisecond
     * for exactly one second. Which gives a speed of 10.000 KB/s (9,77 MB/s).
     */
    @Test
    public void testFastTransfer() {
        final long bytesAdded = 1024;
        int time = 0;

        for (int i = 1; time < 1000; i++) {
            final boolean addTime = (i % 10 == 0 ? true : false);

            if (addTime) {
                time++;
            }

            final long timeSpent = counter.updateTimeSpent(time);

            if (addTime) {
                assertEquals(1, timeSpent);
            } else {
                assertEquals(0, timeSpent);
            }

            counter.updateCounters(bytesAdded, timeSpent);

            if (time < 1000) {
                assertEquals(1024 * i, counter.getBytesCounted());
                assertEquals(time, counter.getTimeCounted());
            }
        }

        assertEquals(10240000, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests a very fast file transfer, with 1 KB counted 50 times every millisecond
     * for exactly one second. Which gives a speed of 50.000 KB/s (48,83 MB/s).
     */
    @Test
    public void testVeryFastTransfer() {
        final long bytesAdded = 1024;
        int time = 0;

        for (int i = 1; time < 1000; i++) {
            final boolean addTime = (i % 50 == 0 ? true : false);

            if (addTime) {
                time++;
            }

            final long timeSpent = counter.updateTimeSpent(time);

            if (addTime) {
                assertEquals(1, timeSpent);
            } else {
                assertEquals(0, timeSpent);
            }

            counter.updateCounters(bytesAdded, timeSpent);

            if (time < 1000) {
                assertEquals(1024 * i, counter.getBytesCounted());
                assertEquals(time, counter.getTimeCounted());
            }
        }

        assertEquals(51200000, counter.getBytesPerSec());
        assertEquals(0, counter.getBytesCounted());
        assertEquals(0, counter.getTimeCounted());
    }

    /**
     * Tests that adding bytes the official way works.
     *
     * <p>It's not easy to make a verification of the result in a consistent
     * way since we are dealing with time, so the test just checks that
     * adding bytes leads to a calculation at some point. This should take
     * about one second.</p>
     */
    @Test
    public void testOfficialAPIWorks() {
        final long bytesAdded = 1024;
        counter.prepare();

        while (counter.getBytesPerSec() == 0) {
            counter.addBytes(bytesAdded);
        }
    }
}
