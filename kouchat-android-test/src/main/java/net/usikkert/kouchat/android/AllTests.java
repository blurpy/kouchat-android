
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
 ***************************************************************************/

package net.usikkert.kouchat.android;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs all the tests in the correct order.
 *
 * @author Christian Ihle
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        final TestSuite testSuite = new TestSuite("All Android tests");

        testSuite.addTestSuite(TopicTest.class); // Must run first
        testSuite.addTestSuite(AboutDialogTest.class);
        testSuite.addTestSuite(LifecycleTest.class);
        testSuite.addTestSuite(MainChatTest.class);
        testSuite.addTestSuite(OrientationTest.class);
        testSuite.addTestSuite(SettingsTest.class);
        testSuite.addTestSuite(UserListTest.class);
//        testSuite.addTestSuite(PrivateChatTest.class);
//        testSuite.addTestSuite(FileTransferTest.class);

        return testSuite;
    }
}
