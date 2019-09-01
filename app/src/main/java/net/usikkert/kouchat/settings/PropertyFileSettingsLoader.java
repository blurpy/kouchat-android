
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

import static net.usikkert.kouchat.settings.PropertyFileSettings.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.PropertyTools;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * Loads settings stored in <code>~/.kouchat/kouchat.ini</code>.
 *
 * @author Christian Ihle
 */
public class PropertyFileSettingsLoader {

    /** The full path to the file where settings are stored. */
    public static final String SETTINGS_FILE = Constants.APP_FOLDER + "kouchat.ini";

    private static final Logger LOG = Logger.getLogger(PropertyFileSettingsLoader.class.getName());

    private final PropertyTools propertyTools = new PropertyTools();

    /**
     * Loads the settings from file.
     *
     * <p>If some values are not found in the settings, the default is used instead.</p>
     *
     * @param settings The settings to load into.
     */
    public void loadSettings(final Settings settings) {
        Validate.notNull(settings, "Settings can not be null");

        try {
            final Properties fileContents = propertyTools.loadProperties(SETTINGS_FILE);

            setNickName(settings, fileContents);
            setOwnColor(settings, fileContents);
            setSysColor(settings, fileContents);
            setLogging(settings, fileContents);
            setBalloons(settings, fileContents);
            setSystemTray(settings, fileContents);
            setBrowser(settings, fileContents);
            setLookAndFeel(settings, fileContents);
            setNetworkInterface(settings, fileContents);
            setSound(settings, fileContents);
            setSmileys(settings, fileContents);
        }

        catch (final FileNotFoundException e) {
            LOG.log(Level.WARNING, "Could not find " + SETTINGS_FILE + ", using default settings.");
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }
    }

    private void setNickName(final Settings settings, final Properties fileContents) {
        final User me = settings.getMe();
        final String loadedNickName = fileContents.getProperty(NICK_NAME.getKey());

        if (loadedNickName != null && Tools.isValidNick(loadedNickName)) {
            me.setNick(loadedNickName.trim());
        }
    }

    private void setOwnColor(final Settings settings, final Properties fileContents) {
        try {
            settings.setOwnColor(Integer.parseInt(fileContents.getProperty(OWN_COLOR.getKey())));
        }

        catch (final NumberFormatException e) {
            LOG.log(Level.WARNING, "Could not read setting for owncolor...");
        }
    }

    private void setSysColor(final Settings settings, final Properties fileContents) {
        try {
            settings.setSysColor(Integer.parseInt(fileContents.getProperty(SYS_COLOR.getKey())));
        }

        catch (final NumberFormatException e) {
            LOG.log(Level.WARNING, "Could not read setting for syscolor...");
        }
    }

    private void setLogging(final Settings settings, final Properties fileContents) {
        settings.setLogging(Boolean.valueOf(fileContents.getProperty(LOGGING.getKey())));
    }

    private void setBalloons(final Settings settings, final Properties fileContents) {
        settings.setBalloons(Boolean.valueOf(fileContents.getProperty(BALLOONS.getKey())));
    }

    private void setSystemTray(final Settings settings, final Properties fileContents) {
        // Defaults to true
        if (fileContents.getProperty(SYSTEM_TRAY.getKey()) != null) {
            settings.setSystemTray(Boolean.valueOf(fileContents.getProperty(SYSTEM_TRAY.getKey())));
        }
    }

    private void setBrowser(final Settings settings, final Properties fileContents) {
        settings.setBrowser(Tools.emptyIfNull(fileContents.getProperty(BROWSER.getKey())));
    }

    private void setLookAndFeel(final Settings settings, final Properties fileContents) {
        settings.setLookAndFeel(Tools.emptyIfNull(fileContents.getProperty(LOOK_AND_FEEL.getKey())));
    }

    private void setNetworkInterface(final Settings settings, final Properties fileContents) {
        settings.setNetworkInterface(fileContents.getProperty(NETWORK_INTERFACE.getKey()));
    }

    private void setSound(final Settings settings, final Properties fileContents) {
        // Defaults to true
        if (fileContents.getProperty(SOUND.getKey()) != null) {
            settings.setSound(Boolean.valueOf(fileContents.getProperty(SOUND.getKey())));
        }
    }

    private void setSmileys(final Settings settings, final Properties fileContents) {
        // Defaults to true
        if (fileContents.getProperty(SMILEYS.getKey()) != null) {
            settings.setSmileys(Boolean.valueOf(fileContents.getProperty(SMILEYS.getKey())));
        }
    }
}
