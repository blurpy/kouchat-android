
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for working with date and time.
 *
 * @author Christian Ihle
 */
public class DateTools {

    /**
     * Converts the current date to a string, in the format specified.
     *
     * @param format The format to get the current date in.
     * @return A converted date.
     * @see SimpleDateFormat
     */
    public String currentDateToString(final String format) {
        return dateToString(null, format);
    }

    /**
     * Converts the specified date to a string, in the format specified.
     *
     * @param date The date to convert to a string.
     * @param format The format to get the date in.
     * @return A converted date.
     * @see SimpleDateFormat
     */
    public String dateToString(@Nullable final Date date, final String format) {
        return Tools.dateToString(date, format);
    }

    /**
     * Returns a string showing how long has passed from 'then' to now.
     *
     * @param then An earlier time.
     * @return How long it's been since 'then'.
     */
    public String howLongFromNow(final long then) {
        if (then != 0) {
            final long diff = System.currentTimeMillis() - then;
            final long totSec = diff / 1000;

            final int oneday = 86400;
            final int onehour = 3600;
            final int onemin = 60;

            final int days = Math.round(totSec / oneday);
            final int hours = Math.round(totSec - days * oneday) / onehour;
            final int minutes = Math.round(totSec - days * oneday - hours * onehour) / onemin;
            final int seconds = Math.round(totSec - days * oneday - hours * onehour - minutes * onemin);

            return days + " days, " + Tools.getDoubleDigit(hours) + ":" + Tools.getDoubleDigit(minutes) +
                    ":" + Tools.getDoubleDigit(seconds);
        }

        else {
            return "0 days, 00:00:00";
        }
    }
}
