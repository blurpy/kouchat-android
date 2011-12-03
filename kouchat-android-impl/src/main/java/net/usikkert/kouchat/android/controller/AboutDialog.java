
/***************************************************************************
 *   Copyright 2006-2011 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU General Public License as               *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with KouChat. If not, see <http://www.gnu.org/licenses/>.       *
 ***************************************************************************/

package net.usikkert.kouchat.android.controller;

import net.usikkert.kouchat.android.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Creates an "About" dialog with copyright information and links.
 *
 * @author Christian Ihle
 */
public class AboutDialog {

    private static final int PADDING = 5;

    /**
     * Creates and shows the about dialog.
     *
     * @param context The activity to create this dialog from.
     */
    public AboutDialog(final Context context) {
        final PackageInfo packageInfo = getPackageInfo(context);

        final String appVersion = packageInfo.versionName;
        final String appName = context.getString(R.string.app_name);

        final String aboutTitle = appName + " v" + appVersion;
        final String aboutText = context.getString(R.string.about_text);
        final TextView message = createTextView(context, aboutText);

        buildDialog(context, aboutTitle, message);
    }

    private void buildDialog(final Context context, final String aboutTitle, final TextView message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(aboutTitle);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.kou_icon_32x32);
        builder.setPositiveButton(context.getString(android.R.string.ok), null);
        builder.setView(message);
        builder.create();

        builder.show();
    }

    private TextView createTextView(final Context context, final String aboutText) {
        final TextView message = new TextView(context);
        final SpannableString s = new SpannableString(aboutText);

        message.setPadding(PADDING, PADDING, PADDING, PADDING);
        message.setText(s);
        Linkify.addLinks(message, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);

        return message;
    }

    private PackageInfo getPackageInfo(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        }

        catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
