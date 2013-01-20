
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.smiley;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.util.Validate;

/**
 * Class for extracting smileys from lines of text.
 *
 * @author Christian Ihle
 */
public class SmileyLocator {

    private static final int SMILEY_GROUP = 2;

    private final Set<Pattern> smileyPatterns;

    /**
     * Constructor.
     *
     * @param smileyCodes The smiley codes to support.
     */
    public SmileyLocator(final Set<String> smileyCodes) {
        Validate.notNull(smileyCodes, "Smiley codes can not be null");

        smileyPatterns = new HashSet<Pattern>();
        addSmileyPatterns(smileyCodes);
    }

    /**
     * Finds all the smileys in the given text, based on the registered smiley codes.
     *
     * @param text The text to find smileys in.
     * @return List of the detected smileys, with their position in the text.
     */
    public List<Smiley> findSmileys(final String text) {
        Validate.notNull(text, "Text can not be null");

        final ArrayList<Smiley> detectedSmileys = new ArrayList<Smiley>();

        for (final Pattern smileyPattern : smileyPatterns) {
            final Matcher matcher = smileyPattern.matcher(text);

            while (matcher.find()) {
                detectedSmileys.add(createSmileyFromMatch(matcher));
            }
        }

        return detectedSmileys;
    }

    private Smiley createSmileyFromMatch(final Matcher matcher) {
        return new Smiley(matcher.group(SMILEY_GROUP), matcher.start(SMILEY_GROUP), matcher.end(SMILEY_GROUP));
    }

    /**
     * Creates regex patterns required to locate each of the smileys in the set of smiley codes.
     *
     * Group 1: beginning of line or whitespace
     * Group 2: smiley
     * Group 3: whitespace or end of line
     *
     * @param smileyCodes The smileys to create patterns for.
     */
    private void addSmileyPatterns(final Set<String> smileyCodes) {
        for (final String smileyCode : smileyCodes) {
            smileyPatterns.add(Pattern.compile("(^|\\s)(" + Pattern.quote(smileyCode) + ")(\\s|$)"));
        }
    }
}
