
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

package net.usikkert.kouchat.android.userlist;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The adapter used for creating the list items in the user list. The items in the list react to chat state.
 *
 * @author Christian Ihle
 */
public class UserListAdapterWithChatState extends UserListAdapter {

    private Drawable envelope;
    private Drawable dot;

    /**
     * Constructor.
     *
     * @param context The controller.
     */
    public UserListAdapterWithChatState(final Context context) {
        super(context);

        Validate.notNull(context, "Context can not be null");

        envelope = context.getResources().getDrawable(R.drawable.ic_envelope);
        dot = context.getResources().getDrawable(R.drawable.ic_dot);
    }

    /**
     * Creates the view that displays the user at the given position in the user list
     * according to the rules defined below.
     *
     * <ul>
     *   <li>Shows when a new message has arrived by changing the icon to an envelope.</li>
     *   <li>Shows who you are in the user list by making "you" appear in bold text.</li>
     *   <li>Shows who is currently writing by appending a <code>*</code> after the nick name.</li>
     *   <li>Shows who is currently away by making the text gray (disabled).</li>
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

        showIfNewPrivateMessage(imageView, user);
        showIfMe(textView, user);
        showIfCurrentlyWriting(textView, user);
        showIfAway(textView, user);

        return linearLayout;
    }

    /**
     * Removing the icons to avoid memory leaks. Drawables contain references to the context that created
     * them, like an activity.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        envelope = null;
        dot = null;
    }

    private void showIfNewPrivateMessage(final ImageView imageView, final User user) {
        if (user.isNewPrivMsg()) {
            imageView.setImageDrawable(envelope);
        } else {
            imageView.setImageDrawable(dot);
        }
    }

    private void showIfMe(final TextView textView, final User user) {
        if (user.isMe()) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            textView.setTypeface(Typeface.DEFAULT);
        }
    }

    private void showIfCurrentlyWriting(final TextView textView, final User user) {
        if (user.isWriting()) {
            textView.setText(user.getNick() + " *");
        } else {
            textView.setText(user.getNick());
        }
    }

    private void showIfAway(final TextView textView, final User user) {
        if (user.isAway()) {
            textView.setEnabled(false);
        } else {
            textView.setEnabled(true);
        }
    }
}
