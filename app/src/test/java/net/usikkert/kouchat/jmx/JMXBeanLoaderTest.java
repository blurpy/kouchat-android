
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

package net.usikkert.kouchat.jmx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.net.ConnectionWorker;
import net.usikkert.kouchat.settings.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link JMXBeanLoader}.
 *
 * @author Christian Ihle
 */
public class JMXBeanLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Controller controller;
    private ConnectionWorker connectionWorker;
    private Settings settings;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        controller = mock(Controller.class);
        connectionWorker = mock(ConnectionWorker.class);
        settings = mock(Settings.class);
        errorHandler = mock(ErrorHandler.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new JMXBeanLoader(null, connectionWorker, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfConnectionWorkerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ConnectionWorker can not be null");

        new JMXBeanLoader(controller, null, settings, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new JMXBeanLoader(controller, connectionWorker, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new JMXBeanLoader(controller, connectionWorker, settings, null);
    }

    @Test
    public void getJMXBeansShouldIncludeThreeBeans() {
        final JMXBeanLoader beanLoader = new JMXBeanLoader(controller, connectionWorker, settings, errorHandler);

        final List<JMXBean> jmxBeans = beanLoader.getJMXBeans();
        assertNotNull(jmxBeans);

        assertEquals(3, jmxBeans.size());
        assertTrue(containsBeanOfType(jmxBeans, NetworkInformation.class));
        assertTrue(containsBeanOfType(jmxBeans, ControllerInformation.class));
        assertTrue(containsBeanOfType(jmxBeans, GeneralInformation.class));
    }

    private boolean containsBeanOfType(final List<JMXBean> jmxBeans, final Class<?> theClass) {
        for (final JMXBean jmxBean : jmxBeans) {
            if (jmxBean.getClass().equals(theClass)) {
                return true;
            }
        }

        return false;
    }
}
