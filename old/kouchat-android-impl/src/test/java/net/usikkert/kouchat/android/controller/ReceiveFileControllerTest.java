
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

package net.usikkert.kouchat.android.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.ReceiveFileDialog;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.content.ServiceConnection;

/**
 * Test of {@link ReceiveFileController}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ReceiveFileControllerTest {

    private ActivityController<ReceiveFileController> activityController;
    private Intent intent;

    private ReceiveFileController controller;

    private AndroidUserInterface ui;
    private FileReceiver fileReceiver;
    private ReceiveFileDialog receiveFileDialog;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(ReceiveFileController.class);
        controller = activityController.get();
        receiveFileDialog = TestUtils.setFieldValueWithMock(controller, "receiveFileDialog", ReceiveFileDialog.class);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        ui = mock(AndroidUserInterface.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);

        fileReceiver = mock(FileReceiver.class);
        when(fileReceiver.getUser()).thenReturn(new User("Sandra", 23456));

        when(ui.getFileReceiver(1234, 5678)).thenReturn(fileReceiver);

        intent = new Intent(Robolectric.application, ReceiveFileController.class);
        intent.putExtra("userCode", 1234);
        intent.putExtra("fileTransferId", 5678);
    }

    @Test
    public void onCreateShouldCreateServiceConnectionAndBindChatService() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "serviceConnection"));

        activityController.create();

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());

        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
        assertFalse(TestUtils.fieldValueIsNull(controller, "serviceConnection"));
    }

    @Test
    public void onDestroyShouldUnbindServiceConnectionAndResetAllFields() {
        activityController.create();
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        activityController.destroy();

        final List<ServiceConnection> unboundServiceConnections =
                Robolectric.getShadowApplication().getUnboundServiceConnections();

        assertEquals(1, unboundServiceConnections.size());
        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void boundServiceShouldGetFileReceiverUsingIntentAndShowReceiveFileDialog() {
        activityController.withIntent(intent).create();

        verify(ui).getFileReceiver(1234, 5678);
        verify(receiveFileDialog).showReceiveFileDialog(controller, fileReceiver);
    }

    @Test
    public void boundServiceShouldHandleMissingIntentAndShowMissingFileDialog() {
        activityController.create();

        verify(ui).getFileReceiver(-1, -1);
        verify(receiveFileDialog).showMissingFileDialog(controller);
    }
}
