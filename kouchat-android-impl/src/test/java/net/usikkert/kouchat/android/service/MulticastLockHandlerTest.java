
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import android.net.wifi.WifiManager;

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
    private WifiManager.MulticastLock multicastLock;

    @Before
    public void setUp() {
        wifiManager = mock(WifiManager.class);
        multicastLock = mock(WifiManager.MulticastLock.class);
        ui = mock(AndroidUserInterface.class);

        when(wifiManager.createMulticastLock("KouChat multicast lock")).thenReturn(multicastLock);

        handler = new MulticastLockHandler(wifiManager, ui);
    }

    @Test
    public void constructorShouldThrowExceptionIfWifiManagerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("WifiManager can not be null");

        new MulticastLockHandler(null, ui);
    }

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new MulticastLockHandler(wifiManager, null);
    }

    @Test
    public void constructorShouldCreateMulticastLockAndRegisterListener() {
        verify(wifiManager).createMulticastLock("KouChat multicast lock");
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
