
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.component.HoloColorPickerPreference;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.misc.Settings;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;

/**
 * Controller for changing the settings.
 *
 * <p>Supports the following settings:</p>
 *
 * <ul>
 *   <li>The nick name of the user.</li>
 *   <li>The color of your own messages.</li>
 *   <li>The color of system messages.</li>
 *   <li>To use a wake lock or not.</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class SettingsController extends SherlockPreferenceActivity
                                implements Preference.OnPreferenceChangeListener,
                                           SharedPreferences.OnSharedPreferenceChangeListener {

    private AndroidUserInterface androidUserInterface;
    private Settings settings;

    private ServiceConnection serviceConnection;

    private String nickNameKey;
    private String wakeLockKey;
    private String ownColorKey;
    private String systemColorKey;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        nickNameKey = getString(R.string.settings_key_nick_name);
        wakeLockKey = getString(R.string.settings_wake_lock_key);
        ownColorKey = getString(R.string.settings_own_color_key);
        systemColorKey = getString(R.string.settings_sys_color_key);

        final Preference nickNamePreference = findPreference(nickNameKey);
        nickNamePreference.setOnPreferenceChangeListener(this);
        setValueAsSummary(nickNamePreference);

        serviceConnection = createServiceConnection();
        final Intent chatServiceIntent = createChatServiceIntent();
        bindService(chatServiceIntent, serviceConnection, BIND_NOT_FOREGROUND);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
     *   <li>Changed wake lock: stores the setting in the {@link Settings}.</li>
     *   <li>Changed own color: stores the setting in the {@link Settings}.</li>
     *   <li>Changed system color: stores the setting in the {@link Settings}.</li>
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
            final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(key);
            settings.setWakeLockEnabled(checkBoxPreference.isChecked());
        }

        else if (key.equals(ownColorKey)) {
            final HoloColorPickerPreference preference = (HoloColorPickerPreference) findPreference(key);
            settings.setOwnColor(preference.getPersistedColor());
        }

        else if (key.equals(systemColorKey)) {
            final HoloColorPickerPreference preference = (HoloColorPickerPreference) findPreference(key);
            settings.setSysColor(preference.getPersistedColor());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (androidUserInterface != null) {
            unbindService(serviceConnection);
        }

        androidUserInterface = null;
        settings = null;
        serviceConnection = null;
        nickNameKey = null;
        wakeLockKey = null;
        ownColorKey = null;
        systemColorKey = null;

        super.onDestroy();
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Clicked on KouChat icon in the action bar
                return goBackToMainChat();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean goBackToMainChat() {
        startActivity(new Intent(this, MainChatController.class));
        return true;
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
        return new Intent(this, ChatService.class);
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
