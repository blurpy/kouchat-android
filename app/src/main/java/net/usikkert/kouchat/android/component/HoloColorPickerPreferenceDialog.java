
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

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import net.usikkert.kouchat.android.R;

import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;

/**
 * The color picker dialog for the {@link HoloColorPickerPreference}.
 *
 * @author Christian Ihle
 */
public class HoloColorPickerPreferenceDialog extends PreferenceDialogFragmentCompat
                                             implements ColorPicker.OnColorChangedListener {

    /** The color last selected by the user. */
    private int currentColor;

    /**
     * A factory method to create an instance of this dialog.
     *
     * <p>Seems to be the recommended pattern, as seen in
     * {@link android.support.v7.preference.EditTextPreferenceDialogFragmentCompat}</p>
     *
     * @param key The preference key, like own_color and sys_color.
     * @return An instance of this.
     */
    public static HoloColorPickerPreferenceDialog newInstance(final String key) {
        final Bundle arguments = new Bundle(1);
        arguments.putString(ARG_KEY, key);

        final HoloColorPickerPreferenceDialog dialog = new HoloColorPickerPreferenceDialog();
        dialog.setArguments(arguments);

        return dialog;
    }

    /**
     * Initializes the color picker dialog components.
     *
     * <p>Runs when the dialog opens.</p>
     *
     * @param view The color picker dialog layout.
     */
    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);

        final ColorPicker colorPicker = view.findViewById(R.id.colorPicker);
        final ValueBar valueBar = view.findViewById(R.id.colorPickerValueBar);
        final SaturationBar saturationBar = view.findViewById(R.id.colorPickerSaturationBar);

        final int persistedColor = getColorPickerPreference().getPersistedColor();

        colorPicker.setOnColorChangedListener(this);
        colorPicker.addValueBar(valueBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setColor(persistedColor);
        colorPicker.setOldCenterColor(persistedColor);
    }

    /**
     * Persists the chosen color and updates the preview image when closing the dialog with <code>OK</code>.
     * Does nothing if closing with <code>Cancel</code>.
     *
     * @param positiveResult If the positive button (<code>OK</code>) was pressed.
     */
    @Override
    public void onDialogClosed(final boolean positiveResult) {
        if (positiveResult && getPreference().callChangeListener(currentColor)) {
            getColorPickerPreference().persistColor(currentColor);
        }
    }

    /**
     * Sets the currently selected color whenever the user touches the color wheel or any of the sliders.
     *
     * @param color The currently selected color on the color wheel.
     */
    @Override
    public void onColorChanged(final int color) {
        currentColor = color;
    }

    private HoloColorPickerPreference getColorPickerPreference() {
        return (HoloColorPickerPreference) getPreference();
    }
}
