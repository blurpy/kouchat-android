
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

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper around {@link java.util.logging.Logger} to provide more convenient methods for logging,
 * including message parameters.
 *
 * @author Christian Ihle
 */
public final class Logger {

    private static final int CALLING_METHOD_INDEX = 2; // Index in the stack trace to find calling method

    private final java.util.logging.Logger logger;

    public static Logger getLogger(final Class<?> clazz) {
        return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
    }

    private Logger(final java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void fine(@NonNls final String message,
                     @NonNls final Object... messageParameters) {
        log(Level.FINE, message, messageParameters, null);
    }

    public void info(@NonNls final String message,
                     @NonNls final Object... messageParameters) {
        log(Level.INFO, message, messageParameters, null);
    }

    public void warning(@NonNls final String message,
                        @NonNls final Object... messageParameters) {
        log(Level.WARNING, message, messageParameters, null);
    }

    public void severe(@NonNls final String message,
                       @NonNls final Object... messageParameters) {
        log(Level.SEVERE, message, messageParameters, null);
    }

    public void severe(final Throwable throwable,
                       @NonNls final String message,
                       @NonNls final Object... messageParameters) {
        log(Level.SEVERE, message, messageParameters, throwable);
    }

    /**
     * It's necessary to create a {@link LogRecord} manually, otherwise all log output will
     * have this class and method as "caller". The log manager is allowed to use optimized private APIs,
     * so this method might be slower, but logging is usually for warnings and debugging,
     * so it should not have an impact on performance during normal usage.
     */
    private void log(final Level level, final String message, final Object[] messageParameters,
                     final Throwable throwable) {
        if (logger.isLoggable(level)) {
            final String formattedMessage = getFormattedMessageOrNull(message, messageParameters);
            final LogRecord logRecord = new LogRecord(level, formattedMessage);

            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            final StackTraceElement stackTraceElement = stackTrace[CALLING_METHOD_INDEX];

            logRecord.setSourceMethodName(stackTraceElement.getMethodName());
            logRecord.setSourceClassName(stackTraceElement.getClassName());

            logRecord.setThrown(throwable);

            logger.log(logRecord);
        }
    }

    @Nullable
    private String getFormattedMessageOrNull(final String message, final Object[] messageParameters) {
        if (message == null) {
            return null;
        }

        return String.format(message, messageParameters);
    }
}
