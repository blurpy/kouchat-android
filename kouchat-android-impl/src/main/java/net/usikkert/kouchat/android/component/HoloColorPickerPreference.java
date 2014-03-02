
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

package net.usikkert.kouchat.android.component;

import net.usikkert.kouchat.android.R;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * A preference using the Holo Color Picker.
 *
 * @author Christian Ihle
 */
public class HoloColorPickerPreference extends DialogPreference implements ColorPicker.OnColorChangedListener {

    /** Used if default value is unspecified in xml. */
    private final int defaultColor = Color.BLACK;

    /** The color last selected by the user. */
    private int currentColor;

    /** The color that apply now. */
    private int persistedColor;

    public HoloColorPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.color_picker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    /**
     * Gets the default color to use, if no color has been stored yet.
     * Either from the preference definition in xml (<code>android:defaultValue</code>),
     * or {@link #defaultColor} if the xml does not specify a default value.
     *
     * <p>Runs when opening the settings.</p>
     *
     * @param typedArray An array of values from xml.
     * @param index The index of the array to look for the default value.
     * @return The default color to use.
     */
    @Override
    protected Object onGetDefaultValue(final TypedArray typedArray, final int index) {
        return typedArray.getInteger(index, defaultColor);
    }

    /**
     * Sets the initial color to use. Either the default color, or the last persisted color if there is one.
     *
     * <p>Runs when opening the settings.</p>
     *
     * @param restorePersistedValue If the color has been persisted before, and should be restored.
     * @param defaultValue The color from {@link #onGetDefaultValue(TypedArray, int)}. Is <code>null</code> when restore is false.
     */
    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
        if (restorePersistedValue) {
            // Color has been persisted. Get that color. Or the defaultColor, but now sure if that could happen.
            persistedColor = getPersistedInt(defaultColor);
        } else {
            // Color has never been persisted. Use value from onGetDefaultValue
            persistedColor = (Integer) defaultValue;
        }
    }

    /**
     * Initializes the color picker dialog components.
     *
     * <p>Runs when the dialog opens.</p>
     *
     * @param view The layout that contains all the components of the dialog.
     */
    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);

        final ColorPicker colorPicker = (ColorPicker) view.findViewById(R.id.colorPicker);
        final ValueBar valueBar = (ValueBar) view.findViewById(R.id.colorPickerValueBar);
        final SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.colorPickerSaturationBar);

        colorPicker.setOnColorChangedListener(this);
        colorPicker.addValueBar(valueBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.setColor(persistedColor);
        colorPicker.setOldCenterColor(persistedColor);
    }

    /**
     * Persists the chosen color when closing the dialog with <code>OK</code>.
     * Does nothing if closing with <code>Cancel</code>.
     *
     * @param positiveResult If the positive button (<code>OK</code>) was pressed.
     */
    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult) {
            persistedColor = currentColor;
            persistInt(persistedColor);
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

    /**
     * Returns the color that apply now.
     *
     * @return The color that apply now.
     */
    public int getPersistedColor() {
        return persistedColor;
    }
}
