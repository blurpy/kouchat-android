
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

import java.util.Timer;
import java.util.TimerTask;

import org.jetbrains.annotations.NonNls;

/**
 * Utility methods for working with timers.
 *
 * @author Christian Ihle
 */
public class TimerTools {

    /**
     * Schedules a specified one time timer task with the specified delay and name.
     *
     * @param name Name of the timer task thread.
     * @param timerTask The timer task to execute.
     * @param delay Number of milliseconds to wait before executing the timer task.
     */
    public void scheduleTimerTask(@NonNls final String name, final TimerTask timerTask, final long delay) {
        final Timer delayedLogonTimer = new Timer(name);
        delayedLogonTimer.schedule(timerTask, delay);
    }
}
