
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

/**
 * This is the class that contains information about the topic of the main chat.
 *
 * @author Christian Ihle
 */
public class Topic {

    /** The current topic. */
    private String topic;

    /** The nick name of the user that last changed the topic. */
    private String nick;

    /** The time when the topic was last changed. */
    private long time;

    /**
     * Constructor.
     *
     * Sets all fields to blank or 0.
     */
    public Topic() {
        resetTopic();
    }

    /**
     * Constructor.
     *
     * Sets all fields from the parameters.
     *
     * @param topic The topic.
     * @param nick The user which set the topic.
     * @param time The time when the topic was set.
     */
    public Topic(final String topic, final String nick, final long time) {
        this.topic = topic;
        this.nick = nick;
        this.time = time;
    }

    /**
     * Changes the topic.
     *
     * Sets all fields from the parameters.
     *
     * @param topic The topic.
     * @param nick The user which set the topic.
     * @param time The time when the topic was set.
     */
    public void changeTopic(final String topic, final String nick, final long time) {
        this.topic = topic;
        this.nick = nick;
        this.time = time;
    }

    /**
     * Changes the current topic (with all fields) to the topic in the parameter.
     *
     * @param topic The topic to set.
     */
    public void changeTopic(final Topic topic) {
        this.topic = topic.getTopic();
        this.nick = topic.getNick();
        this.time = topic.getTime();
    }

    /**
     * Resets all the fields to blank and 0 values.
     */
    public void resetTopic() {
        topic = "";
        nick = "";
        time = 0;
    }

    /**
     * Gets the nick name of the user that last changed the topic.
     *
     * @return The nick name of the user that last changed the topic.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets the time when the topic was last changed.
     *
     * @return The time when the topic was last changed.
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the current topic.
     *
     * @return The current topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * If a topic is currently set.
     *
     * @return If it has a topic.
     */
    public boolean hasTopic() {
        return !topic.isEmpty();
    }

    /**
     * Returns the values in this format: <code>topic (nick)</code>.
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return topic + " (" + nick + ")";
    }
}
