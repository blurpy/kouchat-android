
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.usikkert.kouchat.android.R;

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
public class SettingsController extends AppCompatActivity {

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
}
