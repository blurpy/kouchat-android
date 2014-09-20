
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

package net.usikkert.kouchat.settings;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link Settings}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SettingsTest {

    private Settings settings;

    private SettingsListener listener;
    private Setting lastChangedSetting;

    @Before
    public void setUp() throws Exception {
        settings = new Settings();

        // Setting these to known values to avoid tests behaving differently on different machines
        settings.setSound(false);
        settings.setLogging(false);
        settings.setOwnColor(0);
        settings.setSysColor(0);

        System.setProperty("file.separator", "/");

        listener = new SettingsListener() {
            public void settingChanged(final Setting setting) {
                lastChangedSetting = setting;
            }
        };

        settings.addSettingsListener(listener);
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
    public void setSoundShouldWork() {
        assertFalse(settings.isSound());

        settings.setSound(true);

        assertTrue(settings.isSound());
    }

    @Test
    public void setOwnColorShouldWork() {
        assertEquals(0, settings.getOwnColor());

        settings.setOwnColor(100);

        assertEquals(100, settings.getOwnColor());
    }

    @Test
    public void setSysColorShouldWork() {
        assertEquals(0, settings.getSysColor());

        settings.setSysColor(100);

        assertEquals(100, settings.getSysColor());
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
}
