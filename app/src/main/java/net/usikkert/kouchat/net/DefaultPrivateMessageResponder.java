
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Validate;

/**
 * This class responds to events from the message parser.
 *
 * @author Christian Ihle
 */
public class DefaultPrivateMessageResponder implements PrivateMessageResponder {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DefaultPrivateMessageResponder.class.getName());

    /** The controller for lower layers. */
    private final Controller controller;

    /** The user interface to notify. */
    private final UserInterface ui;

    /** The controller for showing messages in the user interface. */
    private final MessageController msgController;

    /** The application user. */
    private final User me;

    /**
     * Constructor.
     *
     * @param controller The controller.
     * @param ui The user interface.
     * @param settings The settings to use.
     */
    public DefaultPrivateMessageResponder(final Controller controller, final UserInterface ui,
                                          final Settings settings) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(ui, "UserInterface can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.controller = controller;
        this.ui = ui;

        me = settings.getMe();
        msgController = ui.getMessageController();
    }

    /**
     * Shows the message in the user's private chat window,
     * and notifies the user interface that a new message
     * has arrived.
     *
     * @param userCode The unique code of the user who sent the message.
     * @param msg The message.
     * @param color The color the message has.
     */
    @Override
    public void messageArrived(final int userCode, final String msg, final int color) {
        if (!controller.isNewUser(userCode)) {
            final User user = controller.getUser(userCode);

            if (me.isAway()) {
                LOG.log(Level.WARNING, "Got message from " + user.getNick() + " while away: " + msg);
            } else if (user.isAway()) {
                LOG.log(Level.WARNING, "Got message from " + user.getNick() + " which is away: " + msg);
            } else if (user.getPrivateChatPort() == 0) {
                LOG.log(Level.WARNING, "Got message from " + user.getNick() + " which has no reply port: " + msg);
            } else {
                msgController.showPrivateUserMessage(user, msg, color);

                // Not visible, or not in front
                if (!user.getPrivchat().isVisible() || !user.getPrivchat().isFocused()) {
                    controller.changeNewMessage(user.getCode(), true);
                }

                ui.notifyPrivateMessageArrived(user, msg);
            }
        }

        else {
            LOG.log(Level.SEVERE, "Could not find user: " + userCode);
        }
    }
}
