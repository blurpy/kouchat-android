
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

import static org.junit.Assert.*;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.junit.ExpectedException;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        final Context context = Robolectric.application;

        userListAdapter = new UserListAdapterWithChatState(context);

        user1 = new User("User1", 1);
        user2 = new User("User2", 2);

        userListAdapter.add(user1);
        userListAdapter.add(user2);

        envelope = context.getResources().getDrawable(R.drawable.ic_envelope);
        dot = context.getResources().getDrawable(R.drawable.ic_dot);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(NullPointerException.class); // Happens in Android superclass

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

    @Test
    public void userThatIsAwayShouldBeDisabled() {
        assertTrue(getTextViewForUser(0).isEnabled());
        assertTrue(getTextViewForUser(1).isEnabled());

        user1.setAway(true);

        assertFalse(getTextViewForUser(0).isEnabled());
        assertTrue(getTextViewForUser(1).isEnabled());

        user2.setAway(true);

        assertFalse(getTextViewForUser(0).isEnabled());
        assertFalse(getTextViewForUser(1).isEnabled());

        user1.setAway(false);
        user2.setAway(false);

        assertTrue(getTextViewForUser(0).isEnabled());
        assertTrue(getTextViewForUser(1).isEnabled());
    }

    @Test
    public void onDestroyShouldClearTheList() {
        assertEquals(2, userListAdapter.getCount());

        userListAdapter.onDestroy();

        assertEquals(0, userListAdapter.getCount());
    }

    @Test
    public void onDestroyShouldSetAllFieldsToNull() {
        assertTrue(TestUtils.allFieldsHaveValue(userListAdapter));

        userListAdapter.onDestroy();

        assertTrue(TestUtils.allFieldsAreNull(userListAdapter));
    }

    private boolean userIsBold(final int userPosition) {
        final TextView textView = getTextViewForUser(userPosition);
        final Typeface typeface = textView.getTypeface();

        return typeface != null && typeface.isBold();
    }

    private Drawable getCurrentUserImage(final int userPosition) {
        final LinearLayout linearLayout = (LinearLayout)  userListAdapter.getView(userPosition, null, null);

        return ((ImageView) linearLayout.getChildAt(0)).getDrawable();
    }

    private String getDisplayTextForUser(final int userPosition) {
        final TextView textView = getTextViewForUser(userPosition);

        return textView.getText().toString();
    }

    private TextView getTextViewForUser(final int userPosition) {
        final LinearLayout linearLayout = (LinearLayout)  userListAdapter.getView(userPosition, null, null);

        return (TextView) linearLayout.getChildAt(1);
    }
}
