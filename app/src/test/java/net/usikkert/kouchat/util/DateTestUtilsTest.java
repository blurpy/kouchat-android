
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

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Test of {@link DateTestUtils}.
 *
 * @author Christian Ihle
 */
public class DateTestUtilsTest {

    @Test
    public void isNowShouldBeTrueForNewDate() {
        assertTrue(DateTestUtils.isNow(new Date()));
    }

    @Test
    public void isNowShouldBeFalseFor10SecondsAgo() {
        assertFalse(DateTestUtils.isNow(new DateTime().minusSeconds(10).toDate()));
    }

    @Test
    public void isNowShouldBeFalseFor10SecondsInTheFuture() {
        assertFalse(DateTestUtils.isNow(new DateTime().plusSeconds(10).toDate()));
    }
}
