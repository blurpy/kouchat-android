
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Validate;

/**
 * This thread is responsible for sending a special "idle"
 * message every IDLE_TIME milliseconds to inform other clients
 * that this client is still online. It will also check if
 * other clients have stopped sending these messages,
 * and if that is the case, remove them and show a message
 * in the user interface.
 *
 * @author Christian Ihle
 */
public class IdleThread extends Thread {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(IdleThread.class.getName());

    /**
     * Number of milliseconds to wait before the next
     * idle message will be sent.
     */
    private static final int IDLE_TIME = 15000;

    /**
     * If an idle message has not been received from another
     * client in this number of milliseconds, then it's not
     * on the network anymore and must be removed.
     */
    private static final int TIMEOUT = 120000;

    private final Controller controller;
    private final UserList userList;
    private final User me;
    private final MessageController msgController;

    /** The thread runs while this is true. */
    private boolean run;

    /**
     * Constructor. Makes sure the thread is ready to start.
     *
     * @param controller The controller.
     * @param ui The user interface.
     * @param settings The settings to use.
     */
    public IdleThread(final Controller controller, final UserInterface ui, final Settings settings) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(ui, "User interface can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.controller = controller;

        userList = controller.getUserList();
        me = settings.getMe();
        msgController = ui.getMessageController();

        run = true;
        setName("IdleThread");
    }

    /**
     * This is where most of the action is.
     *
     * <li>Sends idle messages
     * <li>Restarts the network if there are problems
     * <li>Removes timed out clients
     */
    @Override
    public void run() {
        // In case of any error messages during startup
        me.setLastIdle(System.currentTimeMillis());

        while (run) {
            controller.sendIdleMessage();
            boolean timeout = false;

            for (int i = 0; i < userList.size(); i++) {
                final User temp = userList.get(i);

                if (temp.getCode() != me.getCode() && temp.getLastIdle() < System.currentTimeMillis() - TIMEOUT) {
                    userTimedOut(temp);
                    timeout = true;
                    i--;
                }
            }

            if (timeout) {
                controller.updateAfterTimeout();
            }

            try {
                sleep(IDLE_TIME);
            }

            // Sleep interrupted - probably from stopThread()
            catch (final InterruptedException e) {
                LOG.log(Level.FINE, e.toString());
            }
        }
    }

    /**
     * When a user times out, all current file transfers must
     * be canceled, and messages must be shown in the normal
     * chat window, and the private chat window.
     *
     * @param user The user which timed out.
     */
    private void userTimedOut(final User user) {
        final String timeOutMessage = user.getNick() + " timed out";

        controller.removeUser(user, timeOutMessage);
        msgController.showSystemMessage(timeOutMessage);
    }

    /**
     * Shuts down the thread in a controlled manner.
     */
    public void stopThread() {
        run = false;
        interrupt();
    }
}
