
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

package net.usikkert.kouchat.android.setting;

import static org.junit.Assert.*;

import net.usikkert.kouchat.android.util.RobolectricTestUtils;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    private Settings settings;
    private Application context;

    private User me;

    @Before
    public void setUp() {
        settingsLoader = new AndroidSettingsLoader();

        context = Robolectric.application;
        settings = new Settings();

        me = new User("Me", 1234);
        TestUtils.setFieldValue(settings, "me", me);
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
    public void loadStoredSettingsShouldSetInvalidNickToUserCode() {
        RobolectricTestUtils.setNickNameInTheAndroidSettingsTo("123456789012345");
        assertEquals("Me", me.getNick());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("1234", me.getNick());
    }

    @Test
    public void loadStoredSettingsShouldSetMissingNickToUserCode() {
        assertEquals("Me", me.getNick());

        settingsLoader.loadStoredSettings(context, settings);

        assertEquals("1234", me.getNick());
    }
}
