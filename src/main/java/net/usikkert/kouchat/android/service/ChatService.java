
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

package net.usikkert.kouchat.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service for accessing the chat operations in lower layers.
 *
 * @author Christian Ihle
 */
public class ChatService extends Service {


    @Override
    public void onCreate() {
        System.out.println("ChatService " + this + ": onCreate !!!!!!!!!!!!!");

        super.onCreate();
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        System.out.println("ChatService " + this + ": onStart !!!!!!!!!!!!! " + startId);

        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        System.out.println("ChatService " + this + ": onStartCommand !!!!!!!!!!!!! " + startId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        System.out.println("ChatService " + this + ": onBind !!!!!!!!!!!!!");

        return null;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        System.out.println("ChatService " + this + ": onUnbind !!!!!!!!!!!!!");

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(final Intent intent) {
        System.out.println("ChatService " + this + ": onRebind !!!!!!!!!!!!!");

        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("ChatService " + this + ": onDestroy !!!!!!!!!!!!!");

        super.onDestroy();
    }
}
