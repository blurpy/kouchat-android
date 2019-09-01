
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

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.usikkert.kouchat.android.R;
import net.usikkert.kouchat.android.chatwindow.AndroidUserInterface;
import net.usikkert.kouchat.android.controller.MainChatController;
import net.usikkert.kouchat.android.controller.PrivateChatController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.testclient.TestUtils;
import net.usikkert.kouchat.util.Tools;

import com.robotium.solo.Solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.text.Layout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
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
     * Clicks a menu item in the action bar. Expects the menu to be open.
     *
     * @param solo The solo tester.
     * @param title Title of the menu item to click.
     */
    public static void clickMenuItem(final Solo solo, final String title) {
        final TextView topic = RobotiumTestUtils.getTextViewWithExactText(solo, title);
        solo.clickOnView((View) topic.getParent());
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
     * Writes the text to the field with current focus.
     *
     * <p>This can be used when it's important not to change the state of the view with focus.
     * Using the regular method to enter text will put the view in a strange state where the cursor
     * disappears, and pressing 'enter' changes focus to the next view instead of adding a new line.</p>
     *
     * @param instrumentation The test instrumentation.
     * @param text The text to write.
     */
    public static void writeText(final Instrumentation instrumentation, final String text) {
        // Send one character at the time, with some sleep, to hack around some weird issue where sometimes
        // the characters end up in the wrong order
        for (int i = 0; i < text.length(); i++) {
            final String character = String.valueOf(text.charAt(i));
            instrumentation.sendStringSync(character);

            Tools.sleep(15);
        }
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
        final List<TextView> currentTextViews = solo.getCurrentViews(TextView.class);

        for (final TextView currentTextView : currentTextViews) {
            if (isTextView(currentTextView) && currentTextView.getText().toString().contains(text)) {
                return currentTextView;
            }
        }

        throw new IllegalArgumentException("Could not find TextView with text: " + text);
    }

    /**
     * Gets a textview with the extact given text.
     *
     * @param solo The solo tester.
     * @param text The text to look for in textviews.
     * @return The textview with the text.
     * @throws IllegalArgumentException If no textview was found with the given text.
     */
    public static TextView getTextViewWithExactText(final Solo solo, final String text) {
        final List<TextView> currentTextViews = solo.getCurrentViews(TextView.class);

        for (final TextView currentTextView : currentTextViews) {
            if (isTextView(currentTextView) && currentTextView.getText().toString().equals(text)) {
                return currentTextView;
            }
        }

        throw new IllegalArgumentException("Could not find TextView with text: " + text);
    }

    /**
     * Gets all the lines of text from a textview.
     *
     * @param fullText The full text from the textview. This is a separate parameter to avoid memory issues
     *                 with repeated calls to {@link TextView#getText()}.
     * @param textView The textview to get all the lines of text from.
     * @return All the lines if text in the textview. Each list item is one line.
     */
    public static List<String> getAllLinesOfText(final String fullText, final TextView textView) {
        final List<String> allLines = new ArrayList<>();

        final Layout layout = textView.getLayout();
        final int lineCount = layout.getLineCount();

        for (int currentLineNumber = 0; currentLineNumber < lineCount; currentLineNumber++) {
            allLines.add(getLineOfText(fullText, currentLineNumber, layout));
        }

        return allLines;
    }

    /**
     * Gets the text on the given line from the full text of a textview.
     *
     * @param fullText The full text from a textview.
     * @param lineNumber The line number in the textview to get the text from.
     * @param layout The layout of the textview.
     * @return The text found on the given line.
     */
    public static String getLineOfText(final String fullText, final int lineNumber, final Layout layout) {
        final int lineStart = layout.getLineStart(lineNumber);
        final int lineEnd = layout.getLineEnd(lineNumber);

        return fullText.substring(lineStart, lineEnd);
    }

    /**
     * Clicks on the given text. If the text spans multiple lines, the last line gets clicked.
     *
     * <p>Added because {@link Solo#clickOnText(String)} has issues with locating the text.
     * It will often click the wrong place. Use this method instead.</p>
     *
     * @param solo The solo tester.
     * @param textViewId Id of the textview with the text to click.
     * @param scrollViewId Id of the scrollview that contains the textview.
     * @param textToClick The text to click.
     * @throws IllegalArgumentException If the text is not visible or not found.
     */
    public static void clickOnText(final Solo solo, final int textViewId, final int scrollViewId, final String textToClick) {
        final Point coordinatesForLine = getCoordinatesForText(solo, textViewId, scrollViewId, textToClick);

        solo.clickOnScreen(coordinatesForLine.x, coordinatesForLine.y);
    }

    /**
     * Long clicks on the given text. If the text spans multiple lines, the last line gets clicked.
     *
     * <p>Added because {@link Solo#clickLongOnText(String)} has issues with locating the text.
     * It will often click the wrong place. Use this method instead.</p>
     *
     * @param solo The solo tester.
     * @param textViewId Id of the textview with the text to long click.
     * @param scrollViewId Id of the scrollview that contains the textview.
     * @param textToClick The text to long click.
     * @throws IllegalArgumentException If the text is not visible or not found.
     */
    public static void clickLongOnText(final Solo solo, final int textViewId, final int scrollViewId, final String textToClick) {
        final Point coordinatesForLine = getCoordinatesForText(solo, textViewId, scrollViewId, textToClick);

        solo.clickLongOnScreen(coordinatesForLine.x, coordinatesForLine.y);
    }

    /**
     * Searches for a textview with the given text.
     *
     * <p>Added because {@link Solo#searchText(String)} looks for text in everything that inherits from
     * textview, and that includes edittext. If it's important to look only in textviews, use this method.</p>
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
        solo.clickOnActionBarHomeButton();
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
     * Switches the entire user interface to {@link Locale#US}.
     *
     * @param activity The activity to change the language of.
     */
    public static void switchUserInterfaceToEnglish(final Activity activity) {
        Locale.setDefault(Locale.US); // Switches the decimal separators, time format and so on

        final Resources resources = activity.getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        final Configuration configuration = resources.getConfiguration();

        configuration.locale = Locale.US; // Switches the actual language shown in the user interface

        resources.updateConfiguration(configuration, displayMetrics);
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
     * Use {@link #clickOnChangeNickNameInTheSettings(Solo)} first.
     *
     * @param solo The solo tester.
     * @param nickName The nick name to change to.
     */
    public static void changeNickNameTo(final Solo solo, final String nickName) {
        solo.hideSoftKeyboard();
        solo.clearEditText(0);
        solo.enterText(0, nickName);
        solo.clickOnButton("OK");
    }

    /**
     * Opens the menu, clicks on "Topic", sets the specified topic, and clicks OK.
     *
     * @param solo The solo tester.
     * @param instrumentation The test instrumentation.
     * @param topic The topic to set.
     */
    public static void changeTopicTo(final Solo solo, final Instrumentation instrumentation, final String topic) {
        solo.sleep(100);
        openMenu(solo);

        solo.sleep(500);
        clickMenuItem(solo, "Topic");
        solo.sleep(400);

        writeText(instrumentation, topic);
        solo.sleep(200);
        solo.clickOnText("OK");
        solo.sleep(200);
    }

    /**
     * Opens the menu, clicks on "Away", sets the specified away message, and clicks OK.
     *
     * @param solo The solo tester.
     * @param instrumentation The test instrumentation.
     * @param awayMessage The away message to set.
     */
    public static void goAway(final Solo solo, final Instrumentation instrumentation, final String awayMessage) {
        solo.sleep(100);
        openMenu(solo);

        solo.sleep(500);
        clickMenuItem(solo, "Away");
        solo.sleep(400);

        writeText(instrumentation, awayMessage);
        solo.sleep(200);
        solo.clickOnText("OK");
        solo.sleep(200);
    }

    /**
     * Extracts "me" from the android user interface in an activity.
     *
     * @param activity An activity with an android user interface object.
     * @return The application user.
     */
    public static User getMe(final Activity activity) {
        final AndroidUserInterface androidUserInterface = getAndroidUserInterface(activity);

        return TestUtils.getFieldValue(androidUserInterface, User.class, "me");
    }

    /**
     * Extracts the android user interface from an activity.
     *
     * @param activity An activity with an android user interface object.
     * @return The android user interface.
     */
    public static AndroidUserInterface getAndroidUserInterface(final Activity activity) {
        return TestUtils.getFieldValue(activity, AndroidUserInterface.class, "androidUserInterface");
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
     * <p>Expects that the user is not away.</p>
     *
     * @param solo The solo tester.
     * @param instrumentation The instrumentation instance.
     * @param numberOfUsers Number of users to expect in the main chat.
     * @param userNumber The number to expect the specified user to be in the list.
     * @param userName Expected user name of the user to open the private chat with.
     */
    public static void openPrivateChat(final Solo solo, final Instrumentation instrumentation,
                                       final int numberOfUsers, final int userNumber,
                                       final String userName) {
        openPrivateChat(solo, instrumentation, numberOfUsers, userNumber, userName, null);
    }

    /**
     * Opens a private chat with the specified user.
     *
     * <p>Supports a user that is away.</p>
     *
     * @param solo The solo tester.
     * @param instrumentation The instrumentation instance.
     * @param numberOfUsers Number of users to expect in the main chat.
     * @param userNumber The number to expect the specified user to be in the list.
     * @param userName Expected user name of the user to open the private chat with.
     * @param awayMessage Expected away message of the user to open the private chat with.
     */
    public static void openPrivateChat(final Solo solo, final Instrumentation instrumentation,
                                       final int numberOfUsers, final int userNumber,
                                       final String userName, final String awayMessage) {
        solo.sleep(500);
        assertEquals(numberOfUsers, solo.getCurrentViews(ListView.class).get(0).getCount());
        solo.clickInList(userNumber);
        solo.sleep(500);

        solo.assertCurrentActivity("Should have opened the private chat", PrivateChatController.class);
        instrumentation.waitForIdleSync();
        solo.sleep(500);

        // To be sure we are chatting with the right user
        final AppCompatActivity currentActivity = (AppCompatActivity) solo.getCurrentActivity();
        final ActionBar actionBar = currentActivity.getSupportActionBar();
        assertEquals(userName, actionBar.getTitle());
        assertEquals(awayMessage, actionBar.getSubtitle());
    }

    /**
     * Checks if the text is currently visible in the scrollview.
     *
     * @param solo The solo tester.
     * @param textViewId Id of the textview with the text to check.
     * @param scrollViewId Id of the scrollview that contains the textview.
     * @param textToFind The text to check if it's visible.
     * @return If the text is currently visible.
     * @throws IllegalArgumentException If the text is not not found.
     */
    public static boolean textIsVisible(final Solo solo, final int textViewId, final int scrollViewId, final String textToFind) {
        final TextView textView = (TextView) solo.getView(textViewId);
        final ScrollView scrollView = (ScrollView) solo.getView(scrollViewId);

        final Rect visibleScrollArea = getVisibleScrollArea(scrollView);
        final String fullText = textView.getText().toString();
        final List<String> allLinesOfText = getAllLinesOfText(fullText, textView);
        final List<Line> matchingLinesOfText = getMatchingLinesOfText(fullText, allLinesOfText, textToFind);

        for (final Line matchingLine : matchingLinesOfText) {
            final Point coordinatesForLine = getCoordinatesForLine(textView, matchingLine.getLineText(),
                    matchingLine.getLineNumber(), allLinesOfText.get(matchingLine.getLineNumber()));

            if (!visibleScrollArea.contains(coordinatesForLine.x, coordinatesForLine.y)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets all the lines of text matching text to find.
     *
     * @param fullText The full text to search in.
     * @param allLinesOfText The full text, split in lines.
     * @param textToFind The text to find the lines for.
     * @return List containing the text to find, with the exact part of the text found on each line,
     *         and which line number that part of the text was found at.
     * @throws IllegalArgumentException If the text to find is not located in the full text.
     */
    public static List<Line> getMatchingLinesOfText(final String fullText, final List<String> allLinesOfText, final String textToFind) {
        final int textToFindIndex = fullText.lastIndexOf(textToFind);

        if (textToFindIndex < 0) {
            throw new IllegalArgumentException("Could not find: " + textToFind);
        }

        final int startLine = findStartLine(allLinesOfText, textToFindIndex);
        final List<Line> matchingLines = new ArrayList<>();
        final List<String> wordsFromTextToFind = splitOnBoundaries(textToFind);
        removeEmptyFirstWord(wordsFromTextToFind);

        for (int currentLineNumber = startLine; currentLineNumber < allLinesOfText.size(); currentLineNumber++) {
            addMatchingLine(allLinesOfText.get(currentLineNumber), currentLineNumber, wordsFromTextToFind, matchingLines);

            if (wordsFromTextToFind.isEmpty()) {
                break;
            }
        }

        return matchingLines;
    }

    private static int findStartLine(final List<String> allLinesOfText, final int textToFindIndex) {
        int startLine = 0;
        int currentIndex = 0;

        for (final String line : allLinesOfText) {
            if (currentIndex + line.length() >= textToFindIndex) {
                break;
            }

            startLine++;
            currentIndex += line.length();
        }

        return startLine;
    }

    private static List<String> splitOnBoundaries(final String text) {
        return new ArrayList<>(Arrays.asList(text.split("\\b")));
    }

    private static void removeEmptyFirstWord(final List<String> words) {
        if (words.get(0).equals("")) {
            words.remove(0);
        }
    }

    private static void addMatchingLine(final String currentLine, final int currentLineNumber,
                                        final List<String> wordsFromTextToFind, final List<Line> matchingLines) {
        String wordFromTextToFind = wordsFromTextToFind.get(0);

        if (!currentLine.contains(wordFromTextToFind)) {
            return;
        }

        final String currentLineStartingAtWord = currentLine.substring(currentLine.indexOf(wordFromTextToFind));
        final List<String> wordsFromCurrentLine = splitOnBoundaries(currentLineStartingAtWord);
        removeEmptyFirstWord(wordsFromCurrentLine);

        String wordFromCurrentLine = wordsFromCurrentLine.remove(0);
        wordFromTextToFind = wordsFromTextToFind.remove(0);
        final StringBuilder matchingLine = new StringBuilder();

        while (wordFromTextToFind.equals(wordFromCurrentLine)) {
            matchingLine.append(wordFromCurrentLine);

            if (wordsFromTextToFind.isEmpty() || wordsFromCurrentLine.isEmpty()) {
                break;
            }

            wordFromTextToFind = wordsFromTextToFind.remove(0);
            wordFromCurrentLine = wordsFromCurrentLine.remove(0);
        }

        matchingLines.add(new Line(currentLineNumber, matchingLine.toString()));
    }

    private static Rect getVisibleScrollArea(final ScrollView scrollView) {
        final int[] locationOnScreen = new int[2];

        scrollView.getLocationOnScreen(locationOnScreen);

        return new Rect(
                locationOnScreen[0], // left position
                locationOnScreen[1], // top position
                locationOnScreen[0] + scrollView.getWidth(), // right position
                locationOnScreen[1] + scrollView.getHeight()); // bottom position
    }

    private static Point getCoordinatesForText(final Solo solo, final int textViewId, final int scrollViewId, final String textToFind) {
        final TextView textView = (TextView) solo.getView(textViewId);
        final ScrollView scrollView = (ScrollView) solo.getView(scrollViewId);

        final Rect visibleScrollArea = getVisibleScrollArea(scrollView);
        final String fullText = textView.getText().toString();
        final List<String> allLinesOfText = getAllLinesOfText(fullText, textView);
        final List<Line> matchingLinesOfText = getMatchingLinesOfText(fullText, allLinesOfText, textToFind);
        final Line lastMatchingLine = matchingLinesOfText.get(matchingLinesOfText.size() - 1);

        final Point coordinatesForLine = getCoordinatesForLine(textView, lastMatchingLine.getLineText(),
                lastMatchingLine.getLineNumber(), allLinesOfText.get(lastMatchingLine.getLineNumber()));

        if (!visibleScrollArea.contains(coordinatesForLine.x, coordinatesForLine.y)) {
            throw new IllegalArgumentException("Text to find is not visible: " + textToFind);
        }

        return coordinatesForLine;
    }

    private static Point getCoordinatesForLine(final TextView textView, final String textToFind,
                                               final int lineNumber, final String fullLine) {
        final Layout layout = textView.getLayout();
        final TextPaint paint = textView.getPaint();

        final int textIndex = fullLine.indexOf(textToFind);
        final String preText = fullLine.substring(0, textIndex);

        final int textWidth = (int) Layout.getDesiredWidth(textToFind, paint);
        final int preTextWidth = (int) Layout.getDesiredWidth(preText, paint);

        final int[] textViewXYLocation = new int[2];
        textView.getLocationOnScreen(textViewXYLocation);

        // Width: in the middle of the text
        final int xPosition = preTextWidth + (textWidth / 2);
        // Height: in the middle of the given line, plus the text view position from the top, minus the amount scrolled
        final int yPosition = layout.getLineBaseline(lineNumber) + textViewXYLocation[1] - textView.getScrollY();

        return new Point(xPosition, yPosition);
    }

    private static boolean isTextView(final TextView textView) {
        return textView.getClass().equals(TextView.class) ||
                textView.getClass().equals(AppCompatTextView.class);
    }
}
