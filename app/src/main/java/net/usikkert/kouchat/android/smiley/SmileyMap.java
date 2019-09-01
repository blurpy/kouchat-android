
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

package net.usikkert.kouchat.android.smiley;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * This class represents a map of all the supported smileys, with the smiley code as the key,
 * and the drawable as the value.
 *
 * <p>Supported smiley codes:</p>
 *
 * <ul>
 *   <li>:)</li>
 *   <li>:(</li>
 *   <li>:p</li>
 *   <li>:D</li>
 *   <li>;)</li>
 *   <li>:O</li>
 *   <li>:@</li>
 *   <li>:S</li>
 *   <li>;(</li>
 *   <li>:$</li>
 *   <li>8)</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class SmileyMap {

    private final Map<String, Drawable> smileyMap;

    /**
     * Constructor. Loads all the smileys in the map.
     *
     * @param context An Android context, for loading resources.
     */
    public SmileyMap(final Context context) {
        Validate.notNull(context, "Context can not be null");

        smileyMap = new HashMap<>();

        loadSmileys(context.getResources());
    }

    /**
     * Gets the smiley with the specified code. Like "<code>:)</code>"
     *
     * @param code The code of the smiley to get.
     * @return The smiley with the specified code.
     */
    public Drawable getSmiley(final String code) {
        Validate.notEmpty(code, "Smiley code can not be empty");

        if (!smileyMap.containsKey(code)) {
            throw new IllegalArgumentException(String.format("Smiley with code '%s' does not exist", code));
        }

        return smileyMap.get(code);
    }

    /**
     * Gets a set of all the smiley codes in this map.
     *
     * @return A set of all the smiley codes.
     */
    public Set<String> getSmileyCodes() {
        return smileyMap.keySet();
    }

    private void loadSmileys(final Resources resources) {
        smileyMap.put(":)", getDrawable(resources, R.drawable.ic_smiley_smile));
        smileyMap.put(":(", getDrawable(resources, R.drawable.ic_smiley_sad));
        smileyMap.put(":p", getDrawable(resources, R.drawable.ic_smiley_tongue));
        smileyMap.put(":D", getDrawable(resources, R.drawable.ic_smiley_teeth));
        smileyMap.put(";)", getDrawable(resources, R.drawable.ic_smiley_wink));
        smileyMap.put(":O", getDrawable(resources, R.drawable.ic_smiley_omg));
        smileyMap.put(":@", getDrawable(resources, R.drawable.ic_smiley_angry));
        smileyMap.put(":S", getDrawable(resources, R.drawable.ic_smiley_confused));
        smileyMap.put(";(", getDrawable(resources, R.drawable.ic_smiley_cry));
        smileyMap.put(":$", getDrawable(resources, R.drawable.ic_smiley_embarrassed));
        smileyMap.put("8)", getDrawable(resources, R.drawable.ic_smiley_shade));
    }

    private Drawable getDrawable(final Resources resources, final int smileyId) {
        final Drawable drawable = resources.getDrawable(smileyId);

        // For some reason, the bounds is set to 0, with the result that the smiley becomes invisible. Fixing manually.
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        return drawable;
    }
}
