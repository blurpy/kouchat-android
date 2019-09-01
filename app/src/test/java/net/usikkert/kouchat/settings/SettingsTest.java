
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

/**
 * Test of {@link Settings}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SettingsTest {

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private Settings settings;

    private SettingsListener listener;
    private Setting lastChangedSetting;

    @Before
    public void setUp() throws Exception {
        settings = new Settings();

        System.setProperty("file.separator", "/");

        listener = new SettingsListener() {
            public void settingChanged(final Setting setting) {
                lastChangedSetting = setting;
            }
        };

        settings.addSettingsListener(listener);
    }

    @Test
    public void meShouldBeCreated() {
        final User me = settings.getMe();

        assertTrue(me.isMe());
    }

    @Test
    public void defaultValuesShouldBeSet() {
        verifyDefaultValues(settings);
    }

    @Test
    public void getLogLocationShouldReturnSetValue() {
        settings.setLogLocation("/var/log/kouchat/");

        assertEquals("/var/log/kouchat/", settings.getLogLocation());
    }

    @Test
    public void getLogLocationShouldAlwaysEndWithSlash() {
        settings.setLogLocation("/var/log/kouchat");

        assertEquals("/var/log/kouchat/", settings.getLogLocation());
    }

    @Test
    public void getLogLocationShouldReturnDefaultLocationOfValueNotSet() {
        settings.setLogLocation(null);

        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }

    @Test
    public void setLoggingShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertFalse(settings.isLogging());

        settings.setLogging(false);

        assertFalse(settings.isLogging());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setLoggingShouldNotifyListenersIfSettingIsChanged() {
        assertFalse(settings.isLogging());

        settings.setLogging(true);

        assertTrue(settings.isLogging());
        assertEquals(Setting.LOGGING, lastChangedSetting);
    }

    @Test
    public void isLoggingShouldBeTrueIfAlwaysLogIsEnabled() {
        assertFalse(settings.isLogging());

        settings.setAlwaysLog(true);

        assertTrue(settings.isLogging());
    }

    @Test
    public void setSystemTrayShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertTrue(settings.isSystemTray());

        settings.setSystemTray(true);

        assertTrue(settings.isSystemTray());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setSystemTrayShouldNotifyListenersIfSettingIsChanged() {
        assertTrue(settings.isSystemTray());

        settings.setSystemTray(false);

        assertFalse(settings.isSystemTray());
        assertEquals(Setting.SYSTEM_TRAY, lastChangedSetting);
    }

    @Test
    public void setClientShouldSetClientOnMeWithAppNameAndVersion() {
        final User me = settings.getMe();
        assertEquals("<unknown>", me.getClient());

        settings.setClient("SuperClient");

        assertEquals("KouChat v" + Constants.APP_VERSION + " SuperClient", me.getClient());
    }

    @Test
    public void fireSettingChangedShouldNotifyAllListeners() {
        settings.removeSettingsListener(listener);

        final SettingsListener listener1 = mock(SettingsListener.class);
        final SettingsListener listener2 = mock(SettingsListener.class);

        settings.addSettingsListener(listener1);
        settings.addSettingsListener(listener2);

        final Setting setting = new Setting("MONKEY");

        settings.fireSettingChanged(setting);

        verify(listener1).settingChanged(setting);
        verify(listener2).settingChanged(setting);
    }

    static void verifyDefaultValues(final Settings settings) {
        assertEquals(-15987646, settings.getOwnColor());
        assertEquals(-16759040, settings.getSysColor());

        assertTrue(settings.isSound());
        assertFalse(settings.isLogging());
        assertTrue(settings.isSmileys());
        assertFalse(settings.isBalloons());
        assertTrue(settings.isSystemTray());

        assertEquals("", settings.getBrowser());
        assertEquals("", settings.getLookAndFeel());
        assertNull(settings.getNetworkInterface());

        assertFalse(settings.isNoPrivateChat());
        assertFalse(settings.isAlwaysLog());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }
}
