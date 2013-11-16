
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

package net.usikkert.kouchat.android.service;

import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.misc.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Test of {@link MulticastLockHandler}.
 *
 * @author Christian Ihle
 */
public class MulticastLockHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MulticastLockHandler handler;

    private WifiManager wifiManager;
    private AndroidUserInterface ui;
    private Settings settings;
    private PowerManager powerManager;

    private WifiManager.MulticastLock multicastLock;
    private PowerManager.WakeLock wakeLock;

    @Before
    public void setUp() {
        settings = mock(Settings.class);
        wifiManager = mock(WifiManager.class);
        multicastLock = mock(WifiManager.MulticastLock.class);
        ui = mock(AndroidUserInterface.class);
        powerManager = mock(PowerManager.class);
        wakeLock = mock(PowerManager.WakeLock.class);

        when(wifiManager.createMulticastLock("KouChat multicast lock")).thenReturn(multicastLock);
        when(powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KouChat wake lock")).thenReturn(wakeLock);

        handler = new MulticastLockHandler(ui, settings, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfWifiManagerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("WifiManager can not be null");

        new MulticastLockHandler(ui, settings, null, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfPowerManagerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("PowerManager can not be null");

        new MulticastLockHandler(ui, settings, wifiManager, null);
    }

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new MulticastLockHandler(null, settings, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new MulticastLockHandler(ui, null, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldCreateLocksAndRegisterListener() {
        verify(wifiManager).createMulticastLock("KouChat multicast lock");
        verify(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KouChat wake lock");

        verify(ui).registerNetworkConnectionListener(handler);
    }

    @Test
    public void releaseShouldReleaseMulticastLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);

        handler.release();

        verify(multicastLock).release();
    }

    @Test
    public void releaseShouldNotReleaseMulticastLockIfItIsNotHeld() {
        when(multicastLock.isHeld()).thenReturn(false);

        handler.release();

        verify(multicastLock, never()).release();
    }

    @Test
    public void acquireShouldAcquireMulticastLockIfItIsNotHeld() {
        when(multicastLock.isHeld()).thenReturn(false);

        handler.acquire();

        verify(multicastLock).acquire();
    }

    @Test
    public void acquireShouldNotAcquireMulticastLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);

        handler.acquire();

        verify(multicastLock, never()).acquire();
    }

    @Test
    public void beforeNetworkCameUpShouldAcquireMulticastLock() {
        when(multicastLock.isHeld()).thenReturn(false);

        handler.beforeNetworkCameUp();

        verify(multicastLock).acquire();
    }

    @Test
    public void networkCameUpShouldDoNothing() {
        handler.networkCameUp(false);

        verifyZeroInteractions(multicastLock);
    }

    @Test
    public void networkWentDownShouldReleaseMulticastLock() {
        when(multicastLock.isHeld()).thenReturn(true);

        handler.networkWentDown(false);

        verify(multicastLock).release();
    }
}
