
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

package net.usikkert.kouchat.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.util.IOTools;
import net.usikkert.kouchat.util.Tools;

/**
 * This is a class that loads and saves the application settings to file.
 *
 * <p>These settings are persisted:</p>
 *
 * <ul>
 *   <li>Nick name</li>
 *   <li>Browser</li>
 *   <li>Enable sound</li>
 *   <li>Enable logging</li>
 *   <li>Enable balloons</li>
 *   <li>Enable smileys</li>
 *   <li>Own message color</li>
 *   <li>System message color</li>
 *   <li>Chosen look and feel</li>
 *   <li>Chosen network interface</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class Settings {

    private static final Logger LOG = Logger.getLogger(Settings.class.getName());

    /** The path to the file storing the settings. */
    private static final String FILENAME = Constants.APP_FOLDER + "kouchat.ini";

    private final IOTools ioTools = new IOTools();

    /** A list of listeners. These listeners are notified when a setting is changed. */
    private final List<SettingsListener> listeners;

    /** The error handler, for showing messages to the user. */
    private final ErrorHandler errorHandler;

    // The stored settings:

    /**
     * The nick name of the application user. The rest of the values in <code>me</code>
     * is generated in the constructor.
     */
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

    // Android settings

    /** If the wake lock should be enabled. */
    private boolean wakeLockEnabled;

    /**
     * Constructor.
     *
     * <p>Initializes default settings, and creates <code>me</code>.</p>
     *
     * <p>Remember to {@link #setClient(String)}.</p>
     */
    public Settings() {
        final int code = 10000000 + (int) (Math.random() * 9999999);

        me = new User(createNickName(code), code);
        me.setMe(true);
        me.setLastIdle(System.currentTimeMillis());
        me.setLogonTime(System.currentTimeMillis());
        me.setOperatingSystem(System.getProperty("os.name"));

        listeners = new ArrayList<SettingsListener>();
        errorHandler = ErrorHandler.getErrorHandler();
        browser = "";
        ownColor = -15987646;
        sysColor = -16759040;
        sound = true;
        smileys = true;
        lookAndFeel = "";

        wakeLockEnabled = false;

        loadSettings();
    }

    /**
     * Sets the client to report to other users. Like <code>Swing, Android, Console</code>.
     *
     * <p>Must be done before logging on to the network.</p>
     *
     * @param client The client to set, on <code>me</code>.
     */
    public void setClient(final String client) {
        me.setClient(Constants.APP_NAME + " v" + Constants.APP_VERSION + " " + client);
    }

    /**
     * Creates a new default nick name from the name of the user logged in to
     * the operating system. The name is shortened to 10 characters and the
     * first letter is capitalized.
     *
     * <p>If the name is invalid as a nick name then the user code is used instead.</p>
     *
     * @param code The user code.
     * @return The created nick name.
     */
    private String createNickName(final int code) {
        final String userName = System.getProperty("user.name");

        if (userName == null) {
            return Integer.toString(code);
        }

        final String[] splitUserName = userName.split(" ");
        final String defaultNick = Tools.capitalizeFirstLetter(Tools.shorten(splitUserName[0].trim(), 10));

        if (Tools.isValidNick(defaultNick)) {
            return defaultNick;
        }

        return Integer.toString(code);
    }

    /**
     * Saves the current settings to file. Creates any missing folders
     * or files.
     */
    public void saveSettings() {
        FileWriter fileWriter = null;
        BufferedWriter buffWriter = null;

        final File appFolder = new File(Constants.APP_FOLDER);

        if (!appFolder.exists()) {
            appFolder.mkdir();
        }

        try {
            fileWriter = new FileWriter(FILENAME);
            buffWriter = new BufferedWriter(fileWriter);

            buffWriter.write("nick=" + me.getNick());
            buffWriter.newLine();
            buffWriter.write("owncolor=" + ownColor);
            buffWriter.newLine();
            buffWriter.write("syscolor=" + sysColor);
            buffWriter.newLine();
            buffWriter.write("logging=" + logging);
            buffWriter.newLine();
            buffWriter.write("sound=" + sound);
            buffWriter.newLine();
            // Properties does not support loading back slash, so replace with forward slash
            buffWriter.write("browser=" + browser.replaceAll("\\\\", "/"));
            buffWriter.newLine();
            buffWriter.write("smileys=" + smileys);
            buffWriter.newLine();
            buffWriter.write("lookAndFeel=" + lookAndFeel);
            buffWriter.newLine();
            buffWriter.write("balloons=" + balloons);
            buffWriter.newLine();
            buffWriter.write("networkInterface=" + networkInterface);
            buffWriter.newLine();
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString());
            errorHandler.showError("Settings could not be saved:\n " + e);
        }

        finally {
            ioTools.flush(buffWriter);
            ioTools.flush(fileWriter);
            ioTools.close(buffWriter);
            ioTools.close(fileWriter);
        }
    }

    /**
     * Loads the settings from file.
     * If some values are not found in the settings, the default is used instead.
     */
    private void loadSettings() {
        FileInputStream fileStream = null;

        try {
            final Properties fileContents = new Properties();
            fileStream = new FileInputStream(FILENAME);
            fileContents.load(fileStream);

            final String tmpNick = fileContents.getProperty("nick");

            if (tmpNick != null && Tools.isValidNick(tmpNick)) {
                me.setNick(tmpNick.trim());
            }

            try {
                ownColor = Integer.parseInt(fileContents.getProperty("owncolor"));
            }

            catch (final NumberFormatException e) {
                LOG.log(Level.WARNING, "Could not read setting for owncolor..");
            }

            try {
                sysColor = Integer.parseInt(fileContents.getProperty("syscolor"));
            }

            catch (final NumberFormatException e) {
                LOG.log(Level.WARNING, "Could not read setting for syscolor..");
            }

            logging = Boolean.valueOf(fileContents.getProperty("logging"));
            balloons = Boolean.valueOf(fileContents.getProperty("balloons"));
            browser = fileContents.getProperty("browser");
            lookAndFeel = fileContents.getProperty("lookAndFeel");
            networkInterface = fileContents.getProperty("networkInterface");

            // Defaults to true
            if (fileContents.getProperty("sound") != null) {
                sound = Boolean.valueOf(fileContents.getProperty("sound"));
            }

            // Defaults to true
            if (fileContents.getProperty("smileys") != null) {
                smileys = Boolean.valueOf(fileContents.getProperty("smileys"));
            }
        }

        catch (final FileNotFoundException e) {
            LOG.log(Level.WARNING, "Could not find " + FILENAME + ", using default settings.");
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        finally {
            ioTools.close(fileStream);
        }
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
     * Listeners are notified of the change.
     *
     * @param ownColor The color for own messages.
     */
    public void setOwnColor(final int ownColor) {
        if (this.ownColor != ownColor) {
            this.ownColor = ownColor;
            fireSettingChanged(Setting.OWN_COLOR);
        }
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
     * Listeners are notified of the change.
     *
     * @param sysColor The color for system messages.
     */
    public void setSysColor(final int sysColor) {
        if (this.sysColor != sysColor) {
            this.sysColor = sysColor;
            fireSettingChanged(Setting.SYS_COLOR);
        }
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
     * Listeners are notified of the change.
     *
     * @param sound If sound is enabled.
     */
    public void setSound(final boolean sound) {
        if (this.sound != sound) {
            this.sound = sound;
            fireSettingChanged(Setting.SOUND);
        }
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
    private void fireSettingChanged(final Setting setting) {
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
            fireSettingChanged(Setting.WAKE_LOCK);
        }
    }
}
