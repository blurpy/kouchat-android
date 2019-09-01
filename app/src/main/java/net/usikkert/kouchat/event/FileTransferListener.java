
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

package net.usikkert.kouchat.event;

/**
 * This interface needs to be implemented by the ui to add support
 * for file transfers. The methods here are called when
 * file transfer states change.
 *
 * @author Christian Ihle
 */
public interface FileTransferListener {

    /**
     * Called before anything has happened yet.
     */
    void statusWaiting();

    /**
     * Called when the file transfer process is started, but before
     * connection has been established.
     */
    void statusConnecting();

    /**
     * Called when a file is transferring.
     */
    void statusTransferring();

    /**
     * Called when a file transfer finished successfully.
     */
    void statusCompleted();

    /**
     * Called if for some reason the transfer failed.
     */
    void statusFailed();

    /**
     * Used to notify that more of the transfer has completed.
     */
    void transferUpdate();
}
