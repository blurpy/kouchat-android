
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

import java.io.File;
import java.net.URL;
import java.util.Locale;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.filetransfer.AndroidFileUtils;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.SortedUserList;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Test of {@link SendFileController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class SendFileControllerTest {

    private SendFileController controller;
    private ActivityController<SendFileController> activityController;

    private AndroidUserInterface ui;
    private SortedUserList userList;
    private AndroidFileUtils androidFileUtils;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US); // To avoid issues with "." and "," in asserts containing file sizes

        activityController = Robolectric.buildActivity(SendFileController.class);
        controller = activityController.get();

        androidFileUtils = TestUtils.setFieldValueWithMock(controller, "androidFileUtils", AndroidFileUtils.class);
        ui = mock(AndroidUserInterface.class);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        final User me = new User("Me", 123);
        me.setMe(true);

        userList = new SortedUserList();
        userList.add(me);

        when(ui.getUserList()).thenReturn(userList);
        when(ui.getMe()).thenReturn(me);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
    }

    @Test
    public void onCreateShouldRegisterClickEventOnCancelButtonToFinishActivity() {
        activityController.create();
        assertFalse(controller.isFinishing());

        final Button cancelButton = (Button) controller.findViewById(R.id.sendFileCancelButton);

        cancelButton.performClick();
        assertTrue(controller.isFinishing());
    }

    @Test
    public void onCreateWithoutFileShouldHideInfoLine2AndNotStartService() {
        activityController.create();

        final TextView line1TextView = (TextView) controller.findViewById(R.id.sendFileLine1TextView);
        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);

        assertEquals("Unable to locate the file to send.", line1TextView.getText());
        assertEquals("", line2TextView.getText());
        assertEquals(View.GONE, line2TextView.getVisibility());

        assertNull(Robolectric.getShadowApplication().getNextStartedService());
    }

    @Test
    public void onCreateWithUnknownFileShouldSetPathInLine2AndNotStartService() {
        setupControllerWithUnknownFile();
        activityController.create();

        final TextView line1TextView = (TextView) controller.findViewById(R.id.sendFileLine1TextView);
        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);

        assertEquals("Unable to locate the file to send.", line1TextView.getText());
        assertEquals("ftp:google.com#search", line2TextView.getText());
        assertEquals(View.VISIBLE, line2TextView.getVisibility());

        assertNull(Robolectric.getShadowApplication().getNextStartedService());
    }

    @Test
    public void onCreateWithRecognizedFileAndNoUsersShouldSetFileDetailsAndNoUsersInfo() {
        setupControllerIntentWithValidFile();
        activityController.create();

        final TextView line1TextView = (TextView) controller.findViewById(R.id.sendFileLine1TextView);
        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);

        assertEquals("File name: kouchat-1600x1600.png \\nFile size: 67.16KB", line1TextView.getText());
        assertEquals("-- No connected users.", line2TextView.getText());
        assertEquals(View.VISIBLE, line2TextView.getVisibility());
    }

    @Test
    public void onCreateWithRecognizedFileAndUsersShouldSetFileDetailsAndSelectUserInfo() {
        userList.add(new User("SomeOne", 124));
        setupControllerIntentWithValidFile();
        activityController.create();

        final TextView line1TextView = (TextView) controller.findViewById(R.id.sendFileLine1TextView);
        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);

        assertEquals("File name: kouchat-1600x1600.png \\nFile size: 67.16KB", line1TextView.getText());
        assertEquals("Please select the user to send the file to.", line2TextView.getText());
        assertEquals(View.VISIBLE, line2TextView.getVisibility());
    }

    @Test
    public void onCreateWithRecognizedFileShouldStartService() {
        setupControllerIntentWithValidFile();
        activityController.create();

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());

        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    public void onCreateWithRecognizedFileShouldRegisterControllerAsUserListListener() {
        assertEquals(0, userList.getListeners().size());

        setupControllerIntentWithValidFile();
        activityController.create();

        assertEquals(1, userList.getListeners().size());
        assertTrue(userList.getListeners().contains(controller));
    }

    @Test
    public void onCreateWithRecognizedFileShouldRegisterOnClickListenerThatSendsTheFileAndFinishes() {
        userList.add(new User("One", 124));
        final User two = new User("Two", 125);
        userList.add(two);

        final File file = setupControllerIntentWithValidFile();
        activityController.create();

        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final AdapterView.OnItemClickListener listener = userListView.getOnItemClickListener();

        listener.onItemClick(userListView, null, 1, 100);
        verify(ui).sendFile(two, file);
        assertTrue(controller.isFinishing());
    }

    @Test
    public void onDestroyShouldUnregister() {
        userList.add(new User("SomeOne", 124));
        setupControllerIntentWithValidFile();
        activityController.create();

        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
        assertEquals(1, userList.getListeners().size());
        assertEquals(1, adapter.getCount());

        activityController.destroy();

        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
        assertEquals(0, userList.getListeners().size());
        assertEquals(-1, adapter.getCount()); // -1 because it's empty and expects "me" to be present.
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        setupControllerIntentWithValidFile();
        activityController.create();

        assertTrue(TestUtils.allFieldsHaveValue(controller));

        activityController.destroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        activityController.create();

        assertTrue(TestUtils.fieldValueIsNull(controller, "userList"));

        activityController.destroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void userAddedShouldAddUserToAdapterAndUpdateLine2() {
        setupControllerIntentWithValidFile();
        activityController.create();

        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);
        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals("-- No connected users.", line2TextView.getText());
        assertEquals(0, adapter.getCount());

        controller.userAdded(0, new User("Lilly", 125)); // Position is not used

        assertEquals("Please select the user to send the file to.", line2TextView.getText());
        assertEquals(1, adapter.getCount());
    }

    @Test
    public void userAddedShouldSortUsers() {
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(xing);
        userList.add(cecilia);

        setupControllerIntentWithValidFile();
        activityController.create();

        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals(2, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(xing, adapter.getItem(1));

        final User penny = new User("Penny", 126);

        controller.userAdded(0, penny);

        assertEquals(3, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(penny, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));
    }

    @Test
    public void userRemovedShouldRemoveUserFromAdapterAndUpdateLine2() {
        final User lilly = new User("Lilly", 125);
        userList.add(lilly);

        setupControllerIntentWithValidFile();
        activityController.create();

        final TextView line2TextView = (TextView) controller.findViewById(R.id.sendFileLine2TextView);
        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals("Please select the user to send the file to.", line2TextView.getText());
        assertEquals(1, adapter.getCount());

        controller.userRemoved(0, lilly); // Position is not used

        assertEquals("-- No connected users.", line2TextView.getText());
        assertEquals(0, adapter.getCount());
    }

    @Test
    public void userChangedShouldSortAdapter() {
        final User penny = new User("Penny", 126);
        final User xing = new User("Xing", 127);
        final User cecilia = new User("Cecilia", 128);

        userList.add(penny);
        userList.add(xing);
        userList.add(cecilia);

        setupControllerIntentWithValidFile();
        activityController.create();

        final ListView userListView = (ListView) controller.findViewById(R.id.sendFileUserListView);
        final ListAdapter adapter = userListView.getAdapter();

        assertEquals(3, adapter.getCount());
        assertSame(cecilia, adapter.getItem(0));
        assertSame(penny, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));

        penny.setNick("Amy");

        controller.userChanged(0, null); // Doesn't use any of the parameters

        assertEquals(3, adapter.getCount());
        assertSame(penny, adapter.getItem(0)); // Now Amy
        assertSame(cecilia, adapter.getItem(1));
        assertSame(xing, adapter.getItem(2));
    }

    private void setupControllerWithUnknownFile() {
        setupControllerWithIntent(Uri.fromParts("ftp", "google.com", "search"));
    }

    private File setupControllerIntentWithValidFile() {
        final Uri uri = Uri.parse("content://contacts/photos/253");
        final URL resource = getClass().getClassLoader().getResource("kouchat-1600x1600.png");
        assertNotNull("Unable to find kouchat-1600x1600.png", resource);

        final File file = new File(resource.getFile());
        when(androidFileUtils.getFileFromUri(uri, controller.getContentResolver())).thenReturn(file);

        setupControllerWithIntent(uri);

        return file;
    }

    private void setupControllerWithIntent(final Uri uri) {
        final Intent intent = new Intent(Robolectric.application, SendFileController.class);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        activityController.withIntent(intent);
    }
}
