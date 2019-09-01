
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

package net.usikkert.kouchat.misc;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

/**
 * Notifies the user interface when the day changes.
 * Checks every hour, in case daylight saving changes the time.
 *
 * @author Christian Ihle
 */
public class DayTimer extends TimerTask {

    /**
     * Which hour of the day the timer should notify about
     * day change.
     */
    private static final int NOTIFY_HOUR = 0;

    /**
     * How often the timer should check if the day has changed,
     * in milliseconds. Currently set to 1 hour.
     */
    private static final long TIMER_INTERVAL = 1000 * 60 * 60;

    /** The actual timer. */
    private final Timer timer;

    /** The controller for showing messages in the ui. */
    private final MessageController msgController;

    /** If the day changed check is done for the day. */
    private boolean done;

    /**
     * Constructor. Starts the timer.
     *
     * @param ui The user interface.
     */
    public DayTimer(final UserInterface ui) {
        msgController = ui.getMessageController();
        timer = new Timer("DayTimer");
    }

    public void startTimer() {
        final Calendar cal = Calendar.getInstance();

        // Starts the timer at the next hour
        cal.add(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        timer.scheduleAtFixedRate(this, new Date(cal.getTimeInMillis()), TIMER_INTERVAL);
    }

    /**
     * Stops the timer. After this, no more day checks are made.
     */
    public void stopTimer() {
        timer.cancel();
    }

    /**
     * This method is run by the timer every hour, and
     * compares the current time against the time when
     * the day changes.
     */
    @Override
    public void run() {
        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // Needs an extra check, so the message only shows once a day.
        if (hour == NOTIFY_HOUR && !done) {
            final String date = Tools.dateToString(null, "EEEE, d MMMM yyyy");
            msgController.showSystemMessage("Day changed to " + date);
            done = true;
        }

        else if (hour != NOTIFY_HOUR && done) {
            done = false;
        }
    }
}
