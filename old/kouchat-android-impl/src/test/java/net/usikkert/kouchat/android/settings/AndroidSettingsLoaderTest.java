
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

import static org.junit.Assert.*;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.android.util.RobolectricTestUtils;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.app.Application;

/**
 * Test of {@link AndroidSettingsLoader}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidSettingsLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AndroidSettingsLoader settingsLoader;

    private AndroidSettings settings;
    private Application context;

    private User me;

    @Before
    public void setUp() {
        settingsLoader = new AndroidSettingsLoader();

        context = Robolectric.application;
        settings = new AndroidSettings();

        me = new User("Me", 1234);
        TestUtils.setFieldValue(settings, "me", me);

        settings.setOwnColor(0);
        settings.setSysColor(0);
    }

    @Test
    public void loadStoredSettingsShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        settingsLoader.loadStoredSettings(null, settings);
    }

    @Test
    public void loadStoredSettingsShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        settingsLoader.loadStoredSettings(context, null);
    }

    @Test
    public void loadStoredSettingsShouldSetValidNick() {
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("Robofied");
        assertEquals("Me", me.getNick());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("Robofied", me.getNick());
    }

    @Test
    public void loadStoredSettingsShouldSetInvalidNickToNewUser() {
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("123456789012345");
        assertEquals("Me", me.getNick());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("NewUser", me.getNick());
    }

    @Test
    public void loadStoredSettingsShouldSetNickToNewUserIfNotSet() {
        assertEquals("Me", me.getNick());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("NewUser", me.getNick());
    }

    @Test
    public void loadStoredSettingsShouldSetWakeLockToFalseIfNotSet() {
        settingsLoader.loadStoredSettings(context, settings);

        assertFalse(settings.isWakeLockEnabled());
    }

    @Test
    public void loadStoredSettingsShouldSetWakeLockToFalseIfSetToFalse() {
        RobolectricTestUtils.setWakeLockInTheAndroidSettingsTo(false);

        settingsLoader.loadStoredSettings(context, settings);

        assertFalse(settings.isWakeLockEnabled());
    }

    @Test
    public void loadStoredSettingsShouldSetWakeLockToTrueIfSetToTrue() {
        RobolectricTestUtils.setWakeLockInTheAndroidSettingsTo(true);

        settingsLoader.loadStoredSettings(context, settings);

        assertTrue(settings.isWakeLockEnabled());
    }

    @Test
    public void loadStoredSettingsShouldNotSetOwnColorIfNotSet() {
        settingsLoader.loadStoredSettings(context, settings);

        assertEquals(0, settings.getOwnColor());
    }

    @Test
    public void loadStoredSettingsShouldSetOwnColorWhenSet() {
        RobolectricTestUtils.setOwnColorInTheAndroidSettingsTo(12345);

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals(12345, settings.getOwnColor());
    }

    @Test
    public void loadStoredSettingsShouldNotSetSystemColorIfNotSet() {
        settingsLoader.loadStoredSettings(context, settings);

        assertEquals(0, settings.getSysColor());
    }

    @Test
    public void loadStoredSettingsShouldSetSystemColorWhenSet() {
        RobolectricTestUtils.setSystemColorInTheAndroidSettingsTo(54321);

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals(54321, settings.getSysColor());
    }

    @Test
    public void loadStoredSettingsShouldSetAndroidClient() {
        assertEquals("<unknown>", me.getClient());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("KouChat v" + Constants.APP_VERSION + " Android", me.getClient());
    }
}
