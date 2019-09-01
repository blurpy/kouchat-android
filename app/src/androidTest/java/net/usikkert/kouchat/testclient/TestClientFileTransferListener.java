
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

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.net.FileTransfer;

/**
 * A basic file transfer listener that does nothing except register itself.
 *
 * @author Christian Ihle
 */
public class TestClientFileTransferListener implements FileTransferListener {

    private final int fileTransferDelay;

    public TestClientFileTransferListener(final FileTransfer fileTransfer,
                                          final int fileTransferDelay) {
        this.fileTransferDelay = fileTransferDelay;

        fileTransfer.registerListener(this);
    }

    @Override
    public void statusWaiting() {
        if (hasDelay()) {
            sleep(500);
        }
    }

    @Override
    public void statusConnecting() {
        if (hasDelay()) {
            sleep(3000);
        }
    }

    @Override
    public void statusTransferring() {
        if (hasDelay()) {
            sleep(500);
        }
    }

    @Override
    public void statusCompleted() {

    }

    @Override
    public void statusFailed() {

    }

    @Override
    public void transferUpdate() {
        if (hasDelay()) {
            sleep(fileTransferDelay);
        }
    }

    private boolean hasDelay() {
        return fileTransferDelay > 0;
    }

    private void sleep(final int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
