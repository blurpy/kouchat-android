
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

package net.usikkert.kouchat.android.service;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.settings.AndroidSetting;
import net.usikkert.kouchat.android.settings.AndroidSettings;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.settings.Setting;
import net.usikkert.kouchat.util.Validate;

import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Acquires and releases the wake lock, the wifi lock and the multicast lock when appropriate.
 *
 * <p>The wake lock is optional, and helps avoid timeouts because the cpu goes to sleep,
 * and thus can't process packets from the network.</p>
 *
 * <p>The wifi lock can help with packet loss on some devices.</p>
 *
 * <p>The multicast lock is needed to process multicast packets on some devices.
 * The <code>Asus Transformer TF101</code> does not care, but the <code>HTC One</code> does not send
 * or receive any multicast packets without a lock.</p>
 *
 * <p>It's important to keep the locks as fields, and not just as variables, since
 * garbage collected locks are automatically released.</p>
 *
 * @author Christian Ihle
 */
public class LockHandler implements NetworkConnectionListener, SettingsListener {

    public static final String WAKE_LOCK = "KouChat: wake lock";
    public static final String WIFI_LOCK = "KouChat: wifi lock";
    public static final String MULTICAST_LOCK = "KouChat multicast lock";

    private final PowerManager.WakeLock wakeLock;
    private final WifiManager.WifiLock wifiLock;
    private final WifiManager.MulticastLock multicastLock;

    private final AndroidSettings settings;

    public LockHandler(final AndroidUserInterface androidUserInterface,
                       final AndroidSettings settings,
                       final WifiManager wifiManager,
                       final PowerManager powerManager) {
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(wifiManager, "WifiManager can not be null");
        Validate.notNull(powerManager, "PowerManager can not be null");

        this.settings = settings;

        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK);
        this.wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFI_LOCK);
        this.multicastLock = wifiManager.createMulticastLock(MULTICAST_LOCK);

        androidUserInterface.registerNetworkConnectionListener(this);
        settings.addSettingsListener(this);
    }

    /**
     * It's important to release the multicast lock when the network goes down,
     * as it can't be reused when the network is back up.
     *
     * <p>Releasing the other locks as well, as there is no point in keeping locks when there is no network.</p>
     */
    @Override
    public void networkWentDown(final boolean silent) {
        releaseAllLocks();
    }

    /**
     * Need to get the multicast lock before it starts to connect. If not, some of the first messages will be filtered.
     *
     * <p>Gets the other locks too, if it's enabled, to keep awake from the start.</p>
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
        if (setting.equals(AndroidSetting.WAKE_LOCK)) {
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
    public synchronized void releaseAllLocks() {
        releaseMulticastLock();
        releaseWifiLock();
        releaseWakeLock();
    }

    /**
     * Acquires the wifi lock and multicast lock, if not already held.
     * And the wake lock, if it's enabled and not already held.
     */
    public void acquireEnabledLocks() {
        if (settings.isWakeLockEnabled()) {
            acquireWakeLock();
        }

        acquireWifiLock();
        acquireMulticastLock();
    }

    /**
     * Returns if the wifi lock has been acquired and is currently held (active).
     *
     * @return If the wifi lock is held.
     */
    public boolean wifiLockIsHeld() {
        return wifiLock.isHeld();
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

    private void acquireWifiLock() {
        if (!wifiLockIsHeld()) {
            wifiLock.acquire();
        }
    }

    private void releaseWifiLock() {
        if (wifiLockIsHeld()) {
            wifiLock.release();
        }
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
