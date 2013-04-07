
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

package net.usikkert.kouchat.android;

import java.util.Calendar;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Test that simulates a chat between characters in The Hitchhiker's Guide to the Galaxy.
 *
 * Does not really assert anything. Mostly used for making new screenshots.
 *
 * @author Christian Ihle
 */
public class HitchhikerTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static TestClient arthurClient;
    private static Messages arthur;

    private static TestClient fordClient;
    private static Messages ford;

    private static TestClient trillianClient;
    private static Messages trillian;

    private static String originalNickName;

    private Solo solo;
    private User me;

    public HitchhikerTest() {
        super(MainChatController.class);
    }

    @Override
    public void setUp() {
        // Making sure the test clients only logs on once during all the tests
        if (arthurClient == null) {
            arthurClient = new TestClient("Arthur", 12345671, -6750208);
            arthur = arthurClient.logon();

            fordClient = new TestClient("Ford", 12345672, -10066432);
            ford = fordClient.logon();

            trillianClient = new TestClient("Trillian", 12345673);
            trillian = trillianClient.logon();

            arthur.sendTopicRequestedMessage(new Topic("DON'T PANIC", "Ford", getDateInPast()));
        }

        final MainChatController activity = getActivity();

        solo = new Solo(getInstrumentation(), activity);
        me = RobotiumTestUtils.getMe(activity);
    }

    public void test01SetNickNameAndQuit() {
        originalNickName = me.getNick();

        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, "Christian");

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
        solo.sleep(7000);
        RobotiumTestUtils.writeLine(solo, "hey :)");

        solo.sleep(10000);
        arthur.sendChatMessage("What are you doing?");

        solo.sleep(16000);
        ford.sendChatMessage("Preparing for hyperspace. It's rather unpleasantly like being drunk.");

        solo.sleep(9000);
        arthur.sendChatMessage("What's so wrong about being drunk?");

        solo.sleep(7000);
        ford.sendChatMessage("Ask a glass of water.");

        solo.sleep(8000);
        trillian.sendAwayMessage("It won't affect me, I'm already a woman.");

        solo.sleep(6000);
        RobotiumTestUtils.writeLine(solo, "interesting!");

        solo.sleep(1000);
        arthur.sendPrivateMessage("Show me the envelope!", me);

        // For taking screenshot manually. Robotium screenshots don't include status bars.
        solo.sleep(10000);

        arthurClient.logoff();
        trillianClient.logoff();
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

        solo.sleep(500);
        RobotiumTestUtils.openPrivateChat(solo, 2, 2, "Ford");

        solo.sleep(500);
        RobotiumTestUtils.writeLine(solo, "Ford?");

        solo.sleep(9000);
        fordClient.sendPrivateChatMessage("Yeah?", me);

        solo.sleep(12000);
        RobotiumTestUtils.writeLine(solo, "I think I'm a sofa...");

        solo.sleep(13000);
        fordClient.sendPrivateChatMessage("I know how you feel...", me);

        // For taking screenshot manually.
        solo.sleep(10000);

        fordClient.logoff();
    }

    public void test04RestoreNickNameAndQuit() {
        assertNotNull(originalNickName);

        RobotiumTestUtils.clickOnChangeNickNameInTheSettings(solo);
        RobotiumTestUtils.changeNickNameTo(solo, originalNickName);

        RobotiumTestUtils.goHome(solo);
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }

    private long getDateInPast() {
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calendar.add(Calendar.MINUTE, -15);
        calendar.add(Calendar.DATE, -12);

        return calendar.getTimeInMillis();
    }
}
