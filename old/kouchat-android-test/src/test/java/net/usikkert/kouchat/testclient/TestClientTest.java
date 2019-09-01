
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

package net.usikkert.kouchat.testclient;

import static org.junit.Assert.*;

import net.usikkert.kouchat.misc.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A very basic test of {@link TestClient}.
 *
 * @author Christian Ihle
 */
public class TestClientTest {

    private TestClient testClient;

    @Before
    public void setUp() {
        testClient = new TestClient();
        testClient.logon();
    }

    @Test
    public void testClientShouldNotThrowExceptionsOnMainChatMessages() {
        testClient.sendChatMessage("Hello");
    }

    @Test
    public void testClientShouldNotThrowExceptionsOnPrivateMessages() {
        final User user = new User("Cookie", 23);
        user.setPrivateChatPort(12345);
        user.setIpAddress("localhost");

        testClient.sendPrivateChatMessage("Hey there", user);
    }

    @Test
    public void testClientWithNickNameAndUserCodeShouldCreateUniqueUserCodeWhenZeroIsSpecified() {
        final TestClient testClient1 = new TestClient("1", 0);
        assertNotEquals(0, testClient1.getUserCode());

        final TestClient testClient2 = new TestClient("2", 0);
        assertNotEquals(0, testClient2.getUserCode());

        assertNotEquals(testClient1.getUserCode(), testClient2.getUserCode());
    }

    @Test
    public void testClientWithNickNameAndUserCodeShouldUseSpecifiedUserCodeIfNotZero() {
        final TestClient testClient1 = new TestClient("1", 1234);
        assertEquals(1234, testClient1.getUserCode());

        final TestClient testClient2 = new TestClient("2", 6789);
        assertEquals(6789, testClient2.getUserCode());
    }

    @Test
    public void testClientWithNickNameAndUserCodeAndColorShouldCreateUniqueUserCodeWhenZeroIsSpecified() {
        final TestClient testClient1 = new TestClient("1", 0, 100);
        assertNotEquals(0, testClient1.getUserCode());

        final TestClient testClient2 = new TestClient("2", 0, 200);
        assertNotEquals(0, testClient2.getUserCode());

        assertNotEquals(testClient1.getUserCode(), testClient2.getUserCode());
    }

    @Test
    public void testClientWithNickNameAndUserCodeAndColorShouldUseSpecifiedUserCodeIfNotZero() {
        final TestClient testClient1 = new TestClient("1", 1234, 100);
        assertEquals(1234, testClient1.getUserCode());

        final TestClient testClient2 = new TestClient("2", 6789, 200);
        assertEquals(6789, testClient2.getUserCode());
    }

    @After
    public void tearDown() {
        testClient.logoff();
    }
}
