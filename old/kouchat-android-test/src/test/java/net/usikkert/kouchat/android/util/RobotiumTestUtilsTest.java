
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

package net.usikkert.kouchat.android.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test of {@link RobotiumTestUtils}.
 *
 * @author Christian Ihle
 */
public class RobotiumTestUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getMatchingLinesOfTextShouldFindCorrectLinesWhenBetweenOtherLines() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "something http://kouchat.googlecode.com here",
                Arrays.asList(
                        "something ",
                        "http://",
                        "kouchat.googlecode",
                        ".com",
                        " here"),
                "http://kouchat.googlecode.com");

        assertEquals(3, lines.size());

        correctLineContent(lines.get(0), 1, "http://");
        correctLineContent(lines.get(1), 2, "kouchat.googlecode");
        correctLineContent(lines.get(2), 3, ".com");
    }

    @Test
    public void getMatchingLinesOfTextShouldThrowExceptionIfAllWordsAreThereButInWrongOrder() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not find: http://kouchat.googlecode.com");

        RobotiumTestUtils.getMatchingLinesOfText(
                "http://googlecode.kouchat.com",
                Arrays.asList(
                        "http://",
                        "googlecode.kouchat",
                        ".com"),
                "http://kouchat.googlecode.com");
    }

    @Test
    public void getMatchingLinesOfTextShouldThrowExceptionIfAllWordsAreThereButExtraWordInTheMiddle() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not find: http://kouchat.googlecode.com");

        RobotiumTestUtils.getMatchingLinesOfText(
                "http://kouchat.googlecode something .com",
                Arrays.asList(
                        "http://",
                        "kouchat.googlecode",
                        " something ",
                        ".com"),
                "http://kouchat.googlecode.com");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindTheLinesInSequence() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "http://kouchat.googlecode http://kouchat.googlecode.com",
                Arrays.asList(
                        "http://",
                        "kouchat.googlecode ",
                        "http://",
                        "kouchat.googlecode",
                        ".com"),
                "http://kouchat.googlecode.com");

        assertEquals(3, lines.size());

        correctLineContent(lines.get(0), 2, "http://"); // Not line 0
        correctLineContent(lines.get(1), 3, "kouchat.googlecode"); // Not line 1
        correctLineContent(lines.get(2), 4, ".com");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindTheLastOccurrence() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "http://kouchat.googlecode.com http://kouchat.googlecode.com",
                Arrays.asList(
                        "http://",
                        "kouchat.googlecode",
                        ".com ",
                        "http://",
                        "kouchat.googlecode",
                        ".com"),
                "http://kouchat.googlecode.com");

        assertEquals(3, lines.size());

        correctLineContent(lines.get(0), 3, "http://");
        correctLineContent(lines.get(1), 4, "kouchat.googlecode");
        correctLineContent(lines.get(2), 5, ".com");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindCorrectLinesWhenFirstAndLastLinesHaveExtraText() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "[17:02:41] *** Tina aborted reception of kouchat-512x512.png ***",
                Arrays.asList(
                        "[17:02:41] *** Tina aborted ",
                        "reception of ",
                        "kouchat-512x512.png ***"),
                "Tina aborted reception of kouchat-512x512.png");

        assertEquals(3, lines.size());

        correctLineContent(lines.get(0), 0, "Tina aborted ");
        correctLineContent(lines.get(1), 1, "reception of ");
        correctLineContent(lines.get(2), 2, "kouchat-512x512.png");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindCorrectLinesEvenWhenSpreadOverManyLines() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "[18:47:32] *** Tina aborted reception of kouchat-512x512.png ***",
                Arrays.asList(
                        "[18:47:32] *** ",
                        "Tina ",
                        "aborted",
                        " reception ",
                        "of ",
                        "kouchat",
                        "-512x512",
                        ".png",
                        " ***"),
                "Tina aborted reception of kouchat-512x512.png");

        assertEquals(7, lines.size());

        correctLineContent(lines.get(0), 1, "Tina ");
        correctLineContent(lines.get(1), 2, "aborted");
        correctLineContent(lines.get(2), 3, " reception ");
        correctLineContent(lines.get(3), 4, "of ");
        correctLineContent(lines.get(4), 5, "kouchat");
        correctLineContent(lines.get(5), 6, "-512x512");
        correctLineContent(lines.get(6), 7, ".png");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindCorrectLineWhenAllOnOneLine() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "Tina aborted reception of kouchat-512x512.png",
                Arrays.asList("Tina aborted reception of kouchat-512x512.png"),
                "Tina aborted reception of kouchat-512x512.png");

        assertEquals(1, lines.size());

        correctLineContent(lines.get(0), 0, "Tina aborted reception of kouchat-512x512.png");
    }

    @Test
    public void getMatchingLinesOfTextShouldFindCorrectLineWhenAllOnOneLineWithExtraTextBeforeAndAfter() {
        final List<Line> lines = RobotiumTestUtils.getMatchingLinesOfText(
                "[17:02:41] *** Tina aborted reception of kouchat-512x512.png ***",
                Arrays.asList("[17:02:41] *** Tina aborted reception of kouchat-512x512.png ***"),
                "Tina aborted reception of kouchat-512x512.png");

        assertEquals(1, lines.size());

        correctLineContent(lines.get(0), 0, "Tina aborted reception of kouchat-512x512.png");
    }

    @Test
    public void getMatchingLinesOfTextShouldThrowExceptionIfAWordIsMissingOnTheSameLine() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not find: Tina aborted reception of kouchat-512x512.png");

        RobotiumTestUtils.getMatchingLinesOfText(
                "Tina aborted reception of kouchat-512x512",
                Arrays.asList("Tina aborted reception of kouchat-512x512"),
                "Tina aborted reception of kouchat-512x512.png");
    }

    @Test
    public void getMatchingLinesOfTextShouldThrowExceptionIfAWordIsMissingOnANewLine() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not find: Tina aborted reception of kouchat-512x512.png");

        RobotiumTestUtils.getMatchingLinesOfText(
                "Tina aborted reception of kouchat-512x512",
                Arrays.asList(
                        "Tina ",
                        "aborted",
                        " reception ",
                        "of ",
                        "kouchat",
                        "-512x512"),
                "Tina aborted reception of kouchat-512x512.png");
    }

    private void correctLineContent(final Line line, final Integer lineNumber, final String lineText) {
        assertEquals(lineNumber, line.getLineNumber());
        assertEquals(lineText, line.getLineText());
    }
}
