
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

package net.usikkert.kouchat.android.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.misc.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test of {@link UserListAdapterWithChatState}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class UserListAdapterWithChatStateTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserListAdapterWithChatState userListAdapter;

    private User user1;
    private User user2;

    private Drawable envelope;
    private Drawable dot;

    @Before
    public void setUp() throws Exception {
        final Context context = Robolectric.application.getApplicationContext();

        userListAdapter = new UserListAdapterWithChatState(context);

        user1 = new User("User1", 1);
        user2 = new User("User2", 2);

        userListAdapter.add(user1);
        userListAdapter.add(user2);

        envelope = context.getResources().getDrawable(R.drawable.envelope);
        dot = context.getResources().getDrawable(R.drawable.dot);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new UserListAdapterWithChatState(null);
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

    @Test
    public void userThatIsWritingShouldHaveStar() {
        user1.setMe(true);

        assertEquals("User1", getDisplayTextForUser(0));
        assertEquals("User2", getDisplayTextForUser(1));

        user1.setWriting(true);

        assertEquals("User1 *", getDisplayTextForUser(0));
        assertEquals("User2", getDisplayTextForUser(1));

        user2.setWriting(true);

        assertEquals("User1 *", getDisplayTextForUser(0));
        assertEquals("User2 *", getDisplayTextForUser(1));

        user1.setWriting(false);
        user2.setWriting(false);

        assertEquals("User1", getDisplayTextForUser(0));
        assertEquals("User2", getDisplayTextForUser(1));
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

    private String getDisplayTextForUser(final int userPosition) {
        final LinearLayout linearLayout = (LinearLayout)  userListAdapter.getView(userPosition, null, null);
        final TextView textView = (TextView) linearLayout.getChildAt(1);

        return textView.getText().toString();
    }
}
