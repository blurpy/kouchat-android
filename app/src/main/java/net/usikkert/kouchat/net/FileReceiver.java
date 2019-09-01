
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.ByteCounter;

import org.jetbrains.annotations.Nullable;

/**
 * This is a class for receiving files from other users.
 *
 * <p>To receive a file, a server socket has to be opened,
 * to wait for incoming transfers.</p>
 *
 * @author Christian Ihle
 */
public class FileReceiver implements FileTransfer {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(FileReceiver.class.getName());

    /** The user sending the file. */
    private final User user;

    /** The file size in bytes. */
    private final long size;

    /** The file from the user. */
    private File file;

    /** The original file name. */
    private final String originalFileName;

    /** The unique ID of this file transfer. */
    private final int id;

    /** Keeps count of the transfer speed. */
    private final ByteCounter bCounter;

    /** Percent of the file received. */
    private int percent;

    /** Number of bytes received. */
    private long transferred;

    /** If the file was successfully received. */
    private boolean received;

    /** If the file transfer is canceled. */
    private boolean cancel;

    /** If the client has accepted to receive the file. */
    private boolean accepted;

    /** If the client has rejected the file. */
    private boolean rejected;

    /** The file transfer listener. */
    private FileTransferListener listener;

    /** The server socket waiting for an incoming connection. */
    @Nullable
    private ServerSocket sSock;

    /** The socket connection to the other user. */
    @Nullable
    private Socket sock;

    /** The output stream to the file. */
    @Nullable
    private FileOutputStream fos;

    /** The input stream from the other user. */
    @Nullable
    private InputStream is;

    /**
     * Constructor. Creates a new file receiver.
     *
     * @param user The user which sends the file.
     * @param file The file the user is sending.
     * @param size The size of the file, in bytes.
     * @param id The unique ID of this file transfer.
     */
    public FileReceiver(final User user, final File file, final long size, final int id) {
        this.user = user;
        this.file = file;
        this.size = size;
        this.id = id;
        this.originalFileName = file.getName();

        bCounter = new ByteCounter();
    }

    /**
     * Starts a server connection which the sender can use to connect
     * for transferring the file, and returns the opened port.
     *
     * @return The port which the sender can connect to.
     * @throws ServerException If the server could not be started.
     */
    public int startServer() throws ServerException {
        int port = Constants.NETWORK_FILE_TRANSFER_PORT;
        boolean done = false;
        int counter = 0;

        while (!done && counter < 50) {
            try {
                sSock = new ServerSocket(port);
                final TimeoutThread tt = new TimeoutThread();
                tt.start();
                done = true;
            }

            catch (final IOException e) {
                LOG.log(Level.WARNING, "Could not open " + port, e);
                port++;
            }

            finally {
                counter++;
            }
        }

        if (!done) {
            throw new ServerException("Could not start server");
        }

        return port;
    }

    /**
     * Waits for an incoming connection, then receives the
     * file from the other user.
     *
     * @return If the file transfer was successful.
     */
    public boolean transfer() {
        listener.statusConnecting();

        received = false;
        cancel = false;

        try {
            if (sSock != null) {
                sock = sSock.accept();
                listener.statusTransferring();
                fos = new FileOutputStream(file);
                is = sock.getInputStream();

                final byte[] b = new byte[1024];
                transferred = 0;
                percent = 0;
                int tmpTransferred = 0;
                int tmpPercent = 0;
                int transCounter = 0;
                bCounter.prepare();

                while (!cancel && (tmpTransferred = is.read(b)) != -1) {
                    fos.write(b, 0, tmpTransferred);
                    transferred += tmpTransferred;
                    percent = (int) ((transferred * 100) / size);
                    bCounter.addBytes(tmpTransferred);
                    transCounter++;

                    if (percent > tmpPercent || transCounter >= 250) {
                        transCounter = 0;
                        tmpPercent = percent;
                        listener.transferUpdate();
                    }
                }

                if (!cancel && transferred == size) {
                    received = true;
                    listener.statusCompleted();
                }

                else {
                    listener.statusFailed();
                }
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString());
            listener.statusFailed();
        }

        finally {
            stopReceiver();
            cleanupConnections();
        }

        return received;
    }

    /**
     * Sets all connections to null.
     */
    private void cleanupConnections() {
        is = null;
        fos = null;
        sock = null;
        sSock = null;
    }

    /**
     * Closes the connection to the user.
     */
    private void stopReceiver() {
        try {
            if (is != null) {
                is.close();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (fos != null) {
                fos.flush();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (fos != null) {
                fos.close();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (sock != null) {
                sock.close();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (sSock != null) {
                sSock.close();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Checks if the file transfer has been canceled.
     *
     * @return If the file transfer has been canceled.
     */
    @Override
    public boolean isCanceled() {
        return cancel;
    }

    /**
     * Cancels the file transfer.
     */
    @Override
    public void cancel() {
        cancel = true;
        stopReceiver();

        if (listener != null) {
            listener.statusFailed();
        }
    }

    /**
     * The percent of the file transfer that is completed.
     *
     * @return Percent completed.
     */
    @Override
    public int getPercent() {
        return percent;
    }

    /**
     * Checks if the file transfer is complete.
     *
     * @return If the file transfer is complete.
     */
    @Override
    public boolean isTransferred() {
        return received;
    }

    /**
     * Gets the file that is being transferred.
     *
     * @return The file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Changes the file to save to.
     *
     * @param file The new file to save to.
     */
    public void setFile(final File file) {
        this.file = file;
    }

    /**
     * Gets the file name of the original file from the other user.
     * As opposed to the current file which might have been renamed.
     *
     * @return The original file name.
     */
    public String getOriginalFileName() {
        return originalFileName;
    }

    /**
     * The other user, which sends a file.
     *
     * @return The other user.
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * Number of bytes transferred.
     *
     * @return Bytes transferred.
     */
    @Override
    public long getTransferred() {
        return transferred;
    }

    /**
     * Gets the size of the file being transferred, in bytes.
     *
     * @return The file size.
     */
    @Override
    public long getFileSize() {
        return size;
    }

    /**
     * Gets the name of the file being transferred.
     *
     * @return The name of the file.
     */
    @Override
    public String getFileName() {
        return file.getName();
    }

    /**
     * Gets the direction, which is receive.
     *
     * @return Receive, the direction of the file transfer.
     */
    @Override
    public Direction getDirection() {
        return Direction.RECEIVE;
    }

    /**
     * Gets the number of bytes transferred per second.
     *
     * @return The speed in bytes per second.
     */
    @Override
    public long getSpeed() {
        return bCounter.getBytesPerSec();
    }

    /**
     * Gets the ID of this file transfer. The ID is unique during the session, and starts with 1.
     *
     * @return The unique ID of this file transfer.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Registers a file transfer listener, which will receive updates
     * when certain events happen in the progression of the file transfer.
     *
     * @param listener The listener to register.
     */
    @Override
    public void registerListener(final FileTransferListener listener) {
        this.listener = listener;
        listener.statusWaiting();
    }

    /**
     * If the client has accepted to receive the file.
     *
     * @return If the file is accepted.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Accept the file transfer.
     */
    public void accept() {
        accepted = true;
    }

    /**
     * If the client has rejected the file.
     *
     * @return If the file is rejected.
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * Reject the file transfer.
     */
    public void reject() {
        rejected = true;
    }

    /**
     * A thread for closing the server connection if no client
     * has connected within 15 seconds.
     *
     * <p>This does not mean that the user only has 15 seconds to decide
     * where to save the file. This timer is started after the user has
     * decided, and waits for an automated response from the sender.
     * If nothing has happened to the sender, the response should be very quick.</p>
     */
    private class TimeoutThread extends Thread {
        /**
         * Constructor. Sets the name of the thread.
         */
        TimeoutThread() {
            setName("TimeoutThread");
        }

        /**
         * The thread. Sleeps for 15 seconds, and then closes the
         * server connection if it is not already closed.
         */
        @Override
        public void run() {
            try {
                sleep(15000);
            }

            catch (final InterruptedException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }

            try {
                if (sSock != null) {
                    sSock.close();
                    sSock = null;
                }
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
        }
    }
}
