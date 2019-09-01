
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Test of {@link Logger}.
 *
 * @author Christian Ihle
 */
public class LoggerTest {

    private Logger logger;

    private java.util.logging.Logger julLogger;
    private ArgumentCaptor<LogRecord> logCaptor;

    @Before
    public void setUp() {
        logger = Logger.getLogger(LoggerTest.class);

        julLogger = TestUtils.setFieldValueWithMock(logger, "logger", java.util.logging.Logger.class);
        logCaptor = ArgumentCaptor.forClass(LogRecord.class);
    }

    @Test
    public void severeShouldLogMessageWithLevelSevere() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("message");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals(Level.SEVERE, logRecord.getLevel());
        assertEquals("message", logRecord.getMessage());
        assertNull(logRecord.getThrown());
    }

    @Test
    public void severeShouldReplaceParametersInMessage() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("message with %s cookies and %s", 2, "milk");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("message with 2 cookies and milk", logRecord.getMessage());
    }

    @Test
    public void severeShouldHandleNullAsMessage() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe(null);

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertNull(logRecord.getMessage());
    }

    @Test
    public void severeShouldHandleNullAsParameter() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("Message with %s and %s :)", null, null);

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("Message with null and null :)", logRecord.getMessage());
    }

    @Test
    public void severeShouldLogMessageWithCorrectClassAndMethod() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);

        logger.severe("message");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("net.usikkert.kouchat.util.LoggerTest", logRecord.getSourceClassName());
        assertEquals("severeShouldLogMessageWithCorrectClassAndMethod", logRecord.getSourceMethodName());
    }

    @Test
    public void severeShouldNotLogIfLogLevelDisabled() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(false);

        logger.severe("message");

        verify(julLogger, never()).log(any(LogRecord.class));
    }

    @Test
    public void severeWithExceptionShouldLogMessageAndExceptionWithLevelSevere() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);
        final RuntimeException exception = new RuntimeException();

        logger.severe(exception, "message");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals(Level.SEVERE, logRecord.getLevel());
        assertEquals("message", logRecord.getMessage());
        assertEquals(exception, logRecord.getThrown());
    }

    @Test
    public void severeWithExceptionShouldReplaceParametersInMessage() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);
        final RuntimeException exception = new RuntimeException();

        logger.severe(exception, "message with %s cookies and %s", 2, "milk");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("message with 2 cookies and milk", logRecord.getMessage());
    }

    @Test
    public void severeWithExceptionShouldLogMessageWithCorrectClassAndMethod() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(true);
        final RuntimeException exception = new RuntimeException();

        logger.severe(exception, "message");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("net.usikkert.kouchat.util.LoggerTest", logRecord.getSourceClassName());
        assertEquals("severeWithExceptionShouldLogMessageWithCorrectClassAndMethod", logRecord.getSourceMethodName());
    }

    @Test
    public void severeWithExceptionShouldNotLogIfLogLevelDisabled() {
        when(julLogger.isLoggable(Level.SEVERE)).thenReturn(false);

        logger.severe(new RuntimeException(), "message");

        verify(julLogger, never()).log(any(LogRecord.class));
    }

    @Test
    public void warningShouldLogMessageWithLevelWarning() {
        when(julLogger.isLoggable(Level.WARNING)).thenReturn(true);

        logger.warning("message to %s", "you");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("message to you", logRecord.getMessage());
        assertEquals(Level.WARNING, logRecord.getLevel());
    }

    @Test
    public void infoShouldLogMessageWithLevelInfo() {
        when(julLogger.isLoggable(Level.INFO)).thenReturn(true);

        logger.info("message to %s", "you");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("message to you", logRecord.getMessage());
        assertEquals(Level.INFO, logRecord.getLevel());
    }

    @Test
    public void fineShouldLogMessageWithLevelFine() {
        when(julLogger.isLoggable(Level.FINE)).thenReturn(true);

        logger.fine("message to %s", "you");

        verify(julLogger).log(logCaptor.capture());

        final LogRecord logRecord = logCaptor.getValue();
        assertEquals("message to you", logRecord.getMessage());
        assertEquals(Level.FINE, logRecord.getLevel());
    }
}
