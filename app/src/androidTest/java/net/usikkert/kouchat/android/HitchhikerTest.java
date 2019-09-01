
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

import java.util.Calendar;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.ReceiveFileController;
import net.usikkert.kouchat.android.util.AndroidFile;
import net.usikkert.kouchat.android.util.FileUtils;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestClient;

import com.robotium.solo.Solo;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test that simulates a chat between characters in The Hitchhiker's Guide to the Galaxy.
 *
 * Does not really assert anything. Mostly used for making new screenshots.
 *
 * @author Christian Ihle
 */
public class HitchhikerTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient arthur;
    private static TestClient ford;
    private static TestClient trillian;

    private static String originalNickName;
    private static AndroidFile image;

    private Solo solo;
    private User me;

    public HitchhikerTest() {
        super(MainChatController.class);
    }

    @Override
    public void setUp() {
        // Making sure the test clients only logs on once during all the tests
        if (arthur == null) {
            arthur = new TestClient("Arthur", 0, -6750208);
            arthur.logon();

            ford = new TestClient("Ford", 0, -13534789);
            ford.setInitialTopic("DON'T PANIC", getDateInPast());
            ford.logon();

            trillian = new TestClient("Trillian", 0);
            trillian.logon();
        }

        final MainChatController activity = getActivity();
        final Instrumentation instrumentation = getInstrumentation();

        solo = new Solo(instrumentation, activity);
        me = RobotiumTestUtils.getMe(activity);

        if (originalNickName == null) {
            originalNickName = me.getNick();

            FileUtils.copyKouChatImageFromAssetsToSdCard(instrumentation, activity);
            image = FileUtils.getKouChatImageFromSdCardWithContentUri(activity);
        }
    }

    public void test01SetNickNameAndQuit() {
        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, "Christian");

        sleep(10000); // Take screenshot of the settings

        RobotiumTestUtils.goHome(solo);
        RobotiumTestUtils.quit(solo);
    }

    /**
     * In the main chat:
     *
     * Topic: DON'T PANIC
     *
     * <Christian>: hey :)
     * <Arthur>: What are you doing?
     * <Ford>: Preparing for hyperspace. It's rather unpleasantly like being drunk.
     * <Arthur>: What's so wrong about being drunk?
     * <Ford>: Ask a glass of water.
     * *** Trillian went away: It won't affect me, I'm already a woman.
     * <Christian>: interesting!
     */
    public void test02DoMainChat() throws CommandException {
        sleep(7000);
        RobotiumTestUtils.writeLine(solo, "hey :)");

        // Minimize the app

        sleep(10000);
        arthur.sendChatMessage("What are you doing?");

        sleep(16000);
        ford.sendChatMessage("Preparing for hyperspace. It's rather unpleasantly like being drunk.");

        sleep(9000);
        arthur.sendChatMessage("What's so wrong about being drunk?");

        sleep(7000);
        ford.sendChatMessage("Ask a glass of water.");

        // Take screenshot of the notification and click to open app again

        sleep(10000);
        trillian.goAway("It won't affect me, I'm already a woman.");

        sleep(6000);
        RobotiumTestUtils.writeLine(solo, "interesting!");

        sleep(1000);
        arthur.sendPrivateChatMessage("Show me the envelope!", me);

        // Take screenshot of the main chat
        sleep(20000);

        ford.sendFile(me, image.getFile());
        sleep(1000);
        openReceiveFileDialog(ford, 1);
        sleep(1000);
        solo.clickOnText("File transfer request"); // To remove the highlight on the default button

        // Take screenshot of the file transfer request
        sleep(15000);
        solo.clickOnText("Reject");

        // Take screenshot of the file transfer popup in the gallery
        sleep(45000);

        arthur.logoff();
        trillian.logoff();
    }

    /**
     * In the private chat:
     *
     * <Arthur>: Ford?
     * <Ford>: Yeah?
     * <Arthur>: I think I'm a sofa...
     * <Ford>: I know how you feel...
     */
    public void test03DoPrivateChat() {
        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, "Arthur");
        RobotiumTestUtils.goHome(solo);

        sleep(500);
        RobotiumTestUtils.openPrivateChat(solo, getInstrumentation(), 2, 2, "Ford");

        sleep(500);
        RobotiumTestUtils.writeLine(solo, "Ford?");

        sleep(9000);
        ford.sendPrivateChatMessage("Yeah?", me);

        sleep(12000);
        RobotiumTestUtils.writeLine(solo, "I think I'm a sofa...");

        sleep(13000);
        ford.sendPrivateChatMessage("I know how you feel...", me);

        // Take screenshot of the private chat
        sleep(10000);

        ford.logoff();
    }

    public void test04RestoreNickNameAndQuit() {
        // In case any of the tests here fail, log off to avoid the tests in the next classes from failing as well.
        arthur.logoff();
        ford.logoff();
        trillian.logoff();

        assertNotNull(originalNickName);

        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, originalNickName);

        RobotiumTestUtils.goHome(solo);
        RobotiumTestUtils.quit(solo);

        arthur = null;
        ford = null;
        trillian = null;

        originalNickName = null;
        image = null;
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        me = null;
        setActivity(null);

        System.gc();
    }

    private long getDateInPast() {
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calendar.add(Calendar.MINUTE, -15);
        calendar.add(Calendar.DATE, -12);

        return calendar.getTimeInMillis();
    }

    private void sleep(final int ms) {
//        solo.sleep(ms); // Screenshot mode
        solo.sleep(1500); // Test mode
    }

    private void openReceiveFileDialog(final TestClient client, final int fileTransferId) {
        final Intent intent = new Intent();
        intent.putExtra("userCode", client.getUserCode());
        intent.putExtra("fileTransferId", fileTransferId);

        final String packageName = getInstrumentation().getTargetContext().getPackageName();
        launchActivityWithIntent(packageName, ReceiveFileController.class, intent);
    }
}
