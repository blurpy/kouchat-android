
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

package net.usikkert.kouchat.android.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.misc.Setting;
import net.usikkert.kouchat.misc.Settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Test of {@link LockHandler}.
 *
 * @author Christian Ihle
 */
public class LockHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private LockHandler handler;

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

        handler = new LockHandler(ui, settings, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfWifiManagerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("WifiManager can not be null");

        new LockHandler(ui, settings, null, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfPowerManagerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("PowerManager can not be null");

        new LockHandler(ui, settings, wifiManager, null);
    }

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new LockHandler(null, settings, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new LockHandler(ui, null, wifiManager, powerManager);
    }

    @Test
    public void constructorShouldCreateLocksAndRegisterListeners() {
        verify(wifiManager).createMulticastLock("KouChat multicast lock");
        verify(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KouChat wake lock");

        verify(ui).registerNetworkConnectionListener(handler);
        verify(settings).addSettingsListener(handler);
    }

    @Test
    public void releaseAllLocksShouldReleaseMulticastLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);

        handler.releaseAllLocks();

        verify(multicastLock).release();
    }

    @Test
    public void releaseAllLocksShouldReleaseWakeLockIfItIsHeld() {
        when(wakeLock.isHeld()).thenReturn(true);

        handler.releaseAllLocks();

        verify(wakeLock).release();
    }

    @Test
    public void releaseAllLocksShouldReleaseAllLocksIfAllIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);
        when(wakeLock.isHeld()).thenReturn(true);

        handler.releaseAllLocks();

        verify(multicastLock).release();
        verify(wakeLock).release();
    }

    @Test
    public void releaseAllLocksShouldNotReleaseMulticastLockIfItIsNotHeld() {
        when(multicastLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(multicastLock, never()).release();
    }

    @Test
    public void releaseAllLocksShouldNotReleaseWakeLockIfItIsNotHeld() {
        when(wakeLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(wakeLock, never()).release();
    }

    @Test
    public void releaseAllLocksShouldNotReleaseAnyLocksIfNoneIsHeld() {
        when(multicastLock.isHeld()).thenReturn(false);
        when(wakeLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(multicastLock, never()).release();
        verify(wakeLock, never()).release();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireMulticastLockIfItIsNotHeld() {
        when(multicastLock.isHeld()).thenReturn(false);

        handler.acquireEnabledLocks();

        verify(multicastLock).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireMulticastLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(multicastLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireWakeLockIfItIsNotHeldAndEnabled() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(settings.isWakeLockEnabled()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(wakeLock).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireWakeLockIfItIsNotHeldAndDisabled() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(settings.isWakeLockEnabled()).thenReturn(false);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireWakeLockIfItIsHeldAndEnabled() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(settings.isWakeLockEnabled()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireWakeLockIfItIsHeldAndDisabled() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(settings.isWakeLockEnabled()).thenReturn(false);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
    }

    @Test
    public void beforeNetworkCameUpShouldAcquireAllLocks() {
        when(multicastLock.isHeld()).thenReturn(false);
        when(wakeLock.isHeld()).thenReturn(false);
        when(settings.isWakeLockEnabled()).thenReturn(true);

        handler.beforeNetworkCameUp();

        verify(multicastLock).acquire();
        verify(wakeLock).acquire();
    }

    @Test
    public void networkCameUpShouldDoNothing() {
        handler.networkCameUp(false);

        verifyZeroInteractions(multicastLock);
    }

    @Test
    public void networkWentDownShouldReleaseAllLocks() {
        when(multicastLock.isHeld()).thenReturn(true);
        when(wakeLock.isHeld()).thenReturn(true);

        handler.networkWentDown(false);

        verify(multicastLock).release();
        verify(wakeLock).release();
    }

    @Test
    public void settingChangedShouldAcquireWakeLockIfItIsChangedToEnabled() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(settings.isWakeLockEnabled()).thenReturn(true);

        handler.settingChanged(Setting.WAKE_LOCK);

        verify(wakeLock).acquire();
    }

    @Test
    public void settingChangedShouldReleaseWakeLockIfItIsChangedToDisabled() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(settings.isWakeLockEnabled()).thenReturn(false);

        handler.settingChanged(Setting.WAKE_LOCK);

        verify(wakeLock).release();
    }

    @Test
    public void settingChangedShouldNotCareAboutOtherSettings() {
        handler.settingChanged(Setting.LOGGING);

        verifyZeroInteractions(wakeLock, multicastLock);
    }

    @Test
    public void multicastLockIsHeldShouldReturnValueFromMulticastLock() {
        when(multicastLock.isHeld()).thenReturn(true);
        assertTrue(handler.multicastLockIsHeld());

        when(multicastLock.isHeld()).thenReturn(false);
        assertFalse(handler.multicastLockIsHeld());
    }

    @Test
    public void wakeLockIsHeldShouldReturnValueFromWakeLock() {
        when(wakeLock.isHeld()).thenReturn(true);
        assertTrue(handler.wakeLockIsHeld());

        when(wakeLock.isHeld()).thenReturn(false);
        assertFalse(handler.wakeLockIsHeld());
    }

    @Test
    public void settingsListenerShouldEnableAndDisableWakeLockBasedOnChangedSetting() {
        final Settings realSettings = new Settings();
        new LockHandler(ui, realSettings, wifiManager, powerManager);

        verifyZeroInteractions(wakeLock);
        makeWakeLockIsHeldReturnTrueAfterAcquire(); // Like it would with the real implementation

        realSettings.setWakeLockEnabled(true);
        verify(wakeLock).acquire();

        realSettings.setWakeLockEnabled(false);
        verify(wakeLock).release();
    }

    private void makeWakeLockIsHeldReturnTrueAfterAcquire() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                when(wakeLock.isHeld()).thenReturn(true);
                return null;
            }
        }).when(wakeLock).acquire();
    }
}
