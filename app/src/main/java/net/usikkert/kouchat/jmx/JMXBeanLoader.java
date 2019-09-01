
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

import java.util.Arrays;
import java.util.List;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.net.ConnectionWorker;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * Class for getting instances of JMX MBeans.
 *
 * <p>The following beans are registered:</p>
 *
 * <ul>
 *   <li>{@link NetworkInformation}</li>
 *   <li>{@link ControllerInformation}</li>
 *   <li>{@link GeneralInformation}</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class JMXBeanLoader {

    private final List<JMXBean> jmxBeans;

    /**
     * Initializes the bean loader, and the JMX beans.
     *
     * @param controller The controller.
     * @param connectionWorker The connection worker.
     * @param settings The settings.
     * @param errorHandler The error handler to use.
     */
    public JMXBeanLoader(final Controller controller, final ConnectionWorker connectionWorker,
                         final Settings settings, final ErrorHandler errorHandler) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(connectionWorker, "ConnectionWorker can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        jmxBeans = Arrays.asList(
                new NetworkInformation(connectionWorker, settings, errorHandler),
                new ControllerInformation(controller),
                new GeneralInformation(settings));
    }

    public List<JMXBean> getJMXBeans() {
        return jmxBeans;
    }
}
