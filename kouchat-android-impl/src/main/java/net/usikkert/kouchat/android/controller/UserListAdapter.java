
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.controller;

import java.util.List;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.misc.User;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The adapter used for creating the list items in the user list.
 *
 * @author Christian Ihle
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private final Drawable envelope;
    private final Drawable dot;

    /**
     * See {@link ArrayAdapter#ArrayAdapter(android.content.Context, int, int, java.util.List)}.
     *
     * @param context The controller.
     * @param resource Id for the row.
     * @param textViewResourceId Id for the label in the row.
     * @param objects The users backing list.
     */
    public UserListAdapter(final Context context, final int resource,
                           final int textViewResourceId, final List<User> objects) {
        super(context, resource, textViewResourceId, objects);

        envelope = context.getResources().getDrawable(R.drawable.envelope);
        dot = context.getResources().getDrawable(R.drawable.dot);
    }

    /**
     * Creates a user item in the user list.
     *
     * <ul>
     *   <li>Shows when a new message has arrived by changing the icon to an envelope.</li>
     *   <li>Shows who you are in the user list by making "you" appear in bold text.</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final LinearLayout linearLayout = (LinearLayout) super.getView(position, convertView, parent);
        final ImageView imageView = (ImageView) linearLayout.getChildAt(0);
        final TextView textView = (TextView) linearLayout.getChildAt(1);
        final User user = getItem(position);

        if (user.isNewPrivMsg()) {
            imageView.setImageDrawable(envelope);
        } else {
            imageView.setImageDrawable(dot);
        }

        if (user.isMe()) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            textView.setTypeface(Typeface.DEFAULT);
        }

        return linearLayout;
    }
}
