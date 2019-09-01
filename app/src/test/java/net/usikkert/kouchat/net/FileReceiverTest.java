
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

import static org.junit.Assert.*;

import java.io.File;

import net.usikkert.kouchat.misc.User;

import org.junit.Test;

/**
 * Test of {@link FileReceiver}.
 *
 * @author Christian Ihle
 */
public class FileReceiverTest {

    @Test
    public void getOriginalFileNameShouldReturnTheNameOfTheFileFromTheConstructorEvenAfterChange() {
        final FileReceiver fileReceiver = new FileReceiver(new User("Test", 123), new File("cows.gif"), 100, 1);

        assertEquals("cows.gif", fileReceiver.getOriginalFileName());
        assertEquals("cows.gif", fileReceiver.getFileName());
        assertEquals("cows.gif", fileReceiver.getFile().getName());

        fileReceiver.setFile(new File("chickens.jpg"));

        assertEquals("cows.gif", fileReceiver.getOriginalFileName());
        assertEquals("chickens.jpg", fileReceiver.getFileName());
        assertEquals("chickens.jpg", fileReceiver.getFile().getName());
    }
}
