
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

package net.usikkert.kouchat.android.service;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.util.Validate;

import android.net.wifi.WifiManager;

/**
 * Acquires and releases the multicast lock when appropriate.
 *
 * <p>This is needed on some devices. The <code>Asus Transformer TF101</code> tablet does not care,
 * but the <code>HTC One</code> phone does not send or receive any multicast messages without a lock.</p>
 *
 * <p>It's important to keep the lock as a field, and not just a variable, since
 * garbage collected locks are automatically released.</p>
 *
 * @author Christian Ihle
 */
public class MulticastLockHandler implements NetworkConnectionListener {

    public static final String MULTICAST_LOCK = "KouChat multicast lock";

    private final WifiManager.MulticastLock multicastLock;

    public MulticastLockHandler(final WifiManager wifiManager, final AndroidUserInterface androidUserInterface) {
        Validate.notNull(wifiManager, "WifiManager can not be null");
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");

        multicastLock = wifiManager.createMulticastLock(MULTICAST_LOCK);
        androidUserInterface.registerNetworkConnectionListener(this);
    }

    /**
     * It's important to release the lock when the network goes down,
     * as it can't be reused when the network is back up.
     */
    @Override
    public void networkWentDown(final boolean silent) {
        release();
    }

    /**
     * Need to get the lock before it starts to connect. If not, some of the first messages will be filtered.
     */
    @Override
    public void beforeNetworkCameUp() {
        acquire();
    }

    /**
     * To late do do anything.
     */
    @Override
    public void networkCameUp(final boolean silent) {

    }

    /**
     * Releases the multicast lock, if it's being held.
     */
    public void release() {
        if (multicastLock.isHeld()) {
            multicastLock.release();
        }
    }

    /**
     * Acquires the multicast lock, if it's not already held.
     */
    public void acquire() {
        if (!multicastLock.isHeld()) {
            multicastLock.acquire();
        }
    }
}
