
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

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.util.TestClient;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

/**
 * Tests sending and receiving messages in the main chat.
 *
 * @author Christian Ihle
 */
public class MainChatTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    public MainChatTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testOwnMessageIsShownInChat() {
        solo.enterText(0, "This is a new message from myself");
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
        assertTrue(solo.searchText("This is a new message from myself"));
    }

    public void testOtherClientMessageIsShownInChat() throws CommandException {
        final TestClient client = new TestClient();
        final Messages messages = client.logon();

        messages.sendChatMessage("Hello, this is a message from someone else");
        assertTrue(solo.searchText("Hello, this is a message from someone else"));

        client.logoff();
    }

    public void tearDown() {
        solo.finishOpenedActivities();
    }
}
