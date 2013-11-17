
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

package net.usikkert.kouchat.android;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.service.LockHandler;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.testclient.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests the multicast and wake locks.
 *
 * @author Christian Ihle
 */
public class LockTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;
    private ServiceConnection serviceConnection;
    private LockHandler lockHandler;

    public LockTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        bindChatService(activity);
    }

    public void test01DisableWakeLockAndQuit() {
        RobotiumTestUtils.openSettings(solo);

        assertTrue(solo.searchText("Enable wake lock"));

        if (solo.isCheckBoxChecked(0)) {
            solo.clickOnCheckBox(0);
        }

        assertFalse(solo.isCheckBoxChecked(0));

        RobotiumTestUtils.quit(solo);
    }

    public void test02MulticastLockShouldBeEnabledAndWakeLockDisabledByDefault() {
        assertTrue(lockHandler.multicastLockIsHeld());
        assertFalse(lockHandler.wakeLockIsHeld());
    }

    // TODO

    public void test99Quit() {
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        getActivity().unbindService(serviceConnection);

        solo.finishOpenedActivities();

        solo = null;
        lockHandler = null;
        serviceConnection = null;
        setActivity(null);

        System.gc();
    }

    private void bindChatService(final MainChatController activity) {
        final Intent chatServiceIntent = new Intent(activity, ChatService.class);
        serviceConnection = createServiceConnection();

        activity.bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
        solo.sleep(500); // To let the bind complete
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder iBinder) {
                final ChatServiceBinder binder = (ChatServiceBinder) iBinder;
                lockHandler = getLockHandler(binder.getAndroidUserInterface());
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) { }
        };
    }

    private LockHandler getLockHandler(final AndroidUserInterface androidUserInterface) {
        final ChatService chatService = TestUtils.getFieldValue(androidUserInterface, ChatService.class, "context");
        return TestUtils.getFieldValue(chatService, LockHandler.class, "lockHandler");
    }
}
