
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

package net.usikkert.kouchat.misc;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of {@link Topic}.
 *
 * @author Christian Ihle
 */
public class TopicTest {

    @Test
    public void defaultConstructorShouldSetEverythingToBlankAndZer0() {
        final Topic topic = new Topic();

        assertEquals("", topic.getTopic());
        assertEquals("", topic.getNick());
        assertEquals(0, topic.getTime());
    }

    @Test
    public void constructorShouldSetValuesFromInput() {
        final Topic topic = new Topic("The topic", "You", 100);

        assertEquals("The topic", topic.getTopic());
        assertEquals("You", topic.getNick());
        assertEquals(100, topic.getTime());
    }

    @Test
    public void changeTopicShouldSetValuesFromInput() {
        final Topic topic = new Topic();

        topic.changeTopic("Some topic", "Harry", 200);

        assertEquals("Some topic", topic.getTopic());
        assertEquals("Harry", topic.getNick());
        assertEquals(200, topic.getTime());
    }

    @Test
    public void changeTopicShouldCopyValuesFromAnotherTopic() {
        final Topic topic = new Topic();
        final Topic copyFrom = new Topic("Hey", "Carry", 300);

        topic.changeTopic(copyFrom);

        assertEquals("Hey", topic.getTopic());
        assertEquals("Carry", topic.getNick());
        assertEquals(300, topic.getTime());
    }

    @Test
    public void resetTopicShouldSetEverythingToBlankAndZer0() {
        final Topic topic = new Topic("The topic", "You", 100);

        topic.resetTopic();

        assertEquals("", topic.getTopic());
        assertEquals("", topic.getNick());
        assertEquals(0, topic.getTime());
    }

    @Test
    public void hasTopicShouldBeFalseIfTopicIsEmpty() {
        final Topic topic = new Topic();

        assertFalse(topic.hasTopic());
    }

    @Test
    public void hasTopicShouldBeTrueIfTopicIsSet() {
        final Topic topic = new Topic("Something", "Me", 50);

        assertTrue(topic.hasTopic());
    }

    @Test
    public void toStringShouldIncludeBothTopicAndUser() {
        final Topic topic = new Topic("Nice day today", "Kenny", 50);

        assertEquals("Nice day today (Kenny)", topic.toString());
    }
}
