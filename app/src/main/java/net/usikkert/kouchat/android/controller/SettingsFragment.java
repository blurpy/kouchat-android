
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.TwoStatePreference;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.HoloColorPickerPreference;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.android.settings.AndroidSettings;

/**
 * Fragment for changing the settings.
 *
 * @author Christian Ihle
 */
public class SettingsFragment extends PreferenceFragment
                              implements Preference.OnPreferenceChangeListener,
                                         SharedPreferences.OnSharedPreferenceChangeListener {

    private AndroidUserInterface androidUserInterface;
    private AndroidSettings settings;

    private ServiceConnection serviceConnection;

    private String nickNameKey;
    private String wakeLockKey;
    private String ownColorKey;
    private String systemColorKey;

    private String notificationLightKey;
    private String notificationSoundKey;
    private String notificationVibrationKey;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        nickNameKey = getString(R.string.settings_nick_name_key);
        wakeLockKey = getString(R.string.settings_wake_lock_key);
        ownColorKey = getString(R.string.settings_own_color_key);
        systemColorKey = getString(R.string.settings_sys_color_key);

        notificationLightKey = getString(R.string.settings_notification_light_key);
        notificationSoundKey = getString(R.string.settings_notification_sound_key);
        notificationVibrationKey = getString(R.string.settings_notification_vibration_key);

        final Preference nickNamePreference = findPreference(nickNameKey);
        nickNamePreference.setOnPreferenceChangeListener(this);
        setValueAsSummary(nickNamePreference);

        serviceConnection = createServiceConnection();
        final Intent chatServiceIntent = createChatServiceIntent();
        getActivity().bindService(chatServiceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND);
    }

    /**
     * Handles validation when the nick name is about to be changed, and changes the actual nick name if it's valid.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceChange(final Preference preference, final Object value) {
        if (preference.getKey().equals(nickNameKey)) {
            return androidUserInterface.changeNickName(value.toString());
        }

        return true;
    }

    /**
     * Updates state after a setting has been changed and saved.
     *
     * <ul>
     *   <li>Changed nick name: the nick name is set as the summary of the preference.</li>
     *   <li>Changed wake lock: stores the setting in the {@link AndroidSettings}.</li>
     *   <li>Changed own color: stores the setting in the {@link AndroidSettings}.</li>
     *   <li>Changed system color: stores the setting in the {@link AndroidSettings}.</li>
     *   <li>Changed notification light: stores the setting in the {@link AndroidSettings}.</li>
     *   <li>Changed notification sound: stores the setting in the {@link AndroidSettings}.</li>
     *   <li>Changed notification vibration: stores the setting in the {@link AndroidSettings}.</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (key.equals(nickNameKey)) {
            setValueAsSummary(key);
        }

        else if (key.equals(wakeLockKey)) {
            final TwoStatePreference preference = (TwoStatePreference) findPreference(key);
            settings.setWakeLockEnabled(preference.isChecked());
        }

        else if (key.equals(ownColorKey)) {
            final HoloColorPickerPreference preference = (HoloColorPickerPreference) findPreference(key);
            settings.setOwnColor(preference.getPersistedColor());
        }

        else if (key.equals(systemColorKey)) {
            final HoloColorPickerPreference preference = (HoloColorPickerPreference) findPreference(key);
            settings.setSysColor(preference.getPersistedColor());
        }

        else if (key.equals(notificationLightKey)) {
            final TwoStatePreference preference = (TwoStatePreference) findPreference(key);
            settings.setNotificationLightEnabled(preference.isChecked());
        }

        else if (key.equals(notificationSoundKey)) {
            final TwoStatePreference preference = (TwoStatePreference) findPreference(key);
            settings.setNotificationSoundEnabled(preference.isChecked());
        }

        else if (key.equals(notificationVibrationKey)) {
            final TwoStatePreference preference = (TwoStatePreference) findPreference(key);
            settings.setNotificationVibrationEnabled(preference.isChecked());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (androidUserInterface != null) {
            getActivity().unbindService(serviceConnection);
        }

        androidUserInterface = null;
        settings = null;
        serviceConnection = null;

        super.onDestroy();
    }

    private void setValueAsSummary(final String key) {
        final Preference preference = findPreference(key);
        setValueAsSummary(preference);
    }

    /**
     * Sets the current value of a setting as the summary, so it's visible without clicking
     * on the setting to change it. Unless it's not set, in which case the default summary is left untouched.
     *
     * @param preference The setting to update.
     */
    private void setValueAsSummary(final Preference preference) {
        final EditTextPreference editTextPreference = (EditTextPreference) preference;

        if (editTextPreference.getText() != null) {
            preference.setSummary(editTextPreference.getText());
        }
    }

    private Intent createChatServiceIntent() {
        return new Intent(getActivity(), ChatService.class);
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
                final ChatServiceBinder binder = (ChatServiceBinder) iBinder;
                androidUserInterface = binder.getAndroidUserInterface();
                settings = androidUserInterface.getSettings();
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) { }
        };
    }
}
