
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a mock implementation of {@link MessageResponder}, that just stores the messages in a map.
 *
 * @author Christian Ihle
 */
public class MessageResponderMock implements MessageResponder {

    private static final String MESSAGE_ARRIVED = "messageArrived";

    private final User me;
    private final Map<String, List<Object[]>> messages;

    public MessageResponderMock(final User me) {
        Validate.notNull(me, "User me can not be null");

        this.me = me;
        messages = new HashMap<String, List<Object[]>>();
    }

    public boolean gotMessageArrived(final String message) {
        final List<Object[]> messagesArrived = getMessages(MESSAGE_ARRIVED);

        for (final Object[] messageArrived : messagesArrived) {
            if (messageArrived[1].equals(message)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void messageArrived(final int userCode, final String msg, final int color) {
        addMessage(MESSAGE_ARRIVED, new Object[] {userCode, msg, color});
    }

    @Override
    public void topicChanged(final int userCode, final String newTopic, final String nick, final long time) {

    }

    @Override
    public void topicRequested() {

    }

    @Override
    public void awayChanged(final int userCode, final boolean away, final String awayMsg) {

    }

    @Override
    public void nickChanged(final int userCode, final String newNick) {

    }

    @Override
    public void nickCrash() {

    }

    @Override
    public void meLogOn(final String ipAddress) {
        me.setIpAddress(ipAddress);
    }

    @Override
    public void userLogOn(final User newUser) {

    }

    @Override
    public void userLogOff(final int userCode) {

    }

    @Override
    public void userExposing(final User user) {

    }

    @Override
    public void exposeRequested() {

    }

    @Override
    public void writingChanged(final int userCode, final boolean writing) {

    }

    @Override
    public void meIdle(final String ipAddress) {

    }

    @Override
    public void userIdle(final int userCode, final String ipAddress) {

    }

    @Override
    public void fileSend(final int userCode, final long byteSize, final String fileName, final String user,
                         final int fileHash) {

    }

    @Override
    public void fileSendAborted(final int userCode, final String fileName, final int fileHash) {
    }

    @Override
    public void fileSendAccepted(final int userCode, final String fileName, final int fileHash, final int port) {

    }

    @Override
    public void clientInfo(final int userCode, final String client, final long timeSinceLogon,
                           final String operatingSystem, final int privateChatPort) {

    }

    private void addMessage(final String key, final Object[] value) {
        if (!messages.containsKey(key)) {
            messages.put(key, new ArrayList<Object[]>());
        }

        final List<Object[]> values = messages.get(key);
        values.add(value);
    }

    private List<Object[]> getMessages(final String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        } else {
            return Collections.emptyList();
        }
    }
}
