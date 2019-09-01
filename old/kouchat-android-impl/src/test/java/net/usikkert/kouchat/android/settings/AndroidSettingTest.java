
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

package net.usikkert.kouchat.android.settings;

import static org.junit.Assert.*;

import net.usikkert.kouchat.settings.Setting;

import org.junit.Test;

/**
 * Test of {@link AndroidSetting}.
 *
 * @author Christian Ihle
 */
public class AndroidSettingTest {

    @Test
    public void equalsAndHashCodeShouldBeTrueIfSameName() {
        final Setting one = new AndroidSetting("TEST1");
        final Setting two = new AndroidSetting("TEST1");

        assertEquals(one, two);
        assertEquals(two, one);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void equalsAndHashCodeShouldBeFalseIfDifferentName() {
        final Setting one = new AndroidSetting("TEST1");
        final Setting two = new AndroidSetting("TEST2");

        assertNotEquals(one, two);
        assertNotEquals(two, one);
        assertNotEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void equalsShouldBeFalseForInheritingClass() {
        final Setting one = new AndroidSetting("TEST1");
        final Setting differentClass = new AndroidSetting("TEST1") { };

        assertNotEquals(one, differentClass);
    }

    @Test
    public void wakeLockShouldWorkWithEquals() {
        assertEquals(AndroidSetting.WAKE_LOCK, AndroidSetting.WAKE_LOCK);
    }

    @Test
    public void wakeLockShouldNotBeEqualToOtherSetting() {
        assertNotEquals(AndroidSetting.WAKE_LOCK, Setting.LOGGING);
    }
}
