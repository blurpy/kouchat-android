
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

package net.usikkert.kouchat.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Utilities for tests.
 *
 * @author Christian Ihle
 */
public final class TestUtils {

    private TestUtils() {

    }

    /**
     * Gets the value of the field with the specified name in the specified object.
     *
     * @param object The object to get the value from.
     * @param fieldClass The class of the field.
     * @param fieldName The name of the field.
     * @param <T> The class of the field.
     * @return The value in the field of the object.
     */
    public static <T> T getFieldValue(final Object object, final Class<T> fieldClass, final String fieldName) {
        Validate.notNull(object, "The object to get the value from can not be null");
        Validate.notNull(fieldClass, "The class of the field can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        return getValue(object, fieldClass, field);
    }

    /**
     * Set the value in the field with the specified name in the specified object.
     *
     * @param object The object to set the value in.
     * @param fieldName The name of the field.
     * @param value The value to set in the field.
     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Validate.notNull(object, "The object to set the value in can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        setValue(object, value, field);
    }

    /**
     * Quits the application by using the quit menu item.
     *
     * @param solo The solo tester.
     */
    public static void quit(final Solo solo) {
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Quit");
    }

    private static void setValue(final Object object, final Object value, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            field.set(object, value);
        }

        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }

    private static Field getField(final Object object, final String fieldName) {
        try {
            return object.getClass().getDeclaredField(fieldName);
        }

        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getValue(final Object object, final Class<T> fieldClass, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            return fieldClass.cast(field.get(object));
        }

        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }

    /**
     * Adds a line of text to the first edittext field, and presses enter.
     *
     * @param solo The solo tester.
     * @param text The line of text to write.
     */
    public static void writeLine(final Solo solo, final String text) {
        solo.enterText(0, text);
        solo.sendKey(KeyEvent.KEYCODE_ENTER);
    }

    /**
     * Gets a textview containing the given text.
     *
     * @param solo The solo tester.
     * @param text The text to look for in textviews.
     * @return The textview with the text.
     * @throws IllegalArgumentException If no textview was found with the given text.
     */
    public static TextView getTextViewWithText(final Solo solo, final String text) {
        final ArrayList<TextView> currentTextViews = solo.getCurrentTextViews(null);

        for (final TextView currentTextView : currentTextViews) {
            if (currentTextView.getClass().equals(TextView.class) &&
                    currentTextView.getText().toString().contains(text)) {
                return currentTextView;
            }
        }

        throw new IllegalArgumentException("Could not find TextView with text: " + text);
    }

    /**
     * Gets the text on the given line in the textview.
     *
     * @param textView The textview to get the text from.
     * @param line The line in the textview to get the text from.
     * @return The text on the given line.
     */
    public static String getLineOfText(final TextView textView, final int line) {
        final Layout layout = textView.getLayout();
        final String text = textView.getText().toString();

        final int lineStart = layout.getLineStart(line);
        final int lineEnd = layout.getLineEnd(line);

        return text.substring(lineStart, lineEnd);
    }

    /**
     * Gets the coordinates in the textview for the given text.
     *
     * @param textView The textview to find the coordinates in.
     * @param text The text to find coordnates for.
     * @return The coordinates for the given text.
     * @throws IllegalArgumentException If no lines in the textview has the given text.
     */
    public static Point getCoordinatesForText(final TextView textView, final String text) {
        final Layout layout = textView.getLayout();
        final int lineCount = layout.getLineCount();

        for (int currentLineNumber = 0; currentLineNumber < lineCount; currentLineNumber++) {
            final String currentLine = getLineOfText(textView, currentLineNumber);

            if (currentLine.contains(text)) {
                return getCoordinatesForLine(textView, text, currentLineNumber, currentLine);
            }
        }

        throw new IllegalArgumentException("Could not get coordinates for text: " + text);
    }

    private static Point getCoordinatesForLine(final TextView textView, final String text,
                                               final int lineNumber, final String line) {
        final Layout layout = textView.getLayout();
        final TextPaint paint = textView.getPaint();

        final int textIndex = line.indexOf(text.charAt(0));
        final String preText = line.substring(0, textIndex);

        final int textWidth = (int) Layout.getDesiredWidth(text, paint);
        final int preTextWidth = (int) Layout.getDesiredWidth(preText, paint);

        final int[] textViewXYLocation = new int[2];
        textView.getLocationOnScreen(textViewXYLocation);

        // Width: in the middle of the text
        final int xPosition = preTextWidth + (textWidth / 2);
        // Height: in the middle of the given line, plus the text view position from the top, minus the amount scrolled
        final int yPosition = layout.getLineBaseline(lineNumber) + textViewXYLocation[1] - textView.getScrollY();

        return new Point(xPosition, yPosition);
    }

    /**
     * Clicks on the given text.
     *
     * @param solo The solo tester.
     * @param text The text to click on.
     */
    public static void clickOnText(final Solo solo, final String text) {
        final TextView textView = getTextViewWithText(solo, text);
        final Point coordinates = getCoordinatesForText(textView, text);

        solo.clickOnScreen(coordinates.x, coordinates.y);
    }

    /**
     * Hides the software keyboard, if it's visible.
     *
     * @param solo The solo tester.
     * @param activity The current activity.
     */
    public static void hideSoftwareKeyboard(final Solo solo, final Activity activity) {
        if (softwareKeyboardIsVisible(activity)) {
            solo.goBack();
        }
    }

    private static boolean softwareKeyboardIsVisible(final Activity activity) {
        final Rect visibleDisplayFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleDisplayFrame);

        final int statusBarHeight = visibleDisplayFrame.top;
        final int activityHeight = visibleDisplayFrame.height();
        final int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();

        final int diff = (screenHeight - statusBarHeight) - activityHeight;

        return diff > screenHeight / 3;
    }

    /**
     * Searches for a textview with the given text.
     *
     * @param solo The solo tester.
     * @param text The text to search for.
     * @return If the text was found in any textviews.
     */
    public static boolean searchText(final Solo solo, final String text) {
        try {
            getTextViewWithText(solo, text);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }
}
