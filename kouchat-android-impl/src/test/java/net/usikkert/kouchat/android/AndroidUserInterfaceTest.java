
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.notification.NotificationService;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import android.content.Context;

/**
 * Test of {@link AndroidUserInterface}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidUserInterfaceTest {

    private AndroidUserInterface androidUserInterface;

    private MainChatController mainChatController;
    private Controller controller;
    private NotificationService notificationService;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("Me", 1234));

        final Context context = Robolectric.application.getApplicationContext();
        notificationService = mock(NotificationService.class);

        androidUserInterface = new AndroidUserInterface(context, settings, notificationService);

        controller = mock(Controller.class);
        TestUtils.setFieldValue(androidUserInterface, "controller", controller);

        mainChatController = mock(MainChatController.class);
        androidUserInterface.registerMainChatController(mainChatController);
    }

    @Test
    public void showTopicShouldNotSetTitleOfMainChatIfMainChatIsMissing() {
        androidUserInterface.unregisterMainChatController();

        androidUserInterface.showTopic();

        verify(mainChatController, never()).updateTopic(anyString());
    }

    @Test
    public void showTopicShouldSetTitleOfMainChatToNickNameAndApplicationNameWhenNoTopicIsSet() {
        when(controller.getTopic()).thenReturn(new Topic());

        androidUserInterface.showTopic();

        verify(mainChatController).updateTopic("Me - KouChat");
    }

    @Test
    public void showTopicShouldSetTitleOfMainChatToNickNameAndTopicAndApplicationNameWhenATopicIsSet() {
        when(controller.getTopic()).thenReturn(new Topic("This rocks!", "OtherGuy", System.currentTimeMillis()));

        androidUserInterface.showTopic();

        verify(mainChatController).updateTopic("Me - Topic: This rocks! (OtherGuy) - KouChat");
    }

    @Test
    public void updateMeWritingShouldPassTrueToController() {
        androidUserInterface.updateMeWriting(true);

        verify(controller).updateMeWriting(true);
    }

    @Test
    public void updateMeWritingShouldPassFalseToController() {
        androidUserInterface.updateMeWriting(false);

        verify(controller).updateMeWriting(false);
    }

    @Test
    public void notifyMessageArrivedShouldAddNotificationIfMainChatNotVisible() {
        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyMessageArrived(null);

        verify(notificationService).notifyNewMessage();
    }

    @Test
    public void notifyMessageArrivedShouldNotAddNotificationIfMainChatVisible() {
        when(mainChatController.isVisible()).thenReturn(true);

        androidUserInterface.notifyMessageArrived(null);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldAddNotificationIfNotMainChatOrPrivateChatWithSpecifiedUserIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        assertFalse(privchat.isVisible());

        final User testUser = new User("TestUser", 1234);
        testUser.setPrivchat(privchat);

        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verify(notificationService).notifyNewMessage();
    }

    @Test
    public void notifyPrivateMessageArrivedShouldNotAddNotificationIfMainChatIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        assertFalse(privchat.isVisible());

        final User testUser = new User("TestUser", 1234);
        testUser.setPrivchat(privchat);

        when(mainChatController.isVisible()).thenReturn(true);

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void notifyPrivateMessageArrivedShouldNotAddNotificationIfPrivateChatWithSpecifiedUserIsVisible() {
        final AndroidPrivateChatWindow privchat = mock(AndroidPrivateChatWindow.class);
        when(privchat.isVisible()).thenReturn(true);

        final User testUser = new User("TestUser", 1234);
        testUser.setPrivchat(privchat);

        assertFalse(mainChatController.isVisible());

        androidUserInterface.notifyPrivateMessageArrived(testUser);

        verifyZeroInteractions(notificationService);
    }

    @Test
    public void unregisterMainChatControllerShouldSetControllerToNull() {
        final String fieldName = "mainChatController";
        final Class<MainChatController> fieldClass = MainChatController.class;

        assertNotNull(TestUtils.getFieldValue(androidUserInterface, fieldClass, fieldName));
        androidUserInterface.unregisterMainChatController();
        assertNull(TestUtils.getFieldValue(androidUserInterface, fieldClass, fieldName));
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfMainChatControllerIsNull() {
        androidUserInterface.unregisterMainChatController();

        assertFalse(androidUserInterface.isVisible());
        assertFalse(androidUserInterface.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeFalseIfMainChatControllerIsNotVisible() {
        assertFalse(mainChatController.isVisible());

        assertFalse(androidUserInterface.isVisible());
        assertFalse(androidUserInterface.isFocused());
    }

    @Test
    public void isVisibleAndIsFocusedShouldBeTrueIfMainChatControllerIsVisible() {
        when(mainChatController.isVisible()).thenReturn(true);

        assertTrue(androidUserInterface.isVisible());
        assertTrue(androidUserInterface.isFocused());
    }

    @Test
    public void resetAllNotificationsShouldUseTheNotificationService() {
        androidUserInterface.resetAllNotifications();

        verify(notificationService).resetAllNotifications();
    }
}
