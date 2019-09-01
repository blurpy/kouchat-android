
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

package net.usikkert.kouchat.android.manual;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

/**
 * Test that sends a file transfer request with slow transfer, to better
 * see how the notification responds.
 *
 * @author Christian Ihle
 */
public class ManualReceiveFileNotificationTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient kenny;
    private static AndroidFile image;

    private Solo solo;
    private User me;

    public ManualReceiveFileNotificationTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController mainChatController = getActivity();
        final Instrumentation instrumentation = getInstrumentation();

        solo = new Solo(instrumentation, mainChatController);
        me = RobotiumTestUtils.getMe(mainChatController);

        if (kenny == null) {
            kenny = new TestClient("Kenny", 1239);
            kenny.logon();

            // Make sure we have an image to send from a test client to the real client
            FileUtils.copyKouChatImageFromAssetsToInternalStorage(instrumentation, mainChatController);
            image = FileUtils.getKouChatImageFromInternalStorage(mainChatController);
        }

        solo.sleep(500);
    }

    public void test01CheckNotificationManually() {
        kenny.setFileTransferDelay(200);

        kenny.sendFile(me, image.getFile());

        solo.sleep(25000);
    }

    public void test99Quit() {
        kenny.logoff();

        kenny = null;
        image = null;

        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        me = null;
        setActivity(null);

        System.gc();
    }
}
