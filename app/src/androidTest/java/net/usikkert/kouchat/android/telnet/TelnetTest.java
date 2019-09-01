
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

package net.usikkert.kouchat.android.telnet;

import net.usikkert.kouchat.testclient.TestClient;

import junit.framework.TestCase;

/**
 * Test that allows remote control of a test client using telnet.
 *
 * <p>Some setup is required in the emulator router first:</p>
 *
 * <ul>
 *   <li>telnet localhost 5554 (telnet to the emulator)</li>
 *   <li>redir add tcp:20000:20000 (adds a forward from the local port 20000 to the port 20000 in the emulator)</li>
 *   <li>exit (disconnects from the emulator)</li>
 * </ul>
 *
 * <p>Alternatively use <code>adb forward tcp:20000 tcp:20000</code>.</p>
 *
 * <p>Then connect to the test client using: <code>telnet localhost 20000</code></p>
 *
 * <p>The test image is available at <code>/mnt/sdcard/kouchat-1600x1600.png</code>.</p>
 *
 * @author Christian Ihle
 */
public class TelnetTest extends TestCase {

    public void test01StartTelnetAndWait() {
        final TestClient client = new TestClient("Kou", 12345678);

        client.logon();
        client.startTelnetServer();
    }
}
