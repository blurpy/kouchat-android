
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;
import android.text.method.MovementMethod;

/**
 * Test of {@link LinkMovementMethodWithSelectSupport}.
 *
 * @author Christian Ihle
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class LinkMovementMethodWithSelectSupportTest {

    @Test
    public void getInstanceShouldReturnSingleton() {
        final MovementMethod instance1 = LinkMovementMethodWithSelectSupport.getInstance();
        final MovementMethod instance2 = LinkMovementMethodWithSelectSupport.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @Config(reportSdk = Build.VERSION_CODES.JELLY_BEAN)
    public void canSelectArbitrarilyShouldReturnTrueWhenJellyBean() {
        final MovementMethod instance = LinkMovementMethodWithSelectSupport.getInstance();

        assertTrue(instance.canSelectArbitrarily());
    }

    @Test
    @Config(reportSdk = Build.VERSION_CODES.HONEYCOMB)
    public void canSelectArbitrarilyShouldReturnTrueWhenHoneycomb() {
        final MovementMethod instance = LinkMovementMethodWithSelectSupport.getInstance();

        assertTrue(instance.canSelectArbitrarily());
    }

    @Test
    @Config(reportSdk = Build.VERSION_CODES.GINGERBREAD_MR1)
    public void canSelectArbitrarilyShouldReturnFalseWhenGingerbread() {
        final MovementMethod instance = LinkMovementMethodWithSelectSupport.getInstance();

        assertFalse(instance.canSelectArbitrarily());
    }
}
