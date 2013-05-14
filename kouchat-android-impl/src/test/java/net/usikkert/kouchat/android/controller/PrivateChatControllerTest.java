
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.AndroidPrivateChatWindow;
import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Intent;

/**
 * Test of {@link PrivateChatController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class PrivateChatControllerTest {

    private PrivateChatController controller;

    @Before
    public void setUp() {
        controller = new PrivateChatController();

        final User user = new User("User", 1234);
        user.setPrivchat(mock(AndroidPrivateChatWindow.class));

        final AndroidUserInterface ui = mock(AndroidUserInterface.class);
        when(ui.getUser(1234)).thenReturn(user);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        final Intent intent = new Intent();
        intent.putExtra("userCode", 1234);
        controller.setIntent(intent);
    }

    @Test
    public void isVisibleShouldBeTrueOnlyBetweenOnResumeAndOnPause() {
        assertFalse(controller.isVisible());

        controller.onCreate(null);
        assertFalse(controller.isVisible());

        controller.onResume();
        assertTrue(controller.isVisible());

        controller.onPause();
        assertFalse(controller.isVisible());

        controller.onDestroy();
        assertFalse(controller.isVisible());
    }
}
