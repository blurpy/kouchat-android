
/***************************************************************************
 *   Copyright 2006-2014 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.util;

/**
 * Class to represent a line of text.
 *
 * @author Christian Ihle
 */
public class Line {

    private final Integer lineNumber;
    private final String lineText;

    public Line(final Integer lineNumber, final String lineText) {
        this.lineNumber = lineNumber;
        this.lineText = lineText;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getLineText() {
        return lineText;
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineNumber=" + lineNumber +
                ", lineText=" + lineText +
                '}';
    }
}
