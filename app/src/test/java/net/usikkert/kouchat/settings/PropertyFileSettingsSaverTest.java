
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.util.IOTools;
import net.usikkert.kouchat.util.PropertyTools;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

/**
 * Test of {@link PropertyFileSettingsSaver}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PropertyFileSettingsSaverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PropertyFileSettingsSaver settingsSaver;

    private Settings settings;

    private IOTools ioTools;
    private PropertyTools propertyTools;
    private CoreMessages coreMessages;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = new Settings();
        coreMessages = new CoreMessages();
        errorHandler = mock(ErrorHandler.class);

        settingsSaver = new PropertyFileSettingsSaver(settings, coreMessages, errorHandler);

        ioTools = TestUtils.setFieldValueWithMock(settingsSaver, "ioTools", IOTools.class);
        propertyTools = TestUtils.setFieldValueWithMock(settingsSaver, "propertyTools", PropertyTools.class);
        TestUtils.setFieldValueWithMock(settingsSaver, "LOG", Logger.class); // To avoid log output in tests
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new PropertyFileSettingsSaver(null, coreMessages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new PropertyFileSettingsSaver(settings, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new PropertyFileSettingsSaver(settings, coreMessages, null);
    }

    @Test
    public void saveSettingsShouldCreateKouChatFolderBeforeSaving() throws IOException {
        settingsSaver.saveSettings();

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

        settingsSaver.saveSettings();

        verify(errorHandler).showError("Settings could not be saved:\njava.io.IOException: Don't save");
    }

    @Test
    public void saveSettingsShouldNotShowErrorWhenOK() throws IOException {
        settingsSaver.saveSettings();

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
        settings.setSystemTray(false);
        settings.setBrowser("firefox");
        settings.setLookAndFeel("starwars");
        settings.setNetworkInterface("wlan2");

        settingsSaver.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(11, properties.size());

        assertEquals("Linda", properties.get(NICK_NAME.getKey()));
        assertEquals("100", properties.get(OWN_COLOR.getKey()));
        assertEquals("-200", properties.get(SYS_COLOR.getKey()));
        assertEquals("false", properties.get(SOUND.getKey()));
        assertEquals("true", properties.get(LOGGING.getKey()));
        assertEquals("false", properties.get(SMILEYS.getKey()));
        assertEquals("true", properties.get(BALLOONS.getKey()));
        assertEquals("false", properties.get(SYSTEM_TRAY.getKey()));
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

        settingsSaver.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(11, properties.size());

        assertEquals("", properties.get(NICK_NAME.getKey()));
        assertEquals("", properties.get(BROWSER.getKey()));
        assertEquals("", properties.get(LOOK_AND_FEEL.getKey()));
        assertEquals("", properties.get(NETWORK_INTERFACE.getKey()));
    }
}
