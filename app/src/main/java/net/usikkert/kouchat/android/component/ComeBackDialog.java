
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
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Creates a dialog for coming back from away.
 *
 * @author Christian Ihle
 */
public class ComeBackDialog {

    public ComeBackDialog(final Context context, final AndroidUserInterface androidUserInterface) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");

        final LayoutInflater inflater = LayoutInflater.from(context);

        final View comeBackDialog = inflater.inflate(R.layout.come_back_dialog, null);
        final TextView comeBackDialogMessage = comeBackDialog.findViewById(R.id.comeBackDialogMessage);

        setComeBackMessage(androidUserInterface, context, comeBackDialogMessage);

        final AlertDialog alertDialog = createComeBackDialog(androidUserInterface, context, comeBackDialog);

        alertDialog.show();
    }

    private void setComeBackMessage(final AndroidUserInterface ui, final Context context, final TextView comeBackDialogMessage) {
        final String awayMessage = ui.getMe().getAwayMsg();
        final String comeBackMessage = context.getString(R.string.come_back_dialog_message, awayMessage);

        comeBackDialogMessage.setText(comeBackMessage);
    }

    private AlertDialog createComeBackDialog(final AndroidUserInterface ui, final Context context, final View comeBackDialog) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(comeBackDialog);
        builder.setTitle(R.string.away);
        builder.setIcon(R.drawable.ic_dialog);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                ui.comeBack();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }
}
