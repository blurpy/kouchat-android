
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

package net.usikkert.kouchat.android.service;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.misc.Setting;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Validate;

import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Acquires and releases the multicast lock and the wake lock when appropriate.
 *
 * <p>The multicast lock is needed on some devices. The <code>Asus Transformer TF101</code> tablet does not care,
 * but the <code>HTC One</code> phone does not send or receive any multicast messages without a lock.</p>
 *|
 * <p>The wake lock is optional, and helps avoid timeouts because the cpu goes to sleep,
 * and thus can't process packets from the network.</p>
 *
 * <p>It's important to keep the locks as fields, and not just as variables, since
 * garbage collected locks are automatically released.</p>
 *
 * @author Christian Ihle
 */
public class LockHandler implements NetworkConnectionListener, SettingsListener {

    public static final String MULTICAST_LOCK = "KouChat multicast lock";
    public static final String WAKE_LOCK = "KouChat wake lock";

    private final WifiManager.MulticastLock multicastLock;
    private final PowerManager.WakeLock wakeLock;
    private final Settings settings;

    public LockHandler(final AndroidUserInterface androidUserInterface,
                       final Settings settings,
                       final WifiManager wifiManager,
                       final PowerManager powerManager) {
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(wifiManager, "WifiManager can not be null");
        Validate.notNull(powerManager, "PowerManager can not be null");

        this.settings = settings;
        this.multicastLock = wifiManager.createMulticastLock(MULTICAST_LOCK);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK);

        androidUserInterface.registerNetworkConnectionListener(this);
        settings.addSettingsListener(this);
    }

    /**
     * It's important to release the multicast lock when the network goes down,
     * as it can't be reused when the network is back up.
     *
     * <p>Releasing the wake lock as well, as there is no point in keeping a wake lock when there is no network.</p>
     */
    @Override
    public void networkWentDown(final boolean silent) {
        releaseAllLocks();
    }

    /**
     * Need to get the multicast lock before it starts to connect. If not, some of the first messages will be filtered.
     *
     * <p>Gets the wake lock too, if it's enabled, to keep awake from the start.</p>
     */
    @Override
    public void beforeNetworkCameUp() {
        acquireEnabledLocks();
    }

    /**
     * To late do do anything.
     */
    @Override
    public void networkCameUp(final boolean silent) {

    }

    /**
     * Listens for changes in the wake lock setting, and acquires or releases the wake lock accordingly.
     *
     * @param setting The setting which was changed.
     */
    @Override
    public void settingChanged(final Setting setting) {
        if (setting == Setting.WAKE_LOCK) {
            if (settings.isWakeLockEnabled()) {
                acquireWakeLock();
            }

            else {
                releaseWakeLock();
            }
        }
    }

    /**
     * Releases all the locks, if they are being held.
     */
    public void releaseAllLocks() {
        releaseMulticastLock();
        releaseWakeLock();
    }

    /**
     * Acquires the multicast lock, if it's not already held.
     * And the wake lock, if it's enabled and not already held.
     */
    public void acquireEnabledLocks() {
        acquireMulticastLock();

        if (settings.isWakeLockEnabled()) {
            acquireWakeLock();
        }
    }

    /**
     * Returns if the multicast lock has been acquired and is currently held (active).
     *
     * @return If the multicast lock is held.
     */
    public boolean multicastLockIsHeld() {
        return multicastLock.isHeld();
    }

    /**
     * Returns if the wake lock has been acquired and is currently held (active).
     *
     * @return If the wake lock is held.
     */
    public boolean wakeLockIsHeld() {
        return wakeLock.isHeld();
    }

    private void acquireMulticastLock() {
        if (!multicastLockIsHeld()) {
            multicastLock.acquire();
        }
    }

    private void releaseMulticastLock() {
        if (multicastLockIsHeld()) {
            multicastLock.release();
        }
    }

    private void acquireWakeLock() {
        if (!wakeLockIsHeld()) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLockIsHeld()) {
            wakeLock.release();
        }
    }
}
