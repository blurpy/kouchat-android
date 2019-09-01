
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

package net.usikkert.kouchat.settings;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of {@link Setting}.
 *
 * @author Christian Ihle
 */
public class SettingTest {

    @Test
    public void equalsAndHashCodeShouldBeTrueIfSameInstance() {
        final Setting one = new Setting("TEST1");

        assertEquals(one, one);
        assertEquals(one.hashCode(), one.hashCode());
    }

    @Test
    public void equalsAndHashCodeShouldBeTrueIfSameName() {
        final Setting one = new Setting("TEST1");
        final Setting two = new Setting("TEST1");

        assertEquals(one, two);
        assertEquals(two, one);
        assertEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void equalsAndHashCodeShouldBeFalseIfDifferentName() {
        final Setting one = new Setting("TEST1");
        final Setting two = new Setting("TEST2");

        assertNotEquals(one, two);
        assertNotEquals(two, one);
        assertNotEquals(one.hashCode(), two.hashCode());
    }

    @Test
    public void equalsShouldBeFalseForNull() {
        final Setting one = new Setting("TEST1");

        assertNotEquals(one, null);
    }

    @Test
    public void equalsShouldBeFalseForDifferentClass() {
        final Setting one = new Setting("TEST1");

        assertNotEquals(one, 1);
    }

    @Test
    public void equalsShouldBeFalseForInheritingClass() {
        final Setting one = new Setting("TEST1");
        final Setting differentClass = new Setting("TEST1") { };

        assertNotEquals(one, differentClass);
    }

    @Test
    public void loggingShouldWorkWithEquals() {
        assertEquals(Setting.LOGGING, Setting.LOGGING);
    }
}
