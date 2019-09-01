
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.util.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowDialogPreference;
import org.robolectric.shadows.ShadowTypedArray;

import com.larswerkman.holocolorpicker.ColorPicker;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Test of {@link HoloColorPickerPreference}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class HoloColorPickerPreferenceTest {

    private HoloColorPickerPreference preference;

    private View colorPickerDialog;
    private ColorPicker colorPicker;
    private ShadowDialogPreference shadowDialogPreference;

    private LinearLayout colorPreview;
    private ImageView colorPreviewImage;
    private GradientDrawable colorPickerDrawable;

    @Before
    public void setUp() {
        preference = new HoloColorPickerPreference(Robolectric.application, mock(AttributeSet.class));

        shadowDialogPreference = Robolectric.shadowOf(preference);

        final LayoutInflater inflater = LayoutInflater.from(Robolectric.application);
        colorPickerDialog = inflater.inflate(R.layout.color_picker_dialog, null);
        colorPicker = (ColorPicker) colorPickerDialog.findViewById(R.id.colorPicker);

        // Simulating the view expected in onBindView(), as the real one is private and can't be inflated
        colorPreviewImage = new ImageView(Robolectric.application);
        colorPreviewImage.setId(R.id.colorPreviewImage);
        colorPickerDrawable = mock(GradientDrawable.class);
        colorPreviewImage.setImageDrawable(colorPickerDrawable);
        TestUtils.setFieldValue(preference, "colorPreviewImage", colorPreviewImage);

        colorPreview = new LinearLayout(Robolectric.application);
        colorPreview.addView(colorPreviewImage);
    }

    @Test
    public void constructorShouldSetColorPickerDialogLayout() {
        assertEquals(R.layout.color_picker_dialog, preference.getDialogLayoutResource());
    }

    @Test
    public void constructorShouldSetColorPreviewLayout() {
        assertEquals(R.layout.color_preview, preference.getWidgetLayoutResource());
    }

    @Test
    public void constructorShouldSetButtonText() {
        assertEquals("OK", preference.getPositiveButtonText());
        assertEquals("Cancel", preference.getNegativeButtonText());
    }

    /**
     * A TypedArray is used to keep default values from XML.
     *
     * <p>It's a bit weird. It has a type at <code>requested index * 6</code>,
     * and the actual value at <code>requested index * 6 + 1</code>.</p>
     *
     * <p>Example:</p>
     * <ul>
     *   <li>Index 0: type at 0, value at 1</li>
     *   <li>Index 1: type at 6, value at 7</li>
     *   <li>Index 2: type at 12, value at 13</li>
     * </ul>
     */
    @Test
    public void onGetDefaultValueShouldReturnDefaultValueFromXmlWhenSet() {
        final int[] data = new int[20];
        data[6] = TypedValue.TYPE_FIRST_INT; // Need to add the correct type to be able to get the integer.
        data[7] = 50; // The value we want

        final TypedArray typedArray = ShadowTypedArray.create(null, null, data, null, 0, null);

        final Integer value = (Integer) preference.onGetDefaultValue(typedArray, 1);
        assertEquals(Integer.valueOf(50), value);
    }

    @Test
    public void onGetDefaultValueShouldReturnBlackWhenNoDefaultValueSetInXml() {
        final TypedArray typedArray = ShadowTypedArray.create(null, null, new int[20], null, 0, null);

        final Integer value = (Integer) preference.onGetDefaultValue(typedArray, 1);
        assertEquals(Integer.valueOf(Color.BLACK), value);
    }

    @Test
    public void onSetInitialValueShouldSetPersistedColorToValueOfParameterIfRestoreIsFalse() {
        assertEquals(0, preference.getPersistedColor());

        preference.onSetInitialValue(false, 1001);

        assertEquals(1001, preference.getPersistedColor());
    }

    @Test
    public void onSetInitialValueShouldSetPersistedColorToPersistedIntIfRestoreIsTrue() {
        shadowDialogPreference.setPersistent(true);
        shadowDialogPreference.persistInt(501);

        assertEquals(0, preference.getPersistedColor());

        preference.onSetInitialValue(true, 1001);

        assertEquals(501, preference.getPersistedColor());
    }

    @Test
    public void onSetInitialValueShouldSetPersistedColorToBlackIfPersistedIntIsMissingAndRestoreIsTrue() {
        assertEquals(0, preference.getPersistedColor());

        preference.onSetInitialValue(true, 1001);

        assertEquals(Color.BLACK, preference.getPersistedColor());
    }

    @Test
    public void onBindDialogViewShouldSetPreferenceAsTheColorChangedListener() {
        assertNull(colorPicker.getOnColorChangedListener());

        preference.onBindDialogView(colorPickerDialog);

        assertSame(preference, colorPicker.getOnColorChangedListener());
    }

    @Test
    public void onBindDialogViewShouldSetValueBarAndSaturationBar() {
        assertFalse(colorPicker.hasValueBar());
        assertFalse(colorPicker.hasSaturationBar());
        assertFalse(colorPicker.hasOpacityBar());
        assertFalse(colorPicker.hasSVBar());

        preference.onBindDialogView(colorPickerDialog);

        assertTrue(colorPicker.hasValueBar());
        assertTrue(colorPicker.hasSaturationBar());
        assertFalse(colorPicker.hasOpacityBar());
        assertFalse(colorPicker.hasSVBar());
    }

    @Test
    public void onBindDialogViewShouldSetColor() {
        // String.format("#%06X", (0xFFFFFF & colorPicker.getColor())) = #80FF00 - green
        assertEquals(-8323328, colorPicker.getColor());

        TestUtils.setFieldValue(preference, "persistedColor", Color.BLUE);

        preference.onBindDialogView(colorPickerDialog);

        // getColor() returns the value set by a touch event, so this wont work
        // assertEquals(Color.BLUE, colorPicker.getColor());

        // setColor() does set the value of mColor.
        assertEquals(Integer.valueOf(Color.BLUE), TestUtils.getFieldValue(colorPicker, Integer.class, "mColor"));
    }

    @Test
    public void onBindDialogViewShouldSetOldCenterColor() {
        // String.format("#%06X", (0xFFFFFF & colorPicker.getColor())) = #80FF00 - green
        assertEquals(-8323328, colorPicker.getOldCenterColor());

        TestUtils.setFieldValue(preference, "persistedColor", Color.BLUE);

        preference.onBindDialogView(colorPickerDialog);

        assertEquals(Color.BLUE, colorPicker.getOldCenterColor());
    }

    @Test
    public void onDialogClosedWithFalseShouldDoNothing() {
        shadowDialogPreference.setPersistent(true);

        assertEquals(0, preference.getPersistedColor());
        TestUtils.setFieldValue(preference, "currentColor", 500);

        preference.onDialogClosed(false);

        assertEquals(0, preference.getPersistedColor());
        assertEquals(0, shadowDialogPreference.getPersistedInt(-1));
        verifyZeroInteractions(colorPickerDrawable);
    }

    @Test
    public void onDialogClosedWithTrueShouldPersistCurrentColor() {
        shadowDialogPreference.setPersistent(true);

        assertEquals(0, preference.getPersistedColor());
        TestUtils.setFieldValue(preference, "currentColor", 500);

        preference.onDialogClosed(true);

        assertEquals(500, preference.getPersistedColor());
        assertEquals(500, shadowDialogPreference.getPersistedInt(-1));
    }

    @Test
    public void onDialogClosedWithTrueShouldUpdatePreviewWithCurrentColor() {
        shadowDialogPreference.setPersistent(true);
        TestUtils.setFieldValue(preference, "currentColor", 500);

        preference.onDialogClosed(true);

        verify(colorPickerDrawable).setColor(500);
    }

    @Test
    public void onColorChangedShouldSetCurrentColorToValueOfParameter() {
        assertEquals(Integer.valueOf(0), TestUtils.getFieldValue(preference, Integer.class, "currentColor"));

        preference.onColorChanged(50);

        assertEquals(Integer.valueOf(50), TestUtils.getFieldValue(preference, Integer.class, "currentColor"));
    }

    @Test
    public void onBindViewShouldSetColorPreviewImage() {
        TestUtils.setFieldValue(preference, "colorPreviewImage", null);

        preference.onBindView(colorPreview);

        assertSame(colorPreviewImage, TestUtils.getFieldValue(preference, ImageView.class, "colorPreviewImage"));
    }

    @Test
    public void onBindViewShouldUpdatePreviewWithPersistedColor() {
        TestUtils.setFieldValue(preference, "persistedColor", 500);

        preference.onBindView(colorPreview);

        verify(colorPickerDrawable).setColor(500);
    }
}
