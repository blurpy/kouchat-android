
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

package net.usikkert.kouchat.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.NetworkInterface;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link NetworkInterfaceInfo}.
 *
 * @author Christian Ihle
 */
public class NetworkInterfaceInfoTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private NetworkInterface networkInterface;
    private NetworkInterfaceInfo networkInterfaceInfo;

    @Before
    public void setUp() {
        networkInterface = mock(NetworkInterface.class);
        networkInterfaceInfo = new NetworkInterfaceInfo(networkInterface);
    }

    @Test
    public void constructorShouldThrowExceptionIfNetworkInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Network interface to wrap can not be null");

        new NetworkInterfaceInfo(null);
    }

    @Test
    public void getNetworkInterfaceShouldReturnNetworkInterfaceFromConstructor() {
        assertSame(networkInterface, networkInterfaceInfo.getNetworkInterface());
    }

    @Test
    public void getDisplayNameShouldReturnDisplayNameFromNetworkInterface() {
        when(networkInterface.getDisplayName()).thenReturn("Intel Pro Wireless");

        // verify does not work on final system classes
        assertEquals("Intel Pro Wireless", networkInterfaceInfo.getDisplayName());
    }

    @Test
    public void getNameShouldReturnNameFromNetworkInterface() {
        when(networkInterface.getName()).thenReturn("eth0");

        assertEquals("eth0", networkInterfaceInfo.getName());
    }
}
