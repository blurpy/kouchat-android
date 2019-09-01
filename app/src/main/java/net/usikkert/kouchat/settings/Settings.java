
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

package net.usikkert.kouchat.settings;

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Tools;

import org.jetbrains.annotations.NonNls;

/**
 * This class contains all the application settings.
 *
 * @author Christian Ihle
 */
public class Settings {

    /** A list of listeners. These listeners are notified when a setting is changed. */
    private final List<SettingsListener> listeners;

    // The stored settings:

    /** The application user. Only the nick name is stored. */
    private final User me;

    /** The color of the user's own messages. */
    private int ownColor;

    /** The color the system messages. */
    private int sysColor;

    /** If sound is enabled. */
    private boolean sound;

    /** If logging of the main chat is enabled. */
    private boolean logging;

    /** If smileys are enabled. */
    private boolean smileys;

    /** If balloon notifications are enabled. */
    private boolean balloons;

    /** If the system tray icon is enabled. */
    private boolean systemTray;

    /** The choice of browser to open urls with. */
    private String browser;

    /** Name of the chosen look and feel. */
    private String lookAndFeel;

    /** Name of the network interface to use, or <code>null</code> to choose automatically. */
    private String networkInterface;

    // Settings from startup arguments

    /** If private chat is disabled. */
    private boolean noPrivateChat;

    /** If logging is always enabled. */
    private boolean alwaysLog;

    /** The location to store logs. */
    private String logLocation;

    /**
     * Constructor.
     *
     * <p>Initializes default settings, and creates <code>me</code>.</p>
     *
     * <p>Remember to {@link #setClient(String)}.</p>
     */
    public Settings() {
        final MeFactory meFactory = new MeFactory();

        me = meFactory.createMe();
        listeners = new ArrayList<>();
        browser = "";
        ownColor = -15987646;
        sysColor = -16759040;
        sound = true;
        smileys = true;
        systemTray = true;
        lookAndFeel = "";
    }

    /**
     * Sets the client to report to other users. Like <code>Swing, Android, Console</code>.
     *
     * <p>Must be done before logging on to the network.</p>
     *
     * @param client The client to set, on <code>me</code>.
     */
    public void setClient(@NonNls final String client) {
        me.setClient(Constants.APP_NAME + " v" + Constants.APP_VERSION + " " + client);
    }

    /**
     * Gets the application user.
     *
     * @return The application user.
     */
    public User getMe() {
        return me;
    }

    /**
     * Gets the color used for the user's own messages.
     *
     * @return The color for own messages.
     */
    public int getOwnColor() {
        return ownColor;
    }

    /**
     * Sets the color used for the user's own messages.
     *
     * @param ownColor The color for own messages.
     */
    public void setOwnColor(final int ownColor) {
        this.ownColor = ownColor;
    }

    /**
     * Gets the color used for system messages.
     *
     * @return The color for system messages.
     */
    public int getSysColor() {
        return sysColor;
    }

    /**
     * Sets the color used for system messages.
     *
     * @param sysColor The color for system messages.
     */
    public void setSysColor(final int sysColor) {
        this.sysColor = sysColor;
    }

    /**
     * Checks if sound is enabled.
     *
     * @return If sound is enabled.
     */
    public boolean isSound() {
        return sound;
    }

    /**
     * Sets if sound is enabled.
     *
     * @param sound If sound is enabled.
     */
    public void setSound(final boolean sound) {
        this.sound = sound;
    }

    /**
     * Checks if logging is enabled.
     *
     * @return If logging is enabled.
     */
    public boolean isLogging() {
        if (alwaysLog) {
            return true;
        }

        return logging;
    }

    /**
     * Sets if logging is enabled.
     * Listeners are notified of the change.
     *
     * @param logging If logging is enabled.
     */
    public void setLogging(final boolean logging) {
        if (this.logging != logging) {
            this.logging = logging;
            fireSettingChanged(Setting.LOGGING);
        }
    }

    /**
     * Gets the chosen browser for opening urls.
     *
     * @return The chosen browser.
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * Sets the chosen browser for opening urls.
     *
     * @param browser The chosen browser.
     */
    public void setBrowser(final String browser) {
        this.browser = browser;
    }

    /**
     * Checks if smileys are enabled.
     *
     * @return If smileys are enabled.
     */
    public boolean isSmileys() {
        return smileys;
    }

    /**
     * Sets if smileys are enabled.
     *
     * @param smileys If smileys are enabled.
     */
    public void setSmileys(final boolean smileys) {
        this.smileys = smileys;
    }

    /**
     * Gets the chosen look and feel.
     *
     * @return The chosen look and feel.
     */
    public String getLookAndFeel() {
        return lookAndFeel;
    }

    /**
     * Sets the chosen look and feel.
     *
     * @param lookAndFeel The chosen look and feel.
     */
    public void setLookAndFeel(final String lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    /**
     * Notifies the listeners that <code>setting</code> has changed.
     *
     * @param setting The setting that has changed.
     */
    protected void fireSettingChanged(final Setting setting) {
        for (final SettingsListener listener : listeners) {
            listener.settingChanged(setting);
        }
    }

    /**
     * Adds a listener for changes to the settings.
     *
     * @param listener The listener to add.
     */
    public void addSettingsListener(final SettingsListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for changes to the settings.
     *
     * @param listener The listener to remove.
     */
    public void removeSettingsListener(final SettingsListener listener) {
        listeners.remove(listener);
    }

    /**
     * If private chat is disabled.
     *
     * @return If private chat is disabled.
     */
    public boolean isNoPrivateChat() {
        return noPrivateChat;
    }

    /**
     * Sets if private chat should be disabled.
     *
     * @param noPrivateChat If private chat should be disabled.
     */
    public void setNoPrivateChat(final boolean noPrivateChat) {
        this.noPrivateChat = noPrivateChat;
    }

    /**
     * If logging is always enabled.
     *
     * @return If logging is always enabled.
     */
    public boolean isAlwaysLog() {
        return alwaysLog;
    }

    /**
     * Sets if logging should always be enabled.
     *
     * @param alwaysLog If logging should always be enabled.
     */
    public void setAlwaysLog(final boolean alwaysLog) {
        this.alwaysLog = alwaysLog;
    }

    /**
     * The location to store logs. Returns value from startup argument if set, or the default location otherwise.
     *
     * @return The location to store logs.
     */
    public String getLogLocation() {
        if (!Tools.isEmpty(logLocation)) {
            return Tools.appendSlash(logLocation);
        }

        else {
            return Constants.APP_LOG_FOLDER;
        }
    }

    /**
     * Sets the location to store logs.
     * @param logLocation The location to store logs.
     */
    public void setLogLocation(final String logLocation) {
        this.logLocation = logLocation;
    }

    /**
     * If balloon notifications are enabled.
     *
     * @return If balloon notifications are enabled.
     */
    public boolean isBalloons() {
        return balloons;
    }

    /**
     * Sets if balloon notifications should be enabled.
     *
     * @param balloons If balloon notifications should be enabled.
     */
    public void setBalloons(final boolean balloons) {
        this.balloons = balloons;
    }

    /**
     * If the system tray icon is enabled.
     *
     * @return If the system tray icon is enabled.
     */
    public boolean isSystemTray() {
        return systemTray;
    }

    /**
     * Sets if the system tray icon should be enabled.
     * Listeners are notified of the change.
     *
     * @param systemTray If the system tray icon should be enabled.
     */
    public void setSystemTray(final boolean systemTray) {
        if (this.systemTray != systemTray) {
            this.systemTray = systemTray;
            fireSettingChanged(Setting.SYSTEM_TRAY);
        }
    }

    /**
     * Gets the name of the network interface to use.
     * Can be <code>null</code> to allow KouChat to choose automatically.
     *
     * @return The name of the network interface to use.
     */
    public String getNetworkInterface() {
        return networkInterface;
    }

    /**
     * Sets the name of the network interface to use.
     * Can be <code>null</code> to allow KouChat to choose automatically.
     *
     * @param networkInterface The network interface to use.
     */
    public void setNetworkInterface(final String networkInterface) {
        this.networkInterface = networkInterface;
    }
}
