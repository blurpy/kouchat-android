
/***************************************************************************
 *   Copyright 2006-2011 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
 ***************************************************************************/

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.service.ChatService;
import net.usikkert.kouchat.android.service.ChatServiceBinder;
import net.usikkert.kouchat.util.Tools;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Controller for changing the settings.
 *
 * @author Christian Ihle
 */
public class SettingsController extends PreferenceActivity
                                implements Preference.OnPreferenceChangeListener,
                                           SharedPreferences.OnSharedPreferenceChangeListener {

    private AndroidUserInterface androidUserInterface;
    private ServiceConnection serviceConnection;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        final String nickNameKey = getString(R.string.settings_key_nick_name);
        final Preference nickNamePreference = findPreference(nickNameKey);

        nickNamePreference.setOnPreferenceChangeListener(this);
        setValueAsSummary(nickNamePreference);

        serviceConnection = createServiceConnection();
        final Intent chatServiceIntent = createChatServiceIntent();
        bindService(chatServiceIntent, serviceConnection, BIND_NOT_FOREGROUND);
    }

    /**
     * Handles validation when a setting is about to be changed.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceChange(final Preference preference, final Object value) {
        if (Tools.isValidNick(value.toString())) {
            return true;
        }

        Toast.makeText(SettingsController.this, getString(R.string.error_nick_name_invalid), Toast.LENGTH_LONG).show();

        return false;
    }

    /**
     * Updates the summary of the setting after it's been changed.
     *
     * {@inheritDoc}
     */
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        setValueAsSummary(key);
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
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private void setValueAsSummary(final String key) {
        final Preference preference = findPreference(key);
        setValueAsSummary(preference);
    }

    /**
     * Sets the current value of a setting as the summary, so it's visible without clicking
     * on the setting to change it.
     *
     * @param preference The setting to update.
     */
    private void setValueAsSummary(final Preference preference) {
        final EditTextPreference editTextPreference = (EditTextPreference) preference;
        preference.setSummary(editTextPreference.getText());
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
            }

            @Override
            public void onServiceDisconnected(final ComponentName componentName) {

            }
        };
    }
}
