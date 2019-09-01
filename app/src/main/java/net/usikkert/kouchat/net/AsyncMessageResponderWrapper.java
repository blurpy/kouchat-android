
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.WaitingList;
import net.usikkert.kouchat.util.Sleeper;
import net.usikkert.kouchat.util.Validate;

/**
 * Wrapper around a real {@link MessageResponder} that handles operations that need to be async and
 * operations from unknown users.
 *
 * <p>As a rule, all operations are handled by a single thread, to keep the order they arrive.
 * Some operations need to wait for a response, and must therefore be handled by a new thread to
 * avoid locking other operations.</p>
 *
 * <p>Some operations handles users appearing unexpectedly, from a timeout, or because of packet loss.
 * Those will add the user to a waiting list, ask the user to identify, and then wait for it to happen,
 * before continuing.</p>
 *
 * @author Christian Ihle
 */
public class AsyncMessageResponderWrapper implements MessageResponder {

    private final Sleeper sleeper = new Sleeper();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final MessageResponder messageResponder;
    private final Controller controller;
    private final WaitingList waitingList;

    public AsyncMessageResponderWrapper(final MessageResponder messageResponder, final Controller controller) {
        Validate.notNull(messageResponder, "MessageResponder can not be null");
        Validate.notNull(controller, "Controller can not be null");

        this.messageResponder = messageResponder;
        this.controller = controller;
        this.waitingList = controller.getWaitingList();
    }

    /**
     * Shows a message. If the user that sent the message does not yet exist in the user list,
     * the user is asked to identify itself before the message is shown.
     */
    @Override
    public void messageArrived(final int userCode, final String msg, final int color) {
        // A little hack to stop messages from showing before the user is logged on
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    waitForUserToIdentify(userCode);
                    messageResponder.messageArrived(userCode, msg, color);
                }
            });
        }

        else {
            messageResponder.messageArrived(userCode, msg, color);
        }
    }

    /**
     * Topic changed. Asked to identify instead, if unknown.
     */
    @Override
    public void topicChanged(final int userCode, final String newTopic, final String nick, final long time) {
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);
        }

        else {
            messageResponder.topicChanged(userCode, newTopic, nick, time);
        }
    }

    @Override
    public void topicRequested() {
        messageResponder.topicRequested();
    }

    /**
     * User changed away state. Asked to identify instead, if unknown.
     */
    @Override
    public void awayChanged(final int userCode, final boolean away, final String awayMsg) {
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);
        }

        else {
            messageResponder.awayChanged(userCode, away, awayMsg);
        }
    }

    /**
     * User changed nick name. Asked to identify instead, if unknown.
     */
    @Override
    public void nickChanged(final int userCode, final String newNick) {
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);
        }

        else {
            messageResponder.nickChanged(userCode, newNick);
        }
    }

    @Override
    public void nickCrash() {
        messageResponder.nickCrash();
    }

    @Override
    public void meLogOn(final String ipAddress) {
        messageResponder.meLogOn(ipAddress);
    }

    @Override
    public void userLogOn(final User newUser) {
        messageResponder.userLogOn(newUser);
    }

    @Override
    public void userLogOff(final int userCode) {
        messageResponder.userLogOff(userCode);
    }

    @Override
    public void userExposing(final User user) {
        messageResponder.userExposing(user);
    }

    @Override
    public void exposeRequested() {
        messageResponder.exposeRequested();
    }

    @Override
    public void writingChanged(final int userCode, final boolean writing) {
        messageResponder.writingChanged(userCode, writing);
    }

    @Override
    public void meIdle(final String ipAddress) {
        messageResponder.meIdle(ipAddress);
    }

    /**
     * User reports to be idle. Asked to identify instead, if unknown.
     */
    @Override
    public void userIdle(final int userCode, final String ipAddress) {
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);
        }

        else {
            messageResponder.userIdle(userCode, ipAddress);
        }
    }

    /**
     * Receives a file transfer, which may take a long time. Needs to run in a different thread.
     * Handles unidentified users.
     */
    @Override
    public void fileSend(final int userCode, final long byteSize, final String fileName,
                         final String user, final int fileHash) {
        if (controller.isNewUser(userCode)) {
            askUserToIdentify(userCode);
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                waitForUserToIdentify(userCode);
                messageResponder.fileSend(userCode, byteSize, fileName, user, fileHash);
            }
        });
    }

    @Override
    public void fileSendAborted(final int userCode, final String fileName, final int fileHash) {
        messageResponder.fileSendAborted(userCode, fileName, fileHash);
    }

    /**
     * Does the actual file transfer to the other user, which may take a long time. Needs to run
     * in a different thread.
     */
    @Override
    public void fileSendAccepted(final int userCode, final String fileName, final int fileHash, final int port) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                messageResponder.fileSendAccepted(userCode, fileName, fileHash, port);
            }
        });
    }

    @Override
    public void clientInfo(final int userCode, final String client, final long timeSinceLogon,
                           final String operatingSystem, final int privateChatPort, final int tcpChatPort) {
        messageResponder.clientInfo(userCode, client, timeSinceLogon, operatingSystem, privateChatPort, tcpChatPort);
    }

    /**
     * Asks user with the specified userCode to identify with {@link #userExposing(User)}.
     * Adds user to waiting list so we know this user sent a message without being known,
     * and also so we can wait for this user to identify before continuing an operation.
     */
    void askUserToIdentify(final int userCode) {
        waitingList.addWaitingUser(userCode);
        controller.sendExposeMessage();
        controller.sendGetTopicMessage();
    }

    /**
     * Waits for user with the specified userCode to identify in {@link #userExposing(User)}.
     * Gives up after 2 seconds.
     */
    void waitForUserToIdentify(final int userCode) {
        int counter = 0;

        while (waitingList.isWaitingUser(userCode) && counter < 40) {
            counter++;
            sleeper.sleep(50);
        }
    }
}
