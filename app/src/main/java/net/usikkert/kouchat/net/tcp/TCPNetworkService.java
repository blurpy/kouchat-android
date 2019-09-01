
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

package net.usikkert.kouchat.net.tcp;

import net.usikkert.kouchat.event.UserListListener;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * Network service for administration of tcp communication.
 *
 * @author Christian Ihle
 */
public class TCPNetworkService implements UserListListener {

    private final TCPConnectionHandler tcpConnectionHandler;
    private final TCPServer tcpServer;

    public TCPNetworkService(final Controller controller,
                             final Settings settings,
                             final ErrorHandler errorHandler) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.tcpConnectionHandler = new TCPConnectionHandler(controller, settings);
        this.tcpServer = new TCPServer(settings, errorHandler, tcpConnectionHandler);

        controller.getUserList().addUserListListener(this);
    }

    public void startService() {
        tcpServer.startServer();
        tcpConnectionHandler.connect();
    }

    public void stopService() {
        tcpServer.stopServer();
        tcpConnectionHandler.disconnect();
    }

    @Override
    public void userAdded(final int pos, final User user) {
        tcpConnectionHandler.userAdded(user);
    }

    @Override
    public void userChanged(final int pos, final User user) {

    }

    @Override
    public void userRemoved(final int pos, final User user) {
        tcpConnectionHandler.userRemoved(user);
    }

    public void sendMessageToAll(final String message) {
        tcpConnectionHandler.sendMessageToAll(message);
    }

    public void sendMessageToUser(final String message, final User user) {
        tcpConnectionHandler.sendMessageToUser(message, user);
    }

    public void registerReceiverListener(final TCPReceiverListener listener) {
        tcpConnectionHandler.registerReceiverListener(listener);
    }
}
