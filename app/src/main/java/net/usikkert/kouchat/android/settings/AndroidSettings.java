
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

package net.usikkert.kouchat.android.settings;

import net.usikkert.kouchat.settings.Settings;

/**
 * Settings that also includes the Android specific settings.
 *
 * @author Christian Ihle
 */
public class AndroidSettings extends Settings {

    /** If the wake lock should be enabled. */
    private boolean wakeLockEnabled;

    private boolean notificationLightEnabled;
    private boolean notificationSoundEnabled;
    private boolean notificationVibrationEnabled;

    public AndroidSettings() {
        wakeLockEnabled = false;

        notificationLightEnabled = true;
        notificationSoundEnabled = true;
        notificationVibrationEnabled = true;
    }

    /**
     * If the wake lock should be enabled.
     *
     * @return If the wake lock should be enabled.
     */
    public boolean isWakeLockEnabled() {
        return wakeLockEnabled;
    }

    /**
     * Sets if the wake lock should be enabled.
     *
     * Listeners are notified of the change.
     *
     * @param wakeLockEnabled If the wake lock should be enabled.
     */
    public void setWakeLockEnabled(final boolean wakeLockEnabled) {
        if (this.wakeLockEnabled != wakeLockEnabled) {
            this.wakeLockEnabled = wakeLockEnabled;
            fireSettingChanged(AndroidSetting.WAKE_LOCK);
        }
    }

    public boolean isNotificationLightEnabled() {
        return notificationLightEnabled;
    }

    public void setNotificationLightEnabled(final boolean notificationLightEnabled) {
        this.notificationLightEnabled = notificationLightEnabled;
    }

    public boolean isNotificationSoundEnabled() {
        return notificationSoundEnabled;
    }

    public void setNotificationSoundEnabled(final boolean notificationSoundEnabled) {
        this.notificationSoundEnabled = notificationSoundEnabled;
    }

    public boolean isNotificationVibrationEnabled() {
        return notificationVibrationEnabled;
    }

    public void setNotificationVibrationEnabled(final boolean notificationVibrationEnabled) {
        this.notificationVibrationEnabled = notificationVibrationEnabled;
    }
}
