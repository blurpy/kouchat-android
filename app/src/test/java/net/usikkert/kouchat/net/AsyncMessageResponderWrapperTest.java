
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

package net.usikkert.kouchat.net;

import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutorService;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test of {@link AsyncMessageResponderWrapper}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class AsyncMessageResponderWrapperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AsyncMessageResponderWrapper wrapper;

    private MessageResponder messageResponder;
    private Controller controller;
    private ExecutorService executorService;
    private Sleeper sleeper;
    private WaitingList waitingList;

    @Before
    public void setUp() {
        messageResponder = mock(MessageResponder.class);
        controller = mock(Controller.class);
        waitingList = mock(WaitingList.class);
        when(controller.getWaitingList()).thenReturn(waitingList);

        wrapper = spy(new AsyncMessageResponderWrapper(messageResponder, controller));

        executorService = TestUtils.setFieldValueWithMock(wrapper, "executorService", ExecutorService.class);
        sleeper = TestUtils.setFieldValueWithMock(wrapper, "sleeper", Sleeper.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfMessageResponderIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("MessageResponder can not be null");

        new AsyncMessageResponderWrapper(null, controller);
    }

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new AsyncMessageResponderWrapper(messageResponder, null);
    }

    @Test
    public void messageArrivedShouldPassThroughAndNeverAskOrWaitIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.messageArrived(100, "msg", 200);

        verify(messageResponder).messageArrived(100, "msg", 200);
        verifyZeroInteractions(executorService);
        verify(wrapper, never()).askUserToIdentify(anyInt());
        verify(wrapper, never()).waitForUserToIdentify(anyInt());
    }

    @Test
    public void messageArrivedShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.messageArrived(100, "msg", 200);

        verify(wrapper).askUserToIdentify(100);
    }

    @Test
    public void messageArrivedShouldWaitForUserToIdentifyAndPassThroughUsingExecutorIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.messageArrived(100, "msg", 200);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        verifyZeroInteractions(messageResponder);
        verify(wrapper, never()).waitForUserToIdentify(anyInt());

        verify(executorService).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(wrapper).waitForUserToIdentify(100);
        verify(messageResponder).messageArrived(100, "msg", 200);
    }

    @Test
    public void topicChangedShouldPassThroughIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.topicChanged(100, "newTopic", "nick", 300);

        verify(wrapper, never()).askUserToIdentify(anyInt());
        verify(messageResponder).topicChanged(100, "newTopic", "nick", 300);
    }

    @Test
    public void topicChangedShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.topicChanged(100, "newTopic", "nick", 300);

        verify(wrapper).askUserToIdentify(100);
        verify(messageResponder, never()).topicChanged(anyInt(), anyString(), anyString(), anyLong());
    }

    @Test
    public void topicRequestedShouldPassThrough() {
        wrapper.topicRequested();

        verify(messageResponder).topicRequested();
    }

    @Test
    public void awayChangedShouldPassThroughIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.awayChanged(100, true, "awayMsg");

        verify(wrapper, never()).askUserToIdentify(anyInt());
        verify(messageResponder).awayChanged(100, true, "awayMsg");
    }

    @Test
    public void awayChangedShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.awayChanged(100, true, "awayMsg");

        verify(wrapper).askUserToIdentify(100);
        verify(messageResponder, never()).awayChanged(anyInt(), anyBoolean(), anyString());
    }

    @Test
    public void nickChangedShouldPassThroughIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.nickChanged(100, "newNick");

        verify(wrapper, never()).askUserToIdentify(anyInt());
        verify(messageResponder).nickChanged(100, "newNick");
    }

    @Test
    public void nickChangedShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.nickChanged(100, "newNick");

        verify(wrapper).askUserToIdentify(100);
        verify(messageResponder, never()).nickChanged(anyInt(), anyString());
    }

    @Test
    public void nickCrashShouldPassThrough() {
        wrapper.nickCrash();

        verify(messageResponder).nickCrash();
    }

    @Test
    public void meLogOnShouldPassThrough() {
        wrapper.meLogOn("ipAddress");

        verify(messageResponder).meLogOn("ipAddress");
    }

    @Test
    public void userLogOnShouldPassThrough() {
        final User user = new User("User", 123);

        wrapper.userLogOn(user);

        verify(messageResponder).userLogOn(user);
    }

    @Test
    public void userLogOffShouldPassThrough() {
        wrapper.userLogOff(100);

        verify(messageResponder).userLogOff(100);
    }

    @Test
    public void userExposingShouldPassThrough() {
        final User user = new User("User", 123);

        wrapper.userExposing(user);

        verify(messageResponder).userExposing(user);
    }

    @Test
    public void exposeRequestedShouldPassThrough() {
        wrapper.exposeRequested();

        verify(messageResponder).exposeRequested();
    }

    @Test
    public void writingChangedShouldPassThrough() {
        wrapper.writingChanged(100, true);

        verify(messageResponder).writingChanged(100, true);
    }

    @Test
    public void meIdleShouldPassThrough() {
        wrapper.meIdle("ipAddress");

        verify(messageResponder).meIdle("ipAddress");
    }

    @Test
    public void userIdleShouldPassThroughIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.userIdle(100, "ipAddress");

        verify(wrapper, never()).askUserToIdentify(anyInt());
        verify(messageResponder).userIdle(100, "ipAddress");
    }

    @Test
    public void userIdleShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.userIdle(100, "ipAddress");

        verify(wrapper).askUserToIdentify(100);
        verify(messageResponder, never()).userIdle(anyInt(), anyString());
    }

    @Test
    public void fileSendShouldAskUserToIdentifyIfNewUser() {
        when(controller.isNewUser(100)).thenReturn(true);

        wrapper.fileSend(100, 3000, "fileName", "user", 98765);

        verify(wrapper).askUserToIdentify(100);
    }

    @Test
    public void fileSendShouldNotAskUserToIdentifyIfExistingUser() {
        when(controller.isNewUser(100)).thenReturn(false);

        wrapper.fileSend(100, 3000, "fileName", "user", 98765);

        verify(wrapper, never()).askUserToIdentify(anyInt());
    }

    @Test
    public void fileSendShouldWaitForUserToIdentifyAndPassThroughUsingExecutor() {
        wrapper.fileSend(100, 3000, "fileName", "user", 98765);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        verifyZeroInteractions(messageResponder);
        verify(wrapper, never()).waitForUserToIdentify(anyInt());

        verify(executorService).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(wrapper).waitForUserToIdentify(100);
        verify(messageResponder).fileSend(100, 3000, "fileName", "user", 98765);
    }

    @Test
    public void fileSendAbortedShouldPassThrough() {
        wrapper.fileSendAborted(100, "fileName", 98765);

        verify(messageResponder).fileSendAborted(100, "fileName", 98765);
    }

    @Test
    public void fileSendAcceptedShouldPassThroughUsingExecutor() {
        wrapper.fileSendAccepted(100, "fileName", 98765, 1050);

        final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        verifyZeroInteractions(messageResponder);

        verify(executorService).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(messageResponder).fileSendAccepted(100, "fileName", 98765, 1050);
    }

    @Test
    public void clientInfoShouldPassThrough() {
        wrapper.clientInfo(100, "client", 70000, "os",
                           4500, 6000);

        verify(messageResponder).clientInfo(100, "client", 70000, "os",
                                            4500, 6000);
    }

    @Test
    public void askUserToIdentifyShouldAddWaitingUserAndExposeAndGetTopic() {
        wrapper.askUserToIdentify(100);

        verify(waitingList).addWaitingUser(100);
        verify(controller).sendExposeMessage();
        verify(controller).sendGetTopicMessage();
    }

    @Test
    public void waitForUserToIdentifyShouldAbortAfter40SleepsIfUserNeverIdentifies() {
        when(waitingList.isWaitingUser(100)).thenReturn(true);

        wrapper.waitForUserToIdentify(100);

        verify(sleeper, times(40)).sleep(50);
        verify(waitingList, times(41)).isWaitingUser(100);
    }

    @Test
    public void waitForUserToIdentifyShouldAbortWhenUserHasIdentified() {
        when(waitingList.isWaitingUser(100)).thenReturn(true, true, true, false);

        wrapper.waitForUserToIdentify(100);

        verify(sleeper, times(3)).sleep(50);
        verify(waitingList, times(4)).isWaitingUser(100);
    }

    @Test
    public void waitForUserToIdentifyShouldNeverSleepIfUserIsIdentified() {
        when(waitingList.isWaitingUser(100)).thenReturn(false);

        wrapper.waitForUserToIdentify(100);

        verifyZeroInteractions(sleeper);
        verify(waitingList).isWaitingUser(100);
    }
}
