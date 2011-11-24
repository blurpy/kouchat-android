
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

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.util.Tools;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Controller for changing the settings.
 *
 * @author Christian Ihle
 */
public class SettingsController extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        final Preference nickName = getPreference(R.string.settings_key_nick_name);
        nickName.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object value) {
        if (Tools.isValidNick(value.toString())) {
            return true;
        }

        Toast.makeText(SettingsController.this, getString(R.string.error_invalid_nick), Toast.LENGTH_LONG).show();

        return false;
    }

    private Preference getPreference(final int resourceId) {
        return getPreferenceScreen().findPreference(getString(resourceId));
    }
}
