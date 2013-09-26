
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.net.ConnectionWorker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link JMXBeanLoader}.
 *
 * @author Christian Ihle
 */
public class JMXBeanLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Controller can not be null");

        new JMXBeanLoader(null, mock(ConnectionWorker.class), mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfConnectionWorkerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ConnectionWorker can not be null");

        new JMXBeanLoader(mock(Controller.class), null, mock(Settings.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new JMXBeanLoader(mock(Controller.class), mock(ConnectionWorker.class), null);
    }

    @Test
    public void getJMXBeansShouldIncludeThreeBeans() {
        final JMXBeanLoader beanLoader =
                new JMXBeanLoader(mock(Controller.class), mock(ConnectionWorker.class), mock(Settings.class));

        final List<JMXBean> jmxBeans = beanLoader.getJMXBeans();
        assertNotNull(jmxBeans);

        assertEquals(3, jmxBeans.size());
        assertTrue(containsBeanOfType(jmxBeans, NetworkInformation.class));
        assertTrue(containsBeanOfType(jmxBeans, ControllerInformation.class));
        assertTrue(containsBeanOfType(jmxBeans, GeneralInformation.class));
    }

    private boolean containsBeanOfType(final List<JMXBean> jmxBeans, final Class<?> theClass) {
        for (JMXBean jmxBean : jmxBeans) {
            if (jmxBean.getClass().equals(theClass)) {
                return true;
            }
        }

        return false;
    }
}
