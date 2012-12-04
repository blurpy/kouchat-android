
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

import static org.junit.Assert.*;

import java.util.ArrayList;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Test of {@link UserListAdapter}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class UserListAdapterTest {

    private UserListAdapter userListAdapter;

    private User user1;
    private User user2;

    private Drawable envelope;
    private Drawable dot;

    @Before
    public void setUp() throws Exception {
        final Context context = new MainChatController();

        userListAdapter = new UserListAdapter(
                context, R.layout.main_chat_user_list_row, R.id.mainChatUserListLabel, new ArrayList<User>());

        user1 = new User("User1", 1);
        user2 = new User("User2", 2);

        userListAdapter.add(user1);
        userListAdapter.add(user2);

        envelope = context.getResources().getDrawable(R.drawable.envelope);
        dot = context.getResources().getDrawable(R.drawable.dot);
    }

    @Test
    public void shouldShowEnvelopeOnlyWhenNewPrivateMessage() {
        assertEquals(dot, getCurrentUserImage(0));

        user1.setNewPrivMsg(true);
        assertEquals(envelope, getCurrentUserImage(0));

        user1.setNewPrivMsg(false);
        assertEquals(dot, getCurrentUserImage(0));
    }

    @Test
    public void shouldOnlyShowEnvelopeForUserWithNewPrivateMessage() {
        assertEquals(dot, getCurrentUserImage(0));
        assertEquals(dot, getCurrentUserImage(1));

        user2.setNewPrivMsg(true);

        assertEquals(dot, getCurrentUserImage(0));
        assertEquals(envelope, getCurrentUserImage(1));
    }

    @Test
    @Ignore("This does not work with Robolectric yet.")
    public void meShouldBeBold() {
        user1.setMe(true);

        assertTrue(userIsBold(0));
        assertFalse(userIsBold(1));
    }

    private boolean userIsBold(final int userPosition) {
        final LinearLayout linearLayout = (LinearLayout)  userListAdapter.getView(userPosition, null, null);
        final TextView textView = (TextView) linearLayout.getChildAt(1);
        final Typeface typeface = textView.getTypeface();

        return typeface != null && typeface.isBold();
    }

    private Drawable getCurrentUserImage(final int userPosition) {
        final LinearLayout linearLayout = (LinearLayout)  userListAdapter.getView(userPosition, null, null);

        return ((ImageView) linearLayout.getChildAt(0)).getDrawable();
    }
}
