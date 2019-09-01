
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.FileTransferListener;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.ByteCounter;

import org.jetbrains.annotations.Nullable;

/**
 * This is a class for sending files to other users.
 *
 * <p>To send a file, the user at the other end needs to
 * open a server socket so this client can connect.</p>
 *
 * @author Christian Ihle
 */
public class FileSender implements FileTransfer {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(FileSender.class.getName());

    /** The user to send a file to. */
    private final User user;

    /** The file to send to the user. */
    private final FileToSend file;

    /** The unique ID of this file transfer. */
    private final int id;

    /** Keeps count of the transfer speed. */
    private final ByteCounter bCounter;

    /** Percent of the file transferred. */
    private int percent;

    /** Number of bytes transferred. */
    private long transferred;

    /** If the file was successfully sent. */
    private boolean sent;

    /** If the file transfer is canceled. */
    private boolean cancel;

    /** If still waiting for the file transfer to begin. */
    private boolean waiting;

    /** The file transfer listener. */
    private FileTransferListener listener;

    /** The input stream from the file. */
    @Nullable
    private InputStream is;

    /** The output stream to the other user. */
    @Nullable
    private OutputStream os;

    /** The socket connection to the other user. */
    @Nullable
    private Socket sock;

    /**
     * Constructor. Creates a new file sender.
     *
     * @param user The user to send the file to.
     * @param file The file to send.
     * @param id The unique ID of this file transfer.
     */
    public FileSender(final User user, final FileToSend file, final int id) {
        this.user = user;
        this.file = file;
        this.id = id;

        bCounter = new ByteCounter();
        waiting = true;
    }

    /**
     * Connects to the user at the specified port and transfers the file
     * to that user.
     *
     * @param port The port to use when connecting to the user.
     * @return If the file transfer was successful.
     */
    public boolean transfer(final int port) {
        if (!cancel) {
            listener.statusConnecting();

            waiting = false;
            sent = false;

            try {
                int counter = 0;

                while (sock == null && counter < 10) {
                    counter++;

                    try {
                        sock = new Socket(InetAddress.getByName(user.getIpAddress()), port);
                    }

                    catch (final UnknownHostException e) {
                        LOG.log(Level.SEVERE, e.toString(), e);
                    }

                    catch (final IOException e) {
                        LOG.log(Level.SEVERE, e.toString(), e);
                    }

                    try {
                        Thread.sleep(100);
                    }

                    catch (final InterruptedException e) {
                        LOG.log(Level.SEVERE, e.toString(), e);
                    }
                }

                if (sock != null && !cancel) {
                    listener.statusTransferring();
                    is = file.getInputStream();
                    os = sock.getOutputStream();

                    final byte[] b = new byte[1024];
                    transferred = 0;
                    percent = 0;
                    int tmpTransferred = 0;
                    int tmpPercent = 0;
                    int transCounter = 0;
                    bCounter.prepare();

                    while (!cancel && (tmpTransferred = is.read(b)) != -1) {
                        os.write(b, 0, tmpTransferred);
                        transferred += tmpTransferred;
                        percent = (int) ((transferred * 100) / file.length());
                        bCounter.addBytes(tmpTransferred);
                        transCounter++;

                        if (percent > tmpPercent || transCounter >= 250) {
                            transCounter = 0;
                            tmpPercent = percent;
                            listener.transferUpdate();
                        }
                    }

                    if (!cancel && transferred == file.length()) {
                        sent = true;
                        listener.statusCompleted();
                    }

                    else {
                        listener.statusFailed();
                    }
                }

                else {
                    listener.statusFailed();
                }
            }

            catch (final UnknownHostException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
                listener.statusFailed();
            }

            catch (final IOException e) {
                LOG.log(Level.SEVERE, e.toString());
                listener.statusFailed();
            }

            finally {
                stopSender();
                cleanupConnections();
            }
        }

        return sent;
    }

    /**
     * Sets all connections to null.
     */
    private void cleanupConnections() {
        is = null;
        os = null;
        sock = null;
    }

    /**
     * Closes the connection to the user.
     */
    private void stopSender() {
        try {
            if (is != null) {
                is.close();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (os != null) {
                os.flush();
            }
        }

        catch (final IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        try {
            if (os != null) {
                os.close();
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
        stopSender();
        listener.statusFailed();
    }

    /**
     * Checks if the file transfer is complete.
     *
     * @return If the file transfer is complete.
     */
    @Override
    public boolean isTransferred() {
        return sent;
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
     * The other user, which receives a file.
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
     * Gets the name of the file being transferred.
     *
     * @return The name of the file.
     */
    @Override
    public String getFileName() {
        return file.getName();
    }

    /**
     * Gets the size of the file being transferred, in bytes.
     *
     * @return The file size.
     */
    @Override
    public long getFileSize() {
        return file.length();
    }

    /**
     * Gets the direction, which is send.
     *
     * @return Send, the direction of the file transfer.
     */
    @Override
    public Direction getDirection() {
        return Direction.SEND;
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
     * Gets the file that is being transferred.
     *
     * @return The file.
     */
    public FileToSend getFile() {
        return file;
    }

    /**
     * If still waiting for the file transfer to begin.
     *
     * @return If waiting or not.
     */
    public boolean isWaiting() {
        return waiting;
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
}
