
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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Test utility methods for dates.
 *
 * @author Christian Ihle
 */
public class DateTestUtils {

    /**
     * Returns if the specified date is "now", plus/minus 5 seconds of slack.
     *
     * @param date The date to check.
     * @return If the date is "now".
     */
    public static boolean isNow(final Date date) {
        final Interval nowPlusMinus5Seconds = new Interval(new DateTime().minusSeconds(5),
                                                           new DateTime().plusSeconds(5));

        return nowPlusMinus5Seconds.contains(date.getTime());
    }
}
