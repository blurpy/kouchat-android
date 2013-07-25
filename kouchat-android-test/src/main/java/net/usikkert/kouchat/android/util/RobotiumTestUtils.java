
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
import java.util.Arrays;
import java.util.List;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestUtils;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.text.Layout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.ScrollView;
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
        openMenu(solo);
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
     * Opens the overflow menu in the action bar.
     *
     * @param solo The solo tester.
     */
    public static void openMenu(final Solo solo) {
        solo.clickOnView(solo.getView(R.id.mainChatMenu));
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
        final ArrayList<TextView> currentTextViews = solo.getCurrentViews(TextView.class);

        for (final TextView currentTextView : currentTextViews) {
            if (currentTextView.getClass().equals(TextView.class) &&
                    currentTextView.getText().toString().contains(text)) {
                return currentTextView;
            }
        }

        throw new IllegalArgumentException("Could not find TextView with text: " + text);
    }

    // TODO
    public static List<String> getAllLinesOfText(final TextView textView) {
        final ArrayList<String> allLines = new ArrayList<String>();

        final Layout layout = textView.getLayout();
        final int lineCount = layout.getLineCount();

        for (int currentLineNumber = 0; currentLineNumber < lineCount; currentLineNumber++) {
            allLines.add(getLineOfText(textView, currentLineNumber));
        }

        return allLines;
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
     * Clicks on the "up" button in the action bar. The up button is the icon with the left arrow.
     *
     * @param solo The solo tester.
     */
    public static void goUp(final Solo solo) {
        // Native ActionBar in use.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            solo.clickOnView(solo.getView(android.R.id.home));
        }

        // Compatibility ActionBar from ActionBarSherlock in use.
        else {
            solo.clickOnView(solo.getView(R.id.abs__home));
        }
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
     * Goes to the settings in the menu.
     *
     * @param solo The solo tester.
     */
    public static void openSettings(final Solo solo) {
        openMenu(solo);
        solo.clickOnText("Settings");
    }

    /**
     * Goes to the settings in the menu, and selects the option to change the nick name.
     *
     * @param solo The solo tester.
     */
    public static void clickOnChangeNickNameInTheSettings(final Solo solo) {
        openSettings(solo);
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
     * Extracts "me" from the android user interface in an activity.
     *
     * @param activity An activity with an android user interface object.
     * @return The application user.
     */
    public static User getMe(final Activity activity) {
        final AndroidUserInterface androidUserInterface =
                TestUtils.getFieldValue(activity, AndroidUserInterface.class, "androidUserInterface");

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
        assertEquals(numberOfUsers, solo.getCurrentViews(ListView.class).get(0).getCount());
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

    // TODO
    public static boolean textIsVisible(final Solo solo, final int textViewId, final int scrollViewId, final String text) {
        final TextView textView = (TextView) solo.getView(textViewId);
        final ScrollView scrollView = (ScrollView) solo.getView(scrollViewId);

        final int[] location = new int[2];
        scrollView.getLocationOnScreen(location);
        final Rect visibleScrollArea = new Rect(location[0], location[1], location[0] + scrollView.getWidth(), location[1] + scrollView.getHeight());

        final List<String> allLinesOfText = getAllLinesOfText(textView);
        final List<Pair<Integer, String>> matchingLinesOfText = getMatchingLinesOfText(textView.getText().toString(), allLinesOfText, text);

        for (final Pair<Integer, String> matchingLine : matchingLinesOfText) {
            final Point coordinatesForLine = getCoordinatesForLine(textView, matchingLine.second, matchingLine.first, allLinesOfText.get(matchingLine.first));

            if (!visibleScrollArea.contains(coordinatesForLine.x, coordinatesForLine.y)) {
                return false;
            }
        }

        return true;
    }

    // TODO
    public static List<Pair<Integer, String>> getMatchingLinesOfText(final String fullText, final List<String> lines, final String text) {
        final int lastIndex = fullText.lastIndexOf(text);

        if (lastIndex < 0) {
            throw new IllegalArgumentException("Could not find: " + text);
        }

        int startLine = 0;
        int currentIndex = 0;

        for (final String line : lines) {
            if (currentIndex + line.length() >= lastIndex) {
                break;
            }

            startLine++;
            currentIndex += line.length();
        }

        final ArrayList<Pair<Integer, String>> pairs = new ArrayList<Pair<Integer, String>>();
        final ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split("\\b")));

        if (words.get(0).equals("")) {
            words.remove(0);
        }

        for (int i = startLine; i < lines.size(); i++) {
            addLine(lines.get(i), i, words, pairs);

            if (words.isEmpty()) {
                break;
            }
        }

        return pairs;
    }

    private static void addLine(final String line, final int i, final ArrayList<String> words, final ArrayList<Pair<Integer, String>> pairs) {
        String word = words.get(0);

        if (!line.contains(word)) {
            return;
        }

        final ArrayList<String> linewords = new ArrayList<String>(Arrays.asList(line.substring(line.indexOf(word)).split("\\b")));

        if (linewords.get(0).equals("")) {
            linewords.remove(0);
        }

        String lineword = linewords.remove(0);
        word = words.remove(0);

        final StringBuilder sb = new StringBuilder();

        while (word.equals(lineword)) {
            sb.append(lineword);

            if (words.isEmpty() || linewords.isEmpty()) {
                break;
            }

            word = words.remove(0);
            lineword = linewords.remove(0);
        }

        pairs.add(new Pair<Integer, String>(i, sb.toString()));
    }
}
