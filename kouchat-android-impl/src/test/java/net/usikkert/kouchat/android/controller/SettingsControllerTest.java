
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.content.TestSharedPreferences;

import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.ServiceConnection;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;

/**
 * Test of {@link SettingsController}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class SettingsControllerTest {

    private SettingsController controller;
    private SettingsController controllerSpy;

    private AndroidUserInterface ui;
    private Settings settings;
    private ServiceConnection serviceConnection;

    private EditTextPreference nickNamePreference;
    private CheckBoxPreference wakeLockPreference;

    @Before
    public void setUp() {
        controller = new SettingsController();

        TestUtils.setFieldValue(controller, "nickNameKey", "nick_name");
        TestUtils.setFieldValue(controller, "wakeLockKey", "wake_lock");

        controllerSpy = spy(controller);

        ui = mock(AndroidUserInterface.class);
        settings = mock(Settings.class);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        serviceConnection = mock(ServiceConnection.class);

        nickNamePreference = mock(EditTextPreference.class);
        when(nickNamePreference.getKey()).thenReturn("nick_name");
        doReturn(nickNamePreference).when(controllerSpy).findPreference("nick_name");

        wakeLockPreference = mock(CheckBoxPreference.class);
        when(wakeLockPreference.getKey()).thenReturn("wake_lock");
        doReturn(wakeLockPreference).when(controllerSpy).findPreference("wake_lock");
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onCreateShouldBindChatServiceToSetAndroidUserInterface() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onCreate(null); // findPreference() returns null, giving NullPointerException

        assertSame(ui, TestUtils.getFieldValue(controller, AndroidUserInterface.class, "androidUserInterface"));

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());
        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onResumeShouldSetControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));

        controller.onResume(); // getPreferenceScreen() returns null, giving NullPointerException

        assertTrue(sharedPreferences.hasListener(controller));
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onPauseShouldRemoveControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));
        sharedPreferences.registerOnSharedPreferenceChangeListener(controller);
        assertTrue(sharedPreferences.hasListener(controller));

        controller.onPause(); // getPreferenceScreen() returns null, giving NullPointerException

        assertFalse(sharedPreferences.hasListener(controller));
    }

    @Test
    public void onDestroyShouldUnregister() {
        setupMocks();

        controller.onDestroy();

        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        setupMocks();
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        controller.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        assertTrue(TestUtils.fieldValueIsNull(controller, "androidUserInterface"));

        controller.onDestroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onPreferenceChangeShouldAskAndroidUserInterfaceToChangeNickNameAndReturnTheResultIfFalse() {
        setupMocks();
        when(ui.changeNickName(anyString())).thenReturn(false);

        final boolean change = controller.onPreferenceChange(nickNamePreference, "Kelly");

        assertFalse(change);
        verify(ui).changeNickName("Kelly");
    }

    @Test
    public void onPreferenceChangeShouldAskAndroidUserInterfaceToChangeNickNameAndReturnTheResultIfTrue() {
        setupMocks();
        when(ui.changeNickName(anyString())).thenReturn(true);

        final boolean change = controller.onPreferenceChange(nickNamePreference, "Holly");

        assertTrue(change);
        verify(ui).changeNickName("Holly");
    }

    @Test
    public void onPreferenceChangeShouldDoNothingAndReturnTrueIfAnotherPreference() {
        setupMocks();

        final boolean change = controller.onPreferenceChange(wakeLockPreference, "on");

        assertTrue(change);
        verifyZeroInteractions(ui);
    }

    @Test
    public void onSharedPreferenceChangedShouldSetNickNameAsSummaryIfTextIsNotNull() {
        when(nickNamePreference.getText()).thenReturn("This is the value");

        controllerSpy.onSharedPreferenceChanged(null, "nick_name");

        verify(controllerSpy).findPreference("nick_name");
        verify(nickNamePreference).setSummary("This is the value");
    }

    @Test
    public void onSharedPreferenceChangedShouldNotSetNickNameAsSummaryIfTextIsNull() {
        when(nickNamePreference.getText()).thenReturn(null);

        controllerSpy.onSharedPreferenceChanged(null, "nick_name");

        verify(controllerSpy).findPreference("nick_name");
        verify(nickNamePreference, never()).setSummary(anyString());
    }

    @Test
    public void onSharedPreferenceChangedShouldMakeToastAndSaveSettingOnWakeLockChange() {
        setupMocks();
        when(wakeLockPreference.isChecked()).thenReturn(true);

        controllerSpy.onSharedPreferenceChanged(null, "wake_lock");

        verify(controllerSpy).findPreference("wake_lock");
        verify(settings).setWakeLockEnabled(true);
        assertEquals("Enable wake lock: true", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void onOptionsItemSelectedShouldGoBackToMainChatWhenClickOnUpInActionBar() {
        final MenuItem up = new ActionMenuItem(Robolectric.application, 0, android.R.id.home, 0, 0, "Up");

        controller.onOptionsItemSelected(up);

        final Intent nextStartedActivity = Robolectric.getShadowApplication().getNextStartedActivity();
        assertNotNull(nextStartedActivity);

        final ShadowIntent nextStartedActivityShadow = Robolectric.shadowOf(nextStartedActivity);
        assertEquals(MainChatController.class, nextStartedActivityShadow.getIntentClass());
    }

    @Test
    public void onOptionsItemSelectedShouldDoNothingOnOtherClicks() {
        final MenuItem up = new ActionMenuItem(Robolectric.application, 0, 0, 0, 0, "Up");

        controller.onOptionsItemSelected(up);

        final Intent nextStartedActivity = Robolectric.getShadowApplication().getNextStartedActivity();
        assertNull(nextStartedActivity);
    }

    private void setupMocks() {
        TestUtils.setFieldValue(controller, "androidUserInterface", ui);
        TestUtils.setFieldValue(controllerSpy, "androidUserInterface", ui);

        TestUtils.setFieldValue(controller, "settings", settings);
        TestUtils.setFieldValue(controllerSpy, "settings", settings);

        TestUtils.setFieldValue(controller, "serviceConnection", serviceConnection);
        TestUtils.setFieldValue(controllerSpy, "serviceConnection", serviceConnection);
    }

    private TestSharedPreferences getTestSharedPreferences() {
        return (TestSharedPreferences) ShadowPreferenceManager
                .getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
    }
}
