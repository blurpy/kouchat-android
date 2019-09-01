
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

import java.util.ArrayList;
import java.util.List;

/**
 * Saves a number of commands in a history list.
 * The current position in the history is marked by a cursor which
 * can be moved up or down to return the previous or next command.
 *
 * @author Christian Ihle
 */
public class CommandHistory {

    /**
     * Defines the max number of commands to save in the history.
     */
    private static final int MAX_COMMANDS = 50;

    /**
     * An enumeration to describe the last direction the user
     * moved in the command history. This is used to correctly
     * synchronize the cursor with the history list.
     */
    private enum Direction {
        UP,
        MIDDLE,
        DOWN
    }

    /** The last direction the user moved in the history. */
    private Direction direction;

    /** The current position in the history. */
    private int cursor;

    /** The list of items in the history. */
    private final List<String> history;

    /**
     * Default constructor.
     */
    public CommandHistory() {
        history = new ArrayList<>();
        direction = Direction.MIDDLE;
    }

    /**
     * Adds a new command to the list, and resets the cursor.
     * The command will only be added if it is not empty, and
     * not identical to the previous command.
     *
     * @param command The command to add to the list.
     */
    public void add(final String command) {
        boolean add = true;

        if (command.trim().length() == 0) {
            add = false;
        } else if (history.size() > 0 && command.equals(history.get(history.size() - 1))) {
            add = false;
        }

        if (add) {
            history.add(command);

            if (history.size() > MAX_COMMANDS) {
                history.remove(0);
            }
        }

        if (history.size() > 0) {
            cursor = history.size() - 1;
        }

        direction = Direction.MIDDLE;
    }

    /**
     * Moves the cursor up in the history list, to find the previous command.
     * If the list is empty, it will return an empty string.
     *
     * @return The previous command.
     */
    public String goUp() {
        String up = "";

        if (history.size() > 0) {
            if (direction != Direction.MIDDLE && cursor > 0) {
                cursor--;
            }

            direction = Direction.UP;
            up = history.get(cursor);
        }

        return up;
    }

    /**
     * Moves the cursor down in the history list, to find the next command.
     * If the list is empty, or at the end, it will return an empty string.
     *
     * @return The next command.
     */
    public String goDown() {
        String down = "";

        if (history.size() > 0) {
            if (cursor < history.size() - 1) {
                cursor++;
                direction = Direction.DOWN;
                down = history.get(cursor);
            }

            else {
                direction = Direction.MIDDLE;
            }
        }

        return down;
    }
}
