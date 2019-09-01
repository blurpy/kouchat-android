
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

package net.usikkert.kouchat.android.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.settings.AndroidSetting;
import net.usikkert.kouchat.android.settings.AndroidSettings;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.settings.Setting;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Test of {@link LockHandler}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class LockHandlerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private LockHandler handler;

    private AndroidSettings settings;
    private AndroidUserInterface ui;

    private WifiManager wifiManager;
    private PowerManager powerManager;

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private WifiManager.MulticastLock multicastLock;

    @Before
    @SuppressLint("InlinedApi")
    public void setUp() {
        settings = mock(AndroidSettings.class);
        ui = mock(AndroidUserInterface.class);

        wifiManager = mock(WifiManager.class);
        powerManager = mock(PowerManager.class);

        wakeLock = mock(PowerManager.WakeLock.class);
        wifiLock = mock(WifiManager.WifiLock.class);
        multicastLock = mock(WifiManager.MulticastLock.class);

        when(powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KouChat wake lock")).thenReturn(wakeLock);
        when(wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "KouChat wifi lock")).thenReturn(wifiLock);
        when(wifiManager.createMulticastLock("KouChat multicast lock")).thenReturn(multicastLock);

        handler = new LockHandler(ui, settings, wifiManager, powerManager);

        when(settings.isWakeLockEnabled()).thenReturn(true);
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
        verify(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KouChat wake lock");
        verify(wifiManager).createWifiLock(anyInt(), anyString());
        verify(wifiManager).createMulticastLock("KouChat multicast lock");

        verify(ui).registerNetworkConnectionListener(handler);
        verify(settings).addSettingsListener(handler);
    }

    @Test
    @Config(reportSdk = 10)
    public void constructorShouldCreateOlderWifiLockOnApi10() {
        verify(wifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL, "KouChat wifi lock");
    }

    @Test
    @Config(reportSdk = 11)
    public void constructorShouldCreateOlderWifiLockOnApi11() {
        verify(wifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL, "KouChat wifi lock");
    }

    @Test
    @Config(reportSdk = 12)
    @SuppressLint("InlinedApi")
    public void constructorShouldCreateNewWifiLockOnApi12() {
        verify(wifiManager).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "KouChat wifi lock");
    }

    @Test
    public void releaseAllLocksShouldReleaseMulticastLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(false);
        when(wakeLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(multicastLock).release();
        verify(wifiLock, never()).release();
        verify(wakeLock, never()).release();
    }

    @Test
    public void releaseAllLocksShouldReleaseWakeLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(false);
        when(wakeLock.isHeld()).thenReturn(true);

        handler.releaseAllLocks();

        verify(multicastLock, never()).release();
        verify(wifiLock, never()).release();
        verify(wakeLock).release();
    }

    @Test
    public void releaseAllLocksShouldReleaseWifiLockIfItIsHeld() {
        when(multicastLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(true);
        when(wakeLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(multicastLock, never()).release();
        verify(wifiLock).release();
        verify(wakeLock, never()).release();
    }

    @Test
    public void releaseAllLocksShouldReleaseAllLocksIfAllAreHeld() {
        when(multicastLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(true);
        when(wakeLock.isHeld()).thenReturn(true);

        handler.releaseAllLocks();

        verify(multicastLock).release();
        verify(wifiLock).release();
        verify(wakeLock).release();
    }

    @Test
    public void releaseAllLocksShouldNotReleaseAnyLocksIfNoneAreHeld() {
        when(multicastLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(false);
        when(wakeLock.isHeld()).thenReturn(false);

        handler.releaseAllLocks();

        verify(multicastLock, never()).release();
        verify(wifiLock, never()).release();
        verify(wakeLock, never()).release();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireMulticastLockIfItIsNotHeld() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(true);
        when(multicastLock.isHeld()).thenReturn(false);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
        verify(wifiLock, never()).acquire();
        verify(multicastLock).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireWakeLockIfItIsNotHeld() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(true);
        when(multicastLock.isHeld()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(wakeLock).acquire();
        verify(wifiLock, never()).acquire();
        verify(multicastLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireWifiLockIfItIsNotHeld() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(false);
        when(multicastLock.isHeld()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
        verify(wifiLock).acquire();
        verify(multicastLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldAcquireAllLocksIfNoneAreHeld() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(false);
        when(multicastLock.isHeld()).thenReturn(false);

        handler.acquireEnabledLocks();

        verify(wakeLock).acquire();
        verify(wifiLock).acquire();
        verify(multicastLock).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireAnyLocksIfAllAreHeld() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(true);
        when(multicastLock.isHeld()).thenReturn(true);

        handler.acquireEnabledLocks();

        verify(wakeLock, never()).acquire();
        verify(wifiLock, never()).acquire();
        verify(multicastLock, never()).acquire();
    }

    @Test
    public void acquireEnabledLocksShouldNotAcquireWakeLockIfItIsNotHeldAndDisabled() {
        when(wakeLock.isHeld()).thenReturn(false);
        when(settings.isWakeLockEnabled()).thenReturn(false);

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
        when(wakeLock.isHeld()).thenReturn(false);
        when(wifiLock.isHeld()).thenReturn(false);
        when(multicastLock.isHeld()).thenReturn(false);

        handler.beforeNetworkCameUp();

        verify(wakeLock).acquire();
        verify(wifiLock).acquire();
        verify(multicastLock).acquire();
    }

    @Test
    public void networkCameUpShouldDoNothing() {
        handler.networkCameUp(false);

        verifyZeroInteractions(multicastLock, wakeLock, wifiLock);
    }

    @Test
    public void networkWentDownShouldReleaseAllLocks() {
        when(multicastLock.isHeld()).thenReturn(true);
        when(wifiLock.isHeld()).thenReturn(true);
        when(wakeLock.isHeld()).thenReturn(true);

        handler.networkWentDown(false);

        verify(multicastLock).release();
        verify(wifiLock).release();
        verify(wakeLock).release();
    }

    @Test
    public void settingChangedShouldAcquireWakeLockIfItIsChangedToEnabled() {
        when(wakeLock.isHeld()).thenReturn(false);

        handler.settingChanged(AndroidSetting.WAKE_LOCK);

        verify(wakeLock).acquire();
    }

    @Test
    public void settingChangedShouldReleaseWakeLockIfItIsChangedToDisabled() {
        when(wakeLock.isHeld()).thenReturn(true);
        when(settings.isWakeLockEnabled()).thenReturn(false);

        handler.settingChanged(AndroidSetting.WAKE_LOCK);

        verify(wakeLock).release();
    }

    @Test
    public void settingChangedShouldNotCareAboutOtherSettings() {
        handler.settingChanged(Setting.LOGGING);

        verifyZeroInteractions(wakeLock, wifiLock, multicastLock);
    }

    @Test
    public void wifiLockIsHeldShouldReturnValueFromWifiLock() {
        when(wifiLock.isHeld()).thenReturn(true);
        assertTrue(handler.wifiLockIsHeld());

        when(wifiLock.isHeld()).thenReturn(false);
        assertFalse(handler.wifiLockIsHeld());
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
        final AndroidSettings realSettings = new AndroidSettings();
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
