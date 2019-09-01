
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

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test of {@link TimerTools}.
 *
 * @author Christian Ihle
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TimerTools.class)
public class TimerToolsTest {

    private TimerTools timerTools;

    @Before
    public void setUp() {
        timerTools = new TimerTools();
    }

    @Test
    public void scheduleTimerTaskShouldScheduleOneTimeTaskWithCorrectNameAndDelay() throws Exception {
        final Timer timer = mock(Timer.class);
        final TimerTask timerTask = createTimerTask();

        whenNew(Timer.class).withAnyArguments().thenReturn(timer);

        timerTools.scheduleTimerTask("TheTimer", timerTask, 123);

        verifyNew(Timer.class).withArguments("TheTimer");
        verify(timer).schedule(timerTask, 123);
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() { }
        };
    }
}
