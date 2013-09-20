
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
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

package net.usikkert.kouchat.android.component;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.util.Validate;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Dialog for accepting or rejecting a file transfer request.
 *
 * @author Christian Ihle
 */
public class ReceiveFileDialog {

    public ReceiveFileDialog(final Activity activity, final FileReceiver fileReceiver) {
        Validate.notNull(activity, "Activity can not be null");

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.dialog_title_file_transfer_request));
        builder.show();
    }
}
