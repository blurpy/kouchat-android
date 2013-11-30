
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link Settings}.
 *
 * @author Christian Ihle
 */
public class SettingsTest {

    private Settings settings;

    private String lastChangedSetting;

    @Before
    public void setUp() throws Exception {
        settings = new Settings();

        // Setting these to known values to avoid tests behaving differently on different machines
        settings.setSound(false);
        settings.setLogging(false);
        settings.setOwnColor(0);
        settings.setSysColor(0);

        System.setProperty("file.separator", "/");

        settings.addSettingsListener(new SettingsListener() {
            public void settingChanged(final String setting) {
                lastChangedSetting = setting;
            }
        });
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
    public void setWakeLockEnabledShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertFalse(settings.isWakeLockEnabled());

        settings.setWakeLockEnabled(false);

        assertFalse(settings.isWakeLockEnabled());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setWakeLockEnabledShouldNotifyListenersIfSettingIsChanged() {
        assertFalse(settings.isWakeLockEnabled());

        settings.setWakeLockEnabled(true);

        assertTrue(settings.isWakeLockEnabled());
        assertEquals("wakeLockEnabled", lastChangedSetting);
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
        assertEquals("logging", lastChangedSetting);
    }

    @Test
    public void setSoundShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertFalse(settings.isSound());

        settings.setSound(false);

        assertFalse(settings.isSound());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setSoundShouldNotifyListenersIfSettingIsChanged() {
        assertFalse(settings.isSound());

        settings.setSound(true);

        assertTrue(settings.isSound());
        assertEquals("sound", lastChangedSetting);
    }

    @Test
    public void setOwnColorShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertEquals(0, settings.getOwnColor());

        settings.setOwnColor(0);

        assertEquals(0, settings.getOwnColor());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setOwnColorShouldNotifyListenersIfSettingIsChanged() {
        assertEquals(0, settings.getOwnColor());

        settings.setOwnColor(100);

        assertEquals(100, settings.getOwnColor());
        assertEquals("ownColor", lastChangedSetting);
    }

    @Test
    public void setSysColorShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertEquals(0, settings.getSysColor());

        settings.setSysColor(0);

        assertEquals(0, settings.getSysColor());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setSysColorShouldNotifyListenersIfSettingIsChanged() {
        assertEquals(0, settings.getSysColor());

        settings.setSysColor(100);

        assertEquals(100, settings.getSysColor());
        assertEquals("sysColor", lastChangedSetting);
    }
}
