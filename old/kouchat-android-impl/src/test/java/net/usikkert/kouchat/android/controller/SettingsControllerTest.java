
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

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.HoloColorPickerPreference;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.settings.AndroidSettings;
import net.usikkert.kouchat.android.util.RobolectricTestUtils;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.tester.android.content.TestSharedPreferences;
import org.robolectric.util.ActivityController;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;

/**
 * Test of {@link SettingsController}.
 *
 * @author Christian Ihle
 */
@Config(reportSdk = 10)
@RunWith(RobolectricTestRunner.class)
public class SettingsControllerTest {

    private SettingsController controller;
    private ActivityController<SettingsController> activityController;

    private AndroidUserInterface ui;
    private AndroidSettings settings;

    private EditTextPreference nickNamePreference;
    private CheckBoxPreference wakeLockPreference;
    private HoloColorPickerPreference ownColorPreference;
    private HoloColorPickerPreference systemColorPreference;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(SettingsController.class);
        controller = activityController.get();

        ui = mock(AndroidUserInterface.class);
        settings = mock(AndroidSettings.class);

        when(ui.getSettings()).thenReturn(settings);

        final ChatServiceBinder serviceBinder = mock(ChatServiceBinder.class);
        when(serviceBinder.getAndroidUserInterface()).thenReturn(ui);
        Robolectric.getShadowApplication().setComponentNameAndServiceForBindService(null, serviceBinder);

        activityController.create();

        nickNamePreference = (EditTextPreference) controller.findPreference("nick_name");
        wakeLockPreference = (CheckBoxPreference) controller.findPreference("wake_lock");
        ownColorPreference = (HoloColorPickerPreference) controller.findPreference("own_color");
        systemColorPreference = (HoloColorPickerPreference) controller.findPreference("sys_color");
    }

    @Test
    public void onCreateShouldBindChatServiceToSetAndroidUserInterface() {
        assertSame(ui, TestUtils.getFieldValue(controller, AndroidUserInterface.class, "androidUserInterface"));

        final ShadowIntent startedServiceIntent =
                Robolectric.shadowOf(Robolectric.getShadowApplication().getNextStartedService());
        assertEquals(ChatService.class, startedServiceIntent.getIntentClass());
    }

    @Test
    public void onCreateShouldSetControllerAsOnPreferenceChangeListenerOnNickName() {
        final Preference.OnPreferenceChangeListener listener = nickNamePreference.getOnPreferenceChangeListener();

        assertSame(controller, listener);
    }

    @Test
    public void onCreateShouldKeepDefaultSummaryInNickNameIfNickNameIsNotSet() {
        assertEquals("Set your own nick name, so others can identify you in the chat.", nickNamePreference.getSummary());
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void onCreateShouldSetNickNameAsSummaryIfNickNameIsSet() {
        // The loaded preferences do not check persisted values anywhere. Should be able to do something like this:
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("SuperKou");
        activityController.create();

        assertEquals("SuperKou", nickNamePreference.getSummary());
    }

    @Test
    public void onCreateShouldEnableUpInActionBar() {
        final ActionBar actionBar = controller.getSupportActionBar();
        final int displayOptions = actionBar.getDisplayOptions();

        assertTrue((displayOptions & ActionBar.DISPLAY_HOME_AS_UP) != 0);
    }

    @Test
    public void onResumeShouldSetControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));

        activityController.resume();

        assertTrue(sharedPreferences.hasListener(controller));
    }

    @Test
    public void onPauseShouldRemoveControllerAsListener() {
        final TestSharedPreferences sharedPreferences = getTestSharedPreferences();

        assertFalse(sharedPreferences.hasListener(controller));
        sharedPreferences.registerOnSharedPreferenceChangeListener(controller);
        assertTrue(sharedPreferences.hasListener(controller));

        activityController.pause();

        assertFalse(sharedPreferences.hasListener(controller));
    }

    @Test
    public void onDestroyShouldUnregister() {
        activityController.destroy();

        assertEquals(1, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        assertTrue(TestUtils.allFieldsHaveValue(controller));

        activityController.destroy();

        assertTrue(TestUtils.allFieldsAreNull(controller));
    }

    @Test
    public void onDestroyShouldNotFailIfServiceHasNotBeenBound() {
        TestUtils.setFieldValue(controller, "androidUserInterface", null);

        activityController.destroy();

        assertEquals(0, Robolectric.getShadowApplication().getUnboundServiceConnections().size());
    }

    @Test
    public void onPreferenceChangeShouldAskAndroidUserInterfaceToChangeNickNameAndReturnTheResultIfFalse() {
        when(ui.changeNickName(anyString())).thenReturn(false);

        final boolean change = controller.onPreferenceChange(nickNamePreference, "Kelly");

        assertFalse(change);
        verify(ui).changeNickName("Kelly");
    }

    @Test
    public void onPreferenceChangeShouldAskAndroidUserInterfaceToChangeNickNameAndReturnTheResultIfTrue() {
        when(ui.changeNickName(anyString())).thenReturn(true);

        final boolean change = controller.onPreferenceChange(nickNamePreference, "Holly");

        assertTrue(change);
        verify(ui).changeNickName("Holly");
    }

    @Test
    public void onPreferenceChangeShouldDoNothingAndReturnTrueIfAnotherPreference() {
        reset(ui); // onServiceConnected()

        final boolean change = controller.onPreferenceChange(wakeLockPreference, "on");

        assertTrue(change);
        verifyZeroInteractions(ui);
    }

    @Test
    public void onSharedPreferenceChangedShouldSetNickNameAsSummaryIfTextIsNotNull() {
        nickNamePreference.setText("SuperKou");
        nickNamePreference.setSummary("Existing summary");

        controller.onSharedPreferenceChanged(null, "nick_name");

        assertEquals("SuperKou", nickNamePreference.getSummary());
        verifyZeroInteractions(settings);
    }

    @Test
    public void onSharedPreferenceChangedShouldNotSetNickNameAsSummaryIfTextIsNull() {
        nickNamePreference.setText(null);
        nickNamePreference.setSummary("Existing summary");

        controller.onSharedPreferenceChanged(null, "nick_name");

        assertEquals("Existing summary", nickNamePreference.getSummary());
        verifyZeroInteractions(settings);
    }

    @Test
    public void onSharedPreferenceChangedShouldSaveSettingOnWakeLockChange() {
        wakeLockPreference.setChecked(true);

        controller.onSharedPreferenceChanged(null, "wake_lock");

        verify(settings).setWakeLockEnabled(true);
        verifyNoMoreInteractions(settings);
    }

    @Test
    public void onSharedPreferenceChangedShouldSaveSettingOnOwnColorChange() {
        TestUtils.setFieldValue(ownColorPreference, "persistedColor", 12345);

        controller.onSharedPreferenceChanged(null, "own_color");

        verify(settings).setOwnColor(12345);
        verifyNoMoreInteractions(settings);
    }

    @Test
    public void onSharedPreferenceChangedShouldSaveSettingOnSystemColorChange() {
        TestUtils.setFieldValue(systemColorPreference, "persistedColor", 54321);

        controller.onSharedPreferenceChanged(null, "sys_color");

        verify(settings).setSysColor(54321);
        verifyNoMoreInteractions(settings);
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

    private TestSharedPreferences getTestSharedPreferences() {
        return (TestSharedPreferences) ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application);
    }
}
