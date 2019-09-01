
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
import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;

import org.jetbrains.annotations.Nullable;

/**
 * This class keeps a list of all the ongoing file transfers.
 *
 * @author Christian Ihle
 */
public class TransferList {

    /** The list of all the file senders. */
    private final List<FileSender> senders;

    /** The list of all the file receivers. */
    private final List<FileReceiver> receivers;

    /** Counter for unique file transfer id's. */
    private int fileTransferIdCounter;

    /**
     * Constructor.
     */
    public TransferList() {
        senders = new ArrayList<>();
        receivers = new ArrayList<>();
    }

    /**
     * Adds a new file sender to the list using the following parameters.
     *
     * @param user The user to send the file to.
     * @param file The file to send.
     * @return The file sender object that was added to the transfer list.
     */
    public FileSender addFileSender(final User user, final FileToSend file) {
        final FileSender fileSender = new FileSender(user, file, ++fileTransferIdCounter);
        senders.add(fileSender);

        return fileSender;
    }

    /**
     * Removes a file sender from the list.
     *
     * @param fileSender The file sender to remove.
     */
    public void removeFileSender(final FileSender fileSender) {
        senders.remove(fileSender);
    }

    /**
     * Gets the file sender object for the specified user and file.
     *
     * @param user The file sending user.
     * @param fileName The name of the file being sent.
     * @param fileHash The file's hash code.
     * @return The file sender object, or <code>null</code> if none was found.
     */
    @Nullable
    public FileSender getFileSender(final User user, final String fileName, final int fileHash) {
        FileSender fileSender = null;

        for (final FileSender fs : senders) {
            if (fs.getUser() == user && fs.getFile().getName().equals(fileName) && fs.getFile().hashCode() == fileHash) {
                fileSender = fs;
                break;
            }
        }

        return fileSender;
    }

    /**
     * Gets the file sender object for the specified user and file.
     *
     * @param user The file sending user.
     * @param fileName The name of the file being sent.
     * @return The file sender object, or <code>null</code> if none was found.
     */
    @Nullable
    public FileSender getFileSender(final User user, final String fileName) {
        FileSender fileSender = null;

        for (final FileSender fs : senders) {
            if (fs.getUser() == user && fs.getFile().getName().equals(fileName)) {
                fileSender = fs;
                break;
            }
        }

        return fileSender;
    }

    /**
     * Gets the file sender object for the specified user and file transfer id.
     *
     * @param user The file sending user.
     * @param id The file transfer id of the file being sent.
     * @return The file sender object, or <code>null</code> if none was found.
     */
    @Nullable
    public FileSender getFileSender(final User user, final int id) {
        for (final FileSender fs : senders) {
            if (fs.getUser() == user && fs.getId() == id) {
                return fs;
            }
        }

        return null;
    }

    /**
     * Gets all the file sender objects for a given user.
     *
     * @param user The given user.
     * @return A list of all the file senders for the user.
     */
    public List<FileSender> getFileSenders(final User user) {
        final List<FileSender> list = new ArrayList<>();

        for (final FileSender fs : senders) {
            if (fs.getUser() == user) {
                list.add(fs);
            }
        }

        return list;
    }

    /**
     * Gets all the file sender objects for all the users.
     *
     * @return A list of all the file senders.
     */
    public List<FileSender> getFileSenders() {
        final List<FileSender> list = new ArrayList<>();

        for (final FileSender fs : senders) {
            list.add(fs);
        }

        return list;
    }

    /**
     * Adds a new file receiver to the list using the following parameters.
     *
     * @param user The user which sends the file.
     * @param file The file the user is sending.
     * @param size The size of the file, in bytes.
     * @return The file receiver object that was added to the transfer list.
     */
    public FileReceiver addFileReceiver(final User user, final File file, final long size) {
        final FileReceiver fileReceiver = new FileReceiver(user, file, size, ++fileTransferIdCounter);
        receivers.add(fileReceiver);

        return fileReceiver;
    }

    /**
     * Removes a file receiver from the list.
     *
     * @param fileReceiver The file receiver to remove.
     */
    public void removeFileReceiver(final FileReceiver fileReceiver) {
        receivers.remove(fileReceiver);
    }

    /**
     * Gets all the file receiver objects for a given user.
     *
     * @param user The given user.
     * @return A list of all the file receivers for the user.
     */
    public List<FileReceiver> getFileReceivers(final User user) {
        final List<FileReceiver> list = new ArrayList<>();

        for (final FileReceiver fr : receivers) {
            if (fr.getUser() == user) {
                list.add(fr);
            }
        }

        return list;
    }

    /**
     * Gets the file receiver object for the specified user and file.
     *
     * @param user The file receiver user.
     * @param fileName The name of the file being received.
     * @return The file receiver object, or <code>null</code> if none was found.
     */
    @Nullable
    public FileReceiver getFileReceiver(final User user, final String fileName) {
        FileReceiver fileReceiver = null;

        for (final FileReceiver fr : receivers) {
            if (fr.getUser() == user && fr.getFile().getName().equals(fileName)) {
                fileReceiver = fr;
                break;
            }
        }

        return fileReceiver;
    }

    /**
     * Gets the file receiver object for the specified user and file transfer id.
     *
     * @param user The file receiver user.
     * @param id TThe file transfer id of the file being received.
     * @return The file receiver object, or <code>null</code> if none was found.
     */
    @Nullable
    public FileReceiver getFileReceiver(final User user, final int id) {
        for (final FileReceiver fr : receivers) {
            if (fr.getUser() == user && fr.getId() == id) {
                return fr;
            }
        }

        return null;
    }

    /**
     * Gets all the file receiver objects for all the users.
     *
     * @return A list of all the file receivers.
     */
    public List<FileReceiver> getFileReceivers() {
        final List<FileReceiver> list = new ArrayList<>();

        for (final FileReceiver fr : receivers) {
            list.add(fr);
        }

        return list;
    }

    /**
     * Gets a file transfer object for the given user and file.
     *
     * @param user The user to find a file transfer for.
     * @param fileName The filename to look for.
     * @return Either a file receiver, a file sender, or <code>null</code>
     * if none of them was found.
     */
    @Nullable
    public FileTransfer getFileTransfer(final User user, final String fileName) {
        final FileReceiver fileReceiver = getFileReceiver(user, fileName);

        if (fileReceiver != null) {
            return fileReceiver;
        } else {
            return getFileSender(user, fileName);
        }
    }

    /**
     * Gets the file transfer object for the specified user and file transfer id.
     *
     * @param user The user to find a file transfer for.
     * @param id The file transfer id of the file to look for.
     * @return Either a file receiver, a file sender, or <code>null</code>
     * if none of them was found.
     */
    @Nullable
    public FileTransfer getFileTransfer(final User user, final int id) {
        final FileReceiver fileReceiver = getFileReceiver(user, id);

        if (fileReceiver != null) {
            return fileReceiver;
        } else {
            return getFileSender(user, id);
        }
    }
}
