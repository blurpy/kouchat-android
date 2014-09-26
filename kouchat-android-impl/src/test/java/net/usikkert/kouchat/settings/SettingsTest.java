
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

import static net.usikkert.kouchat.settings.PropertyFileSettings.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.SettingsListener;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.IOTools;
import net.usikkert.kouchat.util.PropertyTools;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

/**
 * Test of {@link Settings}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class SettingsTest {

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties("file.separator");

    private Settings settings;

    private SettingsListener listener;
    private Setting lastChangedSetting;

    private IOTools ioTools;
    private PropertyTools propertyTools;
    private ErrorHandler errorHandler;

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

        ioTools = TestUtils.setFieldValueWithMock(settings, "ioTools", IOTools.class);
        propertyTools = TestUtils.setFieldValueWithMock(settings, "propertyTools", PropertyTools.class);
        errorHandler = TestUtils.setFieldValueWithMock(settings, "errorHandler", ErrorHandler.class);
        TestUtils.setFieldValueWithMock(settings, "LOG", Logger.class); // To avoid log output in tests
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

    @Test
    public void saveSettingsShouldCreateKouChatFolderBeforeSaving() throws IOException {
        settings.saveSettings();

        final InOrder inOrder = inOrder(ioTools, propertyTools);

        inOrder.verify(ioTools).createFolder(Constants.APP_FOLDER);
        inOrder.verify(propertyTools).saveProperties(eq(Constants.APP_FOLDER + "kouchat.ini"),
                                                     any(Properties.class),
                                                     eq("KouChat Settings"));
    }

    @Test
    public void saveSettingsShouldShowErrorOnException() throws IOException {
        doThrow(new IOException("Don't save")).when(propertyTools).saveProperties(
                anyString(), any(Properties.class), anyString());

        settings.saveSettings();

        verify(errorHandler).showError("Settings could not be saved:\n java.io.IOException: Don't save");
    }

    @Test
    public void saveSettingsShouldNotShowErrorWhenOK() throws IOException {
        settings.saveSettings();

        verify(errorHandler, never()).showError(anyString());
    }

    @Test
    public void saveSettingsShouldConvertAllValuesToStringsToAvoidClassCastException() throws IOException {
        settings.getMe().setNick("Linda");
        settings.setOwnColor(100);
        settings.setSysColor(-200);
        settings.setSound(false);
        settings.setLogging(true);
        settings.setSmileys(false);
        settings.setBalloons(true);
        settings.setBrowser("firefox");
        settings.setLookAndFeel("starwars");
        settings.setNetworkInterface("wlan2");

        settings.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(10, properties.size());

        assertEquals("Linda", properties.get(NICK_NAME.getKey()));
        assertEquals("100", properties.get(OWN_COLOR.getKey()));
        assertEquals("-200", properties.get(SYS_COLOR.getKey()));
        assertEquals("false", properties.get(SOUND.getKey()));
        assertEquals("true", properties.get(LOGGING.getKey()));
        assertEquals("false", properties.get(SMILEYS.getKey()));
        assertEquals("true", properties.get(BALLOONS.getKey()));
        assertEquals("firefox", properties.get(BROWSER.getKey()));
        assertEquals("starwars", properties.get(LOOK_AND_FEEL.getKey()));
        assertEquals("wlan2", properties.get(NETWORK_INTERFACE.getKey()));
    }

    @Test
    public void saveSettingsShouldHandleNullStringsToAvoidNullPointerException() throws IOException {
        settings.getMe().setNick(null);
        settings.setBrowser(null);
        settings.setLookAndFeel(null);
        settings.setNetworkInterface(null);

        settings.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(10, properties.size());

        assertEquals("", properties.get(NICK_NAME.getKey()));
        assertEquals("", properties.get(BROWSER.getKey()));
        assertEquals("", properties.get(LOOK_AND_FEEL.getKey()));
        assertEquals("", properties.get(NETWORK_INTERFACE.getKey()));
    }

    static void verifyDefaultValues(final Settings settings) {
        assertEquals(-15987646, settings.getOwnColor());
        assertEquals(-16759040, settings.getSysColor());

        assertTrue(settings.isSound());
        assertFalse(settings.isLogging());
        assertTrue(settings.isSmileys());
        assertFalse(settings.isBalloons());

        assertEquals("", settings.getBrowser());
        assertEquals("", settings.getLookAndFeel());
        assertNull(settings.getNetworkInterface());

        assertFalse(settings.isNoPrivateChat());
        assertFalse(settings.isAlwaysLog());
        assertEquals(Constants.APP_LOG_FOLDER, settings.getLogLocation());
    }
}
