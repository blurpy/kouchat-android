
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

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;

/**
 * A user list adapter that hides "me".
 *
 * <p>Expects that "me" is in the list at all times, or else it will misbehave.</p>
 *
 * @author Christian Ihle
 */
public class UserListAdapterWithoutMe extends UserListAdapter {

    private User me;

    public UserListAdapterWithoutMe(final Context context, final User me) {
        super(context);

        Validate.notNull(context, "Context can not be null");
        Validate.notNull(me, "Me can not be null");

        this.me = me;
    }

    @Override
    public User getItem(final int position) {
        final int myPosition = getPosition(me);

        if (position >= myPosition) {
            return super.getItem(position + 1);
        }

        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount() - 1;
    }

    /**
     * Cleanup to avoid memory leak after sending files.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        me = null;
    }
}
