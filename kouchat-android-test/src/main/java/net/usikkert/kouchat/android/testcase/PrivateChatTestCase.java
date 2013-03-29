
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

package net.usikkert.kouchat.android.testcase;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.PrivateMessageResponderMock;
import net.usikkert.kouchat.util.TestClient;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Reusable test case for private chat tests.
 *
 * @author Christian Ihle
 */
public class PrivateChatTestCase extends ActivityInstrumentationTestCase2<MainChatController> {

    protected static TestClient client;
    protected static PrivateMessageResponderMock privateMessageResponder;
    protected static Messages messages;

    protected Solo solo;

    protected Bitmap envelope;
    protected Bitmap dot;

    protected User me;

    private int defaultOrientation;

    public PrivateChatTestCase() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        me = TestUtils.getMe(activity);
        envelope = getBitmap(R.drawable.envelope);
        dot = getBitmap(R.drawable.dot);
        defaultOrientation = TestUtils.getCurrentOrientation(solo);

        // Making sure the test client only logs on once during all the tests
        if (client == null) {
            client = new TestClient();
            privateMessageResponder = client.getPrivateMessageResponderMock();
            messages = client.logon();
        }

        privateMessageResponder.resetMessages();
    }

    public void test99Quit() {
        client.logoff();
        TestUtils.quit(solo);
    }

    public void tearDown() {
        TestUtils.setOrientation(solo, defaultOrientation);
        solo.finishOpenedActivities();
    }

    protected void openPrivateChat() {
        openPrivateChat(2, 2, "Test");
    }

    protected void openPrivateChat(final int numberOfUsers, final int userNumber, final String userName) {
        solo.sleep(500);
        assertEquals(numberOfUsers, solo.getCurrentListViews().get(0).getCount());
        solo.clickInList(userNumber);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have opened the private chat", PrivateChatController.class);
        // To be sure we are chatting with the right user
        assertEquals(userName + " - KouChat", solo.getCurrentActivity().getTitle());
    }

    protected Bitmap getBitmapForTestUser() {
        return getBitmapForUser(2, 2);
    }

    protected Bitmap getBitmapForUser(final int numberOfUsers, final int userNumber) {
        solo.sleep(1000);
        assertEquals(numberOfUsers, solo.getCurrentListViews().get(0).getCount());
        final LinearLayout row = (LinearLayout) solo.getCurrentListViews().get(0).getChildAt(userNumber - 1);
        final ImageView imageAtRow = (ImageView) row.getChildAt(0);
        final BitmapDrawable drawable = (BitmapDrawable) imageAtRow.getDrawable();

        return drawable.getBitmap();
    }

    private Bitmap getBitmap(final int resourceId) {
        final BitmapDrawable drawable = (BitmapDrawable) getActivity().getResources().getDrawable(resourceId);

        return drawable.getBitmap();
    }
}
