
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

package net.usikkert.kouchat.android;

import java.util.ArrayList;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.SendFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.robotium.solo.Solo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;

/**
 * Testing away and back functionality.
 *
 * @author Christian Ihle
 */
public class AwayTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient client;
    private static AndroidFile image;

    private Solo solo;
    private User me;

    public AwayTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();

        RobotiumTestUtils.switchUserInterfaceToEnglish(activity); // To get the buttons in English

        solo = new Solo(getInstrumentation(), activity);
        me = RobotiumTestUtils.getMe(activity);

        if (client == null) {
            client = new TestClient();
            client.logon();
        }

        if (image == null) {
            FileUtils.copyKouChatImageFromAssetsToSdCard(getInstrumentation(), activity);
            image = FileUtils.getKouChatImageFromSdCardWithContentUri(activity);
        }
    }

    public void test01ShouldNotGoAwayOnCancel() {
        solo.sleep(500);
        checkIfBack();

        openGoAwayDialog("Not going away");
        solo.clickOnText("Cancel");

        solo.sleep(500);
        checkIfBack();
    }

    public void test02ShouldGoAwayOnOK() {
        solo.sleep(500);
        checkIfBack();

        openGoAwayDialog("Going for a walk");
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You went away: Going for a walk"));
        checkIfAway();
    }

    public void test03ShouldNotComeBackOnCancel() {
        solo.sleep(500);
        checkIfAway();

        openComeBackDialog("Going for a walk");
        solo.clickOnText("Cancel");

        solo.sleep(500);
        checkIfAway();
    }

    public void test04ShouldComeBackOnOK() {
        solo.sleep(500);
        checkIfAway();

        openComeBackDialog("Going for a walk");
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You came back"));
        checkIfBack();
    }

    public void test05ShouldNotGoAwayWithEmptyAwayMessage() {
        solo.sleep(500);
        checkIfBack();

        openGoAwayDialog(" ");
        solo.clickOnText("OK");

        solo.sleep(500);
        assertTrue(solo.searchText("You can not go away without an away message"));

        checkIfBack();
    }

    public void test06PressingEnterShouldNotAddANewLine() {
        solo.sleep(500);
        checkIfBack();

        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Go away?"));

        RobotiumTestUtils.writeText(getInstrumentation(), "Line1");
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
        RobotiumTestUtils.writeText(getInstrumentation(), "Line2");

        solo.sleep(500);
        assertTrue(solo.searchText("Line1Line2"));
    }

    public void test07ShouldNotBeAbleToSendMessageWhileAway() {
        solo.sleep(500);
        checkIfBack();

        openGoAwayDialog("Leaving");
        solo.clickOnText("OK");

        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.writeLine(solo, "Don't send this message");
        assertTrue(solo.searchText("You can not send a chat message while away"));
    }

    public void test08ShouldNotBeAbleToSendPrivateMessageWhileAway() {
        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Test");

        RobotiumTestUtils.writeLine(solo, "Don't send this private message");
        assertTrue(solo.searchText("You can not send a private chat message while away"));
    }

    public void test09ShouldNotBeAbleToChangeTopicWhileAway() {
        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.changeTopicTo(solo, getInstrumentation(), "Don't set this topic");

        solo.sleep(500);
        assertTrue(solo.searchText("You can not change the topic while away"));
    }

    public void test10ShouldNotBeAbleToChangeNickNameWhileAway() {
        solo.sleep(500);
        checkIfAway();

        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, "Dont");

        solo.sleep(500);
        assertTrue(solo.searchText("You can not change nick while away"));
    }

    public void test11ShouldNotBeAbleToSendFileWhileAway() {
        solo.sleep(500);
        checkIfAway();

        openSendFileDialog();

        // Using hack because the regular clickInList sometimes clicks the main chat user list
        final View sendFileUserListView = solo.getView(R.id.sendFileUserListView);
        final ArrayList<View> results = new ArrayList<>();
        sendFileUserListView.findViewsWithText(results, "Test", View.FIND_VIEWS_WITH_TEXT);
        solo.clickOnView(results.get(0)); // Click on Test

        solo.sleep(500);
        assertTrue(solo.searchText("You can not send a file while away"));
    }

    public void test99Quit() {
        client.logoff();
        RobotiumTestUtils.quit(solo);

        client = null;
        image = null;
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        me = null;
        setActivity(null);

        System.gc();
    }

    private void checkIfAway() {
        final ActionBar supportActionBar = getActivity().getSupportActionBar();

        assertTrue(me.isAway());
        assertEquals(me.getNick() + " (Away) - KouChat", supportActionBar.getTitle());
    }

    private void checkIfBack() {
        final ActionBar supportActionBar = getActivity().getSupportActionBar();

        assertFalse(me.isAway());
        assertEquals(me.getNick() + " - KouChat", supportActionBar.getTitle());
    }

    private void openGoAwayDialog(final String awayMessage) {
        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Go away?"));

        RobotiumTestUtils.writeText(getInstrumentation(), awayMessage);
        solo.sleep(500);
    }

    private void openComeBackDialog(final String awayMessage) {
        RobotiumTestUtils.openMenu(solo);
        solo.sleep(500);
        RobotiumTestUtils.clickMenuItem(solo, "Away");
        solo.sleep(500);

        assertTrue(solo.searchText("Come back from '" + awayMessage + "'?"));
    }

    private void openSendFileDialog() {
        final MainChatController activity = getActivity();
        final Intent intent = new Intent(activity, SendFileController.class);

        intent.putExtra(Intent.EXTRA_STREAM, image.getUri());
        activity.startActivity(intent);

        solo.sleep(500);
    }
}
