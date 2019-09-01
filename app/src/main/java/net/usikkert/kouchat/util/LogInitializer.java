
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class initializes log settings for the application.
 *
 * <p>The default level for output to the console is <code>INFO</code>,
 * but any level can be set using the <code>java.util.logging</code> JMX MBean.</p>
 *
 * @author Christian Ihle
 */
public class LogInitializer {

    /** The main package where all the classes belonging to the application are. */
    private static final String MAIN_PACKAGE = "net.usikkert.kouchat";

    /**
     * Constructor that initializes the logging.
     *
     * @param debug If verbose debug logging should be enabled.
     */
    public LogInitializer(final boolean debug) {
        initHandlers();
        initParentLoggers();

        if (debug) {
            activateDebug();
        }
    }

    /**
     * Creates loggers for important packages, to make
     * it easier to change settings for a group of loggers.
     */
    public void initParentLoggers() {
        Logger.getLogger(MAIN_PACKAGE);
        Logger.getLogger(MAIN_PACKAGE + ".misc");
        Logger.getLogger(MAIN_PACKAGE + ".net");
        Logger.getLogger(MAIN_PACKAGE + ".net.tcp");
        Logger.getLogger(MAIN_PACKAGE + ".ui");
        Logger.getLogger(MAIN_PACKAGE + ".ui.console");
        Logger.getLogger(MAIN_PACKAGE + ".ui.swing");
        Logger.getLogger(MAIN_PACKAGE + ".util");
    }

    /**
     * Enable logging of all levels with the console handler.
     *
     * <br /><br />
     *
     * This is important, because the console handler level is
     * set to <code>INFO</code> by default, which overrides the normal
     * logger level. So changing the logger level with the JMX MBean has no effect
     * without this change.
     */
    public void initHandlers() {
        final Handler[] handlers = Logger.getLogger("").getHandlers();

        for (final Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.ALL);
                break;
            }
        }
    }

    /**
     * Activates logging of all messages in all the loggers.
     */
    public void activateDebug() {
        final Logger mainLogger = Logger.getLogger(MAIN_PACKAGE);
        mainLogger.setLevel(Level.ALL);
    }
}
