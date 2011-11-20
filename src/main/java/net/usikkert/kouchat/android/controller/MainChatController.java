
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
import net.usikkert.kouchat.android.service.ChatService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Controller for the main chat.
 *
 * @author Christian Ihle
 */
public class MainChatController extends Activity {

    public MainChatController() {
        System.out.println("MainChatController " + this + ": constructor !!!!!!!!!!!!!");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        System.out.println("MainChatController " + this + ": onCreate !!!!!!!!!!!!!");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_chat);
        startService(createChatServiceIntent());
    }

    @Override
    protected void onDestroy() {
        System.out.println("MainChatController " + this + ": onDestroy !!!!!!!!!!!!!");

        super.onDestroy();

        stopService(createChatServiceIntent());
    }

    /**
     *  Creates the main chat menu.
     *
     *  {@inheritDoc}
     *
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_chat_menu, menu);

        return true;
    }

    /**
     * Selects the actions to run after a menu item in the main chat has been selected.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainChatMenuQuit:
                return shutdownApplication();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean shutdownApplication() {
        System.out.println("MainChatController " + this + ": shutdownApplication !!!!!!!!!!!!!");

        finish();

        return true;
    }

    private Intent createChatServiceIntent() {
        return new Intent(this, ChatService.class);
    }
}
