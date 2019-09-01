
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

package net.usikkert.kouchat.testclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;

import org.apache.commons.io.IOUtils;

/**
 * Starts a telnet server at port 20000 for remote control of a test client using the console interface.
 *
 * @author Christian Ihle
 */
public class TestClientTelnetServer {

    private final TestClientUserInterface ui;
    private final Controller controller;
    private final CommandParser commandParser;
    private final MessageController messageController;

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private boolean processMessages;

    public TestClientTelnetServer(final TestClientUserInterface ui, final Controller controller, final CommandParser commandParser) {
        this.ui = ui;
        this.controller = controller;
        this.commandParser = commandParser;
        this.messageController = ui.getMessageController();

        startServer(); // This will block until the server is stopped
    }

    private void startServer() {
        processMessages = true;

        try {
            final ServerSocket serverSocket = new ServerSocket(20000);
            socket = serverSocket.accept();
            IOUtils.closeQuietly(serverSocket);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ui.setWriter(writer);

            processMessages();
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopServer() {
        processMessages = false;

        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(socket);
    }

    private void processMessages() {
        try {
            while (processMessages) {
                final String line = reader.readLine().trim();

                if (line.length() == 0) {
                    continue;
                }

                else if (line.startsWith("/quit")) {
                    stopServer();
                }

                else if (line.startsWith("/")) {
                    commandParser.parse(line);
                }

                else {
                    sendChatMessage(line);
                }
            }
        }

        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendChatMessage(final String message) {
        try {
            controller.sendChatMessage(message);
            messageController.showOwnMessage(message);
        }

        catch (final CommandException e) {
            throw new RuntimeException(e);
        }
    }
}
