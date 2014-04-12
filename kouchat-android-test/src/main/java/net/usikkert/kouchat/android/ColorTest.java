
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

package net.usikkert.kouchat.android;

import static org.fest.reflect.core.Reflection.*;

import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.util.RobotiumTestUtils;
import net.usikkert.kouchat.misc.Settings;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.robotium.solo.Solo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Tests changing message colors in the settings.
 *
 * @author Christian Ihle
 */
public class ColorTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private static int originalOwnColor;

    private Solo solo;
    private Settings settings;

    public ColorTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        final MainChatController activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);

        final AndroidUserInterface androidUserInterface = RobotiumTestUtils.getAndroidUserInterface(activity);
        settings = androidUserInterface.getSettings();

        if (originalOwnColor == 0) {
            originalOwnColor = settings.getOwnColor();
        }
    }

    public void test01CancelAfterChangingColorShouldNotSave() {
        sendOwnMessage("This is my color");
        checkTextColor("This is my color", originalOwnColor);

        openSettings();
        checkPreviewColor(originalOwnColor);

        final ColorPicker colorPicker = openColorPicker("Set own message color");
        moveColorWheelPointer(colorPicker, originalOwnColor, -150);
        cancelNewColor();

        RobotiumTestUtils.goHome(solo);

        checkTextColor("This is my color", originalOwnColor);
        sendOwnMessage("This is still my color");
        checkTextColor("This is still my color", originalOwnColor);

        solo.sleep(1000);
    }

    public void test02OkAfterChangingColorShouldSave() {
        openSettings();

        final ColorPicker firstColorPicker = openColorPicker("Set own message color");
        moveColorWheelPointer(firstColorPicker, originalOwnColor, -150);
        acceptNewColor();
        final int firstColor = firstColorPicker.getColor();

        assertEquals(firstColor, settings.getOwnColor());
        checkPreviewColor(firstColor);

        RobotiumTestUtils.goHome(solo);

        checkTextColor("This is my color", originalOwnColor);
        sendOwnMessage("This is my new color");
        checkTextColor("This is my new color", firstColor);

        openSettings();

        final ColorPicker secondColorPicker = openColorPicker("Set own message color");
        moveColorWheelPointer(secondColorPicker, firstColor, 150);
        acceptNewColor();
        final int secondColor = secondColorPicker.getColor();

        assertEquals(hsvFrom(originalOwnColor)[0], hsvFrom(secondColor)[0], 1f);
        assertEquals(secondColor, settings.getOwnColor());
        checkPreviewColor(secondColor);

        RobotiumTestUtils.goHome(solo);

        sendOwnMessage("Color is back");
        checkTextColor("Color is back", secondColor);

        solo.sleep(1000);
    }

    public void test99Quit() {
        // TODO reset back to original color directly using the settings?
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        settings = null;
        setActivity(null);

        System.gc();
    }

    private float[] hsvFrom(final int color) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        return hsv;
    }

    private void cancelNewColor() {
        solo.sleep(100);
        solo.clickOnText("Cancel");
        solo.sleep(500);
    }

    private void acceptNewColor() {
        solo.sleep(100);
        solo.clickOnText("OK");
        solo.sleep(500);
    }

    private void moveColorWheelPointer(final ColorPicker colorPicker,
                                       final int expectedCurrentColor,
                                       final int degreesToMove) {
        final float[] expectedCurrentColorHsv = hsvFrom(expectedCurrentColor);

        final int currentColor = colorPicker.getColor();
        final float[] currentColorHsv = hsvFrom(currentColor);
        final float[] currentPosition = getCurrentPosition(colorPicker);
        final float[] newPosition = calculatePosition(colorPicker, currentColorHsv, degreesToMove);

        assertEquals(expectedCurrentColor, colorPicker.getOldCenterColor());
        assertEquals(expectedCurrentColorHsv[0], currentColorHsv[0], 1f);

        moveColorWheelPointer(currentPosition, newPosition);

        final int newColor = colorPicker.getColor();
        final float[] newColorHsv = hsvFrom(newColor);
        final float degreesMoved = getDegreesMoved(currentColorHsv, newColorHsv);

        assertNotSame(currentColor, newColor);
        assertEquals(Math.abs(degreesToMove), degreesMoved, 2f);
    }

    private void checkTextColor(final String text, final int expectedColor) {
        final TextView mainChatView = (TextView) solo.getView(R.id.mainChatView);
        final int colorForText = getColorForText(mainChatView, text);

        assertEquals(expectedColor, colorForText);
    }

    private void sendOwnMessage(final String text) {
        solo.sleep(500);
        RobotiumTestUtils.writeLine(solo, text);
        solo.sleep(500);
    }

    private void checkPreviewColor(final int expectedColor) {
        final ImageView colorPreviewImage = (ImageView) solo.getView(R.id.colorPreviewImage, 0);
        final int colorFromPreviewImage = getColorFromPreviewImage(colorPreviewImage);

        assertEquals(expectedColor, colorFromPreviewImage);
    }

    private void openSettings() {
        solo.sleep(500);
        RobotiumTestUtils.openSettings(solo);
        solo.sleep(500);
    }

    private int getColorForText(final TextView textView, final String textToFind) {
        final Spannable allText = (Spannable) textView.getText();
        final int textToFindIndex = allText.toString().indexOf(textToFind);
        final Spannable[] resultArray = new Spannable[1];

        textView.post(new Runnable() {
            @Override
            public void run() {
                // subSequence does something on the ui thread
                resultArray[0] = (Spannable) allText.subSequence(textToFindIndex, textToFindIndex + textToFind.length());
            }
        });

        solo.sleep(100);

        final Spannable textToFindSpan = resultArray[0];
        assertEquals(textToFind, textToFindSpan.toString());

        final ForegroundColorSpan[] spans = textToFindSpan.getSpans(0, textToFindSpan.length(), ForegroundColorSpan.class);
        assertEquals(1, spans.length);

        return spans[0].getForegroundColor();
    }

    private int getColorFromPreviewImage(final ImageView previewImage) {
        previewImage.buildDrawingCache();
        final Bitmap drawingCache = previewImage.getDrawingCache();

        return drawingCache.getPixel(10, 10);
    }

    private ColorPicker openColorPicker(final String colorPickerText) {
        solo.clickOnText(colorPickerText);
        solo.sleep(500);

        return solo.getView(ColorPicker.class, 0);
    }

    private void moveColorWheelPointer(final float[] fromPosition, final float[] toPosition) {
        solo.sleep(500);
        solo.drag(fromPosition[0], toPosition[0], fromPosition[1], toPosition[1], 10);
        solo.sleep(500);
    }

    private float getDegreesMoved(final float[] degreeOneHsv, final float[] degreeTwoHsv) {
        final float diff = Math.abs(degreeOneHsv[0] - degreeTwoHsv[0]);

        return Math.min(360 - diff, diff);
    }

    private float[] calculatePosition(final ColorPicker colorPicker,
                                      final float[] currentColorHsv,
                                      final int degreesToMove) {
        final float currentDegree = currentColorHsv[0];
        final float newDegree = currentDegree + degreesToMove % 360;
        final float angle = (float) Math.toRadians(-newDegree);

        return getPosition(colorPicker, angle);
    }

    private float[] getCurrentPosition(final ColorPicker colorPicker) {
        final float mAngle = field("mAngle")
                .ofType(float.class)
                .in(colorPicker)
                .get();

        return getPosition(colorPicker, mAngle);
    }

    private float[] getPosition(final ColorPicker colorPicker, final float angle) {
        final float mTranslationOffset = field("mTranslationOffset")
                .ofType(float.class)
                .in(colorPicker)
                .get();

        final float[] calculatedPointerPosition = method("calculatePointerPosition")
                .withReturnType(float[].class)
                .withParameterTypes(float.class)
                .in(colorPicker)
                .invoke(angle);

        final int[] locationOnScreen = new int[2];
        colorPicker.getLocationOnScreen(locationOnScreen);

        calculatedPointerPosition[0] += mTranslationOffset + locationOnScreen[0];
        calculatedPointerPosition[1] += mTranslationOffset + locationOnScreen[1];

        return calculatedPointerPosition;
    }
}
