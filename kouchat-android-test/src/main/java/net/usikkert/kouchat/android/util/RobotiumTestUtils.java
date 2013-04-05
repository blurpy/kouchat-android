
/***************************************************************************
 *   Copyright 2006-2013 by Christian Ihle                                 *
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

package net.usikkert.kouchat.android.util;

import static junit.framework.Assert.*;

import java.util.ArrayList;

import net.usikkert.kouchat.android.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.text.Layout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * Utilities for tests.
 *
 * @author Christian Ihle
 */
public final class RobotiumTestUtils {

    private RobotiumTestUtils() {

    }

    /**
     * Quits the application by using the quit menu item. Works from any activity.
     *
     * @param solo The solo tester.
     */
    public static void quit(final Solo solo) {
        goHome(solo);
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Quit");
    }

    /**
     * Returns to the main chat.
     *
     * @param solo The solo tester.
     */
    public static void goHome(final Solo solo) {
        solo.sleep(500);
        solo.goBackToActivity(MainChatController.class.getSimpleName());
        solo.sleep(500);
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
     */
    public static void hideSoftwareKeyboard(final Solo solo) {
        if (softwareKeyboardIsVisible(solo.getCurrentActivity())) {
            solo.goBack();
        }
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

    /**
     * Goes back to the previous activity, hiding the software keyboard first if necessary.
     *
     * @param solo The solo tester.
     */
    public static void goBack(final Solo solo) {
        final Activity currentActivity = solo.getCurrentActivity();

        solo.goBack();

        // No change in activity by going back. Assuming it's because the software keyboard was visible.
        // Need to go back once more to actually "go back".
        if (currentActivity == solo.getCurrentActivity()) {
            solo.goBack();
        }

        solo.sleep(500);
    }

    /**
     * Switches the orientation between landscape and portrait.
     *
     * @param solo The solo tester.
     */
    public static void switchOrientation(final Solo solo) {
        if (getCurrentOrientation(solo) == Configuration.ORIENTATION_LANDSCAPE) {
            solo.setActivityOrientation(Solo.PORTRAIT);
        } else {
            solo.setActivityOrientation(Solo.LANDSCAPE);
        }

        solo.sleep(500);
    }

    /**
     * Sets the orientation to the requested new orientation.
     *
     * @param solo The solo tester.
     * @param newOrientation The new orientation to set,
     */
    public static void setOrientation(final Solo solo, final int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            solo.setActivityOrientation(Solo.LANDSCAPE);
        } else {
            solo.setActivityOrientation(Solo.PORTRAIT);
        }

        solo.sleep(500);
    }

    /**
     * Gets the orientation of the activity.
     *
     * @param solo The solo tester.
     * @return The current orientation.
     */
    public static int getCurrentOrientation(final Solo solo) {
        return solo.getCurrentActivity().getResources().getConfiguration().orientation;
    }

    /**
     * Goes to the settings in the menu, and selects the option to change the nick name.
     *
     * @param solo The solo tester.
     */
    public static void clickOnChangeNickNameInTheSettings(final Solo solo) {
        // Go to the Settings menu item and choose to set nick name
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.clickOnText("Set nick name");
    }

    /**
     * Changes the nick name, if already in the correct menu.
     *
     * Use {@link #clickOnChangeNickNameInTheSettings(com.jayway.android.robotium.solo.Solo)} first.
     *
     * @param solo The solo tester.
     * @param nickName The nick name to change to.
     */
    public static void changeNickNameTo(final Solo solo, final String nickName) {
        hideSoftwareKeyboard(solo);
        solo.clearEditText(0);
        solo.enterText(0, nickName);
        solo.clickOnButton("OK");
    }

    /**
     * Extracts "me", using the main chat controller.
     *
     * @param mainChatController The main chat controller.
     * @return The application user.
     */
    public static User getMe(final MainChatController mainChatController) {
        final AndroidUserInterface androidUserInterface =
                TestUtils.getFieldValue(mainChatController, AndroidUserInterface.class, "androidUserInterface");

        return TestUtils.getFieldValue(androidUserInterface, User.class, "me");
    }

    /**
     * Launches the main chat.
     *
     * <p>Use {@link #goBack(Solo)} or {@link #goHome(Solo)} to navigate to an already opened main chat.
     * Use this method if the main chat has been finished and closed.</p>
     *
     * @param testCase The test that needs to launch the main chat.
     */
    public static void launchMainChat(final InstrumentationTestCase testCase) {
        final String packageName = testCase.getInstrumentation().getTargetContext().getPackageName();
        testCase.launchActivity(packageName, MainChatController.class, null);
    }

    /**
     * Closes the main chat, by finishing it.
     *
     * @param testCase The test that needs to close the main chat.
     */
    public static void closeMainChat(final ActivityInstrumentationTestCase2 testCase) {
        final Activity activity = testCase.getActivity();
        assertEquals(MainChatController.class, activity.getClass());

        activity.finish();
    }

    /**
     * Opens a private chat with the specified user.
     *
     * @param solo The solo tester.
     * @param numberOfUsers Number of users to expect in the main chat.
     * @param userNumber The number to expect the specified user to be in the list.
     * @param userName User name of the user to open the private chat with.
     */
    public static void openPrivateChat(final Solo solo, final int numberOfUsers, final int userNumber,
                                       final String userName) {
        solo.sleep(500);
        assertEquals(numberOfUsers, solo.getCurrentListViews().get(0).getCount());
        solo.clickInList(userNumber);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have opened the private chat", PrivateChatController.class);
        // To be sure we are chatting with the right user
        assertEquals(userName + " - KouChat", solo.getCurrentActivity().getTitle());
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

    private static boolean softwareKeyboardIsVisible(final Activity activity) {
        final Rect visibleDisplayFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleDisplayFrame);

        final int statusBarHeight = visibleDisplayFrame.top;
        final int activityHeight = visibleDisplayFrame.height();
        final int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();

        final int diff = (screenHeight - statusBarHeight) - activityHeight;

        return diff > screenHeight / 3;
    }
}
