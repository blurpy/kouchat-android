
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

package net.usikkert.kouchat.android.chatwindow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Test of {@link AndroidPrivateChatWindow}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidPrivateChatWindowTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidPrivateChatWindow chatWindow;

    private User user;
    private MessageStylerWithHistory messageStyler;
    private PrivateChatController controller;

    @Before
    public void setUp() {
        user = new User("Vivi", 1234);
        chatWindow = new AndroidPrivateChatWindow(Robolectric.application, user);

        messageStyler = mock(MessageStylerWithHistory.class);
        TestUtils.setFieldValue(chatWindow, "messageStyler", messageStyler);

        controller = mock(PrivateChatController.class);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new AndroidPrivateChatWindow(null, user);
    }

    @Test
    public void constructorShouldThrowExceptionIfUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User can not be null");

        new AndroidPrivateChatWindow(Robolectric.application, null);
    }

    @Test
    public void getUserShouldReturnUserFromConstructor() {
        assertSame(user, chatWindow.getUser());
    }

    @Test
    public void registerPrivateChatControllerShouldThrowExceptionIfControllerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private chat controller can not be null");

        chatWindow.registerPrivateChatController(null);
    }

    @Test
    public void registerPrivateChatControllerShouldSetTheField() {
        chatWindow.registerPrivateChatController(controller);

        final PrivateChatController controllerFromChatWindow = getControllerFromChatWindow();

        assertSame(controller, controllerFromChatWindow);
    }

    @Test
    public void registerPrivateChatControllerShouldUpdateChatFromHistory() {
        when(messageStyler.getHistory()).thenReturn("History");

        chatWindow.registerPrivateChatController(controller);

        verify(controller).updatePrivateChat("History");
        verify(messageStyler).getHistory();
    }

    @Test
    public void unregisterPrivateChatControllerShouldSetControllerToNull() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        chatWindow.unregisterPrivateChatController();

        assertNull(getControllerFromChatWindow());
    }

    @Test
    public void appendToPrivateChatShouldThrowExceptionIfMessageIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private message can not be empty");

        chatWindow.appendToPrivateChat(null, 500);
    }

    @Test
    public void appendToPrivateChatShouldThrowExceptionIfMessageIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Private message can not be empty");

        chatWindow.appendToPrivateChat(" ", 500);
    }

    @Test
    public void appendToPrivateChatShouldAppendToHistoryIfControllerIsNull() {
        assertNull(getControllerFromChatWindow());

        chatWindow.appendToPrivateChat("Message", 500);

        verify(messageStyler).styleAndAppend("Message", 500);
        verifyZeroInteractions(controller);
    }

    @Test
    public void appendToPrivateChatShouldAppendToHistoryAndController() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);
        when(messageStyler.styleAndAppend(anyString(), anyInt())).thenReturn("Styled message");

        chatWindow.appendToPrivateChat("Message", 500);

        verify(messageStyler).styleAndAppend("Message", 500);
        verify(controller).appendToPrivateChat("Styled message");
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfControllerIsNull() {
        assertNull(getControllerFromChatWindow());

        assertFalse(chatWindow.isVisible());
        assertFalse(chatWindow.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfControllerIsNotVisible() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);
        assertFalse(controller.isVisible());

        assertFalse(chatWindow.isVisible());
        assertFalse(chatWindow.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeTrueIfControllerIsVisible() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);
        when(controller.isVisible()).thenReturn(true);

        assertTrue(chatWindow.isVisible());
        assertTrue(chatWindow.isFocused());
    }

    @Test
    public void updateAwayStateShouldHandleMissingController() {
        assertNull(getControllerFromChatWindow());

        chatWindow.updateAwayState();

        verifyZeroInteractions(controller);
    }

    @Test
    public void updateAwayStateShouldUpdateTitleAndSubtitle() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        chatWindow.updateAwayState();

        verify(controller).updateTitleAndSubtitle("Vivi - KouChat", null); // Tested in more detail in updateTitle() tests
    }

    @Test
    public void setLoggedOffShouldHandleMissingController() {
        assertNull(getControllerFromChatWindow());

        chatWindow.setLoggedOff();

        verifyZeroInteractions(controller);
    }

    @Test
    public void setLoggedOffShouldUpdateTitleAndSubtitle() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        chatWindow.setLoggedOff();

        verify(controller).updateTitleAndSubtitle("Vivi - KouChat", null);  // Tested in more detail in updateTitle() tests
    }

    @Test
    public void getChatTextShouldReturnEmptyString() {
        assertEquals("", chatWindow.getChatText());
    }

    @Test
    public void clearChatTextShouldDoNothing() {
        chatWindow.clearChatText();
        verifyZeroInteractions(controller);
    }

    @Test
    public void setVisibleShouldDoNothing() {
        chatWindow.setVisible(true);

        verifyZeroInteractions(controller);
        assertFalse(chatWindow.isVisible());
    }

    @Test
    public void updateUserInformationShouldDoNothing() {
        chatWindow.updateUserInformation();
        verifyZeroInteractions(controller);
    }

    @Test
    public void updateTitleShouldSetNickNameAndAppNameInTheTitleAndNullInTheSubtitle() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        user.setNick("Marge");

        chatWindow.updateTitle();

        verify(controller).updateTitleAndSubtitle("Marge - KouChat", null);
    }

    @Test
    public void updateTitleShouldIncludeOfflineInTheTitleAndNullInTheSubtitleIfUserIsOffline() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        user.setOnline(false);

        chatWindow.updateTitle();

        verify(controller).updateTitleAndSubtitle("Vivi (Offline) - KouChat", null);
    }

    @Test
    public void updateTitleShouldIncludeAwayInTheTitleAndAwayMessageInTheSubtitleIfUserIsAway() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        user.setAway(true);
        user.setAwayMsg("On the road again");

        chatWindow.updateTitle();

        verify(controller).updateTitleAndSubtitle("Vivi (Away) - KouChat", "On the road again");
    }

    @Test
    public void updateTitleShouldOnlyIncludeOfflineInTheTitleAndNullInTheSubtitleIfUserIsBothOfflineAndAway() {
        TestUtils.setFieldValue(chatWindow, "privateChatController", controller);

        user.setOnline(false);
        user.setAway(true);
        user.setAwayMsg("I left");

        chatWindow.updateTitle();

        verify(controller).updateTitleAndSubtitle("Vivi (Offline) - KouChat", null);
    }

    @Test
    public void updateTitleShouldHandleMissingController() {
        assertTrue(TestUtils.fieldValueIsNull(chatWindow, "privateChatController"));

        chatWindow.updateTitle();

        verifyZeroInteractions(controller);
    }

    private PrivateChatController getControllerFromChatWindow() {
        return TestUtils.getFieldValue(chatWindow, PrivateChatController.class, "privateChatController");
    }
}
