
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
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * A basic adapter used for creating the list items in the user list. Sorts the users.
 *
 * @author Christian Ihle
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private final UserComparator comparator;

    public UserListAdapter(final Context context) {
        super(context, R.layout.main_chat_user_list_row, R.id.mainChatUserListLabel);

        Validate.notNull(context, "Context can not be null");
        this.comparator = new UserComparator();
    }

    /**
     * Adds a user, and then sorts the list.
     *
     * @param user The user to add.
     */
    @Override
    public void add(final User user) {
        Validate.notNull(user, "User can not be null");

        super.add(user);
        sort();
    }

    /**
     * Just sorts the list.
     */
    public void sort() {
        sort(comparator);
    }

    /**
     * Adds and sorts all the users in the user list.
     *
     * @param userList The list of users to add.
     */
    public void addUsers(final UserList userList) {
        Validate.notNull(userList, "UserList can not be null");

        for (int i = 0; i < userList.size(); i++) {
             add(userList.get(i));
        }
    }

    /**
     * Cleanup to do when the activity using this is destroyed.
     */
    public void onDestroy() {
        clear();
    }
}
