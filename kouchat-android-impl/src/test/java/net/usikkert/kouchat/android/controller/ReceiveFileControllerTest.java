
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

import java.util.List;

import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;

import android.content.ServiceConnection;

/**
 * Test of {@link ReceiveFileController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class ReceiveFileControllerTest {

    private ReceiveFileController controller;

    @Before
    public void setUp() {
        controller = new ReceiveFileController();
    }

    @Test
    public void onCreateShouldCreateServiceConnectionAndBindChatService() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "serviceConnection"));

        controller.onCreate(null);

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());

        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
        assertFalse(TestUtils.fieldValueIsNull(controller, "serviceConnection"));
    }

    @Test
    public void onDestroyShouldResetAndUnbindServiceConnection() {
        final ServiceConnection serviceConnection =
                TestUtils.setFieldValueWithMock(controller, "serviceConnection", ServiceConnection.class);

        controller.onDestroy();

        final List<ServiceConnection> unboundServiceConnections =
                Robolectric.getShadowApplication().getUnboundServiceConnections();

        assertEquals(1, unboundServiceConnections.size());
        assertSame(serviceConnection, unboundServiceConnections.get(0));
        assertTrue(TestUtils.fieldValueIsNull(controller, "serviceConnection"));
    }
}
