
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

package net.usikkert.kouchat.android.component;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Dialog for accepting or rejecting a file transfer request.
 *
 * @author Christian Ihle
 */
public class ReceiveFileDialog {

    /**
     * Shows a dialog for accepting or rejecting a file transfer request.
     *
     * <p>When the dialog closes, it will also close the owning activity.</p>
     *
     * @param activity The owning activity.
     * @param fileReceiver The file receiver object with details about the file transfer request.
     */
    public void showReceiveFileDialog(final Activity activity, final FileReceiver fileReceiver) {
        Validate.notNull(activity, "Activity can not be null");
        Validate.notNull(fileReceiver, "FileReceiver can not be null");

        final AlertDialog.Builder builder = setupSharedDialogDetails(activity);

        builder.setMessage(activity.getString(R.string.dialog_receive_file_accept_question,
                fileReceiver.getUser().getNick(),
                fileReceiver.getFileName(),
                Tools.byteToString(fileReceiver.getFileSize())));

        builder.setPositiveButton(activity.getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                fileReceiver.accept();
                activity.finish();
            }
        });

        builder.setNegativeButton(activity.getString(R.string.reject), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                fileReceiver.reject();
                activity.finish();
            }
        });

        final AlertDialog receiveFileDialog = builder.show();
        setTextSize(activity, receiveFileDialog);
    }

    /**
     * Shows a dialog notifying that the file transfer request could not be found.
     *
     * <p>When the dialog closes, it will also close the owning activity.</p>
     *
     * @param activity The owning activity.
     */
    public void showMissingFileDialog(final Activity activity) {
        Validate.notNull(activity, "Activity can not be null");

        final AlertDialog.Builder builder = setupSharedDialogDetails(activity);

        builder.setMessage(activity.getString(R.string.dialog_receive_file_not_found));

        builder.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                activity.finish();
            }
        });

        final AlertDialog missingFileDialog = builder.show();
        setTextSize(activity, missingFileDialog);
    }

    private AlertDialog.Builder setupSharedDialogDetails(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.dialog_title_file_transfer_request));
        builder.setIcon(R.drawable.ic_dialog);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                activity.finish();
            }
        });

        return builder;
    }

    private void setTextSize(final Activity activity, final AlertDialog dialog) {
        final TextView messageView = dialog.findViewById(android.R.id.message);
        final Resources resources = activity.getResources();

        messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size));
    }
}
