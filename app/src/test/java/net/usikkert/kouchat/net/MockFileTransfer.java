
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

import java.io.File;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a mock implementation of the file transfer class, for use in unit tests.
 * It will simulate what a real file transfer behaves like, without touching any
 * physical file.
 *
 * @author Christian Ihle
 */
public class MockFileTransfer implements FileTransfer {

    private final Direction direction;
    private final File file;
    private final User user;

    private FileTransferListener listener;
    private int percent;
    private long transferred;
    private boolean cancel;

    /**
     * Constructor.
     *
     * @param direction If this mock should send or receive the file.
     */
    public MockFileTransfer(final Direction direction) {
        Validate.notNull(direction, "Direction can not be null");
        this.direction = direction;

        user = new User("TestUser", 1234);
        user.setIpAddress("192.168.1.1");

        file = new File("test/this_is_a_fake_test_file_with_a_very_very_long_file_name.txt");
    }

    /**
     * Aborts the file transfer simulation.
     *
     * {@inheritDoc}
     */
    @Override
    public void cancel() {
        cancel = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return file.getName();
    }

    /**
     * Returns ~500KB.
     *
     * @return 500000.
     */
    @Override
    public long getFileSize() {
        return 500000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPercent() {
        return percent;
    }

    /**
     * Returns ~100K/s.
     *
     * @return 100000;
     */
    @Override
    public long getSpeed() {
        return 100000;
    }

    /**
     * Returns 1.
     *
     * @return 1;
     */
    @Override
    public int getId() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTransferred() {
        return transferred;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCanceled() {
        return cancel;
    }

    /**
     * Returns false.
     *
     * @return false.
     */
    @Override
    public boolean isTransferred() {
        return false;
    }

    /**
     * Registers the listener, and starts the thread which simulates the file transfer.
     *
     * @param listener The listener to register.
     */
    @Override
    public void registerListener(final FileTransferListener listener) {
        this.listener = listener;
        new MockTransferThread().start();
    }

    /**
     * This thread simulates the transfer process.
     *
     * @author Christian Ihle
     */
    private class MockTransferThread extends Thread {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                sleep(500);
                listener.statusWaiting();
                sleep(500);
                listener.statusConnecting();
                sleep(1000);
                listener.statusTransferring();
                sleep(500);

                while (!cancel && transferred < getFileSize()) {
                    transferred += 10000;
                    percent += 2;
                    sleep(50);
                    listener.transferUpdate();
                }

                if (cancel) {
                    listener.statusFailed();
                } else {
                    listener.statusCompleted();
                }
            }

            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
