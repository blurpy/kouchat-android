
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
import static org.mockito.Mockito.*;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link DateTools}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DateToolsTest {

    private DateTools dateTools;

    @Before
    public void setUp() {
        dateTools = spy(new DateTools());
    }

    @Test
    public void currentDateToStringShouldUseDateToStringWithNull() {
        when(dateTools.dateToString(nullable(Date.class), anyString())).thenReturn("the date");

        final String dateAsString = dateTools.currentDateToString("EEEE, d MMMM yyyy");

        assertEquals("the date", dateAsString);
        verify(dateTools).dateToString(null, "EEEE, d MMMM yyyy");
    }

    @Test
    public void dateToStringShouldConvertSpecifiedDateToStringWithCorrectFormat() {
        final Date date = new DateTime().withDate(2014, 5, 13).withTime(23, 52, 28, 0).toDate();

        final String dateAsString = dateTools.dateToString(date, "dd.MM.yyyy HH:mm:ss");

        assertEquals("13.05.2014 23:52:28", dateAsString);
    }

    @Test
    public void dateToStringShouldUseTodayWhenDateIsNull() {
        final String dateAsString = dateTools.dateToString(null, "yyyy");

        assertEquals(String.valueOf(new DateTime().getYear()), dateAsString);
    }

    @Test
    public void howLongFromNowShouldReturnZeroIfThenIsZero() {
        final String howLongFromNow = dateTools.howLongFromNow(0);

        assertEquals("0 days, 00:00:00", howLongFromNow);
    }

    @Test
    public void howLongFromNowShouldReturnCorrectWhenSeconds() {
        final DateTime then = new DateTime()
                .minusSeconds(3);
        final String howLongFromNow = dateTools.howLongFromNow(then.toDate().getTime());

        assertEquals("0 days, 00:00:03", howLongFromNow);
    }

    @Test
    public void howLongFromNowShouldReturnCorrectWhenMinutes() {
        final DateTime then = new DateTime()
                .minusSeconds(12)
                .minusMinutes(5);
        final String howLongFromNow = dateTools.howLongFromNow(then.toDate().getTime());

        assertEquals("0 days, 00:05:12", howLongFromNow);
    }

    @Test
    public void howLongFromNowShouldReturnCorrectWhenHours() {
        final DateTime then = new DateTime()
                .minusSeconds(51)
                .minusMinutes(22)
                .minusHours(5);
        final String howLongFromNow = dateTools.howLongFromNow(then.toDate().getTime());

        assertEquals("0 days, 05:22:51", howLongFromNow);
    }

    @Test
    public void howLongFromNowShouldReturnCorrectWhenDays() {
        final DateTime then = new DateTime()
                .minusSeconds(6)
                .minusMinutes(44)
                .minusHours(14)
                .minusDays(2);
        final String howLongFromNow = dateTools.howLongFromNow(then.toDate().getTime());

        // Note: this might fail when logonTime is summer time and current time is not
        assertEquals("2 days, 14:44:06", howLongFromNow);
    }
}
