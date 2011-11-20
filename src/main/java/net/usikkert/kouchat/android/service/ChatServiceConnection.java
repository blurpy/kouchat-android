
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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Representation of a connection to a bound {@link ChatService}.
 *
 * @author Christian Ihle
 */
public class ChatServiceConnection implements ServiceConnection {

    @Override
    public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
        System.out.println("ChatServiceConnection " + this + ": onServiceConnected !!!!!!!!!!!!!");
    }

    @Override
    public void onServiceDisconnected(final ComponentName componentName) {
        System.out.println("ChatServiceConnection " + this + ": onServiceDisconnected !!!!!!!!!!!!!");
    }
}
