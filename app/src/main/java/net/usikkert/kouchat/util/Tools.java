
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

package net.usikkert.kouchat.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.usikkert.kouchat.Constants;

import org.jetbrains.annotations.Nullable;

/**
 * A collection of static utility methods.
 *
 * @author Christian Ihle
 */
public final class Tools {

    private static final Logger LOG = Logger.getLogger(Tools.class.getName());
    private static final Pattern VALID_NICK = Pattern.compile("[\\p{Alnum}[-_]]{1,10}");

    /**
     * Private constructor. Only static methods here.
     */
    private Tools() {

    }

    /**
     * Creates a timestamp in the format [HH:MM:SS].
     *
     * @return The current time.
     */
    public static String getTime() {
        final int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int m = Calendar.getInstance().get(Calendar.MINUTE);
        final int s = Calendar.getInstance().get(Calendar.SECOND);

        return "[" + getDoubleDigit(h) + ":" + getDoubleDigit(m) + ":" + getDoubleDigit(s) + "]";
    }

    /**
     * Checks if a number is lower than 10, and creates a string with
     * a 0 added at the start if that is the case. Useful for clocks.
     *
     * @param number The number to check.
     * @return A string representation of the number.
     */
    public static String getDoubleDigit(final int number) {
        if (number < 10) {
            return "0" + number;
        }

        else {
            return "" + number;
        }
    }

    /**
     * Converts a date to a string, in the format specified.
     *
     * @param d The date to convert to a string.
     * @param format The format to get the date in.
     * @return A converted date.
     * @see SimpleDateFormat
     */
    public static String dateToString(@Nullable final Date d, final String format) {
        String date = "";
        final SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

        if (d == null) {
            date = formatter.format(new Date());
        }

        else {
            date = formatter.format(d);
        }

        return date;
    }

    /**
     * Get a decimal number as a string, in the specified format.
     *
     * @param format The format to get the number.
     * @param number The number to add formatting to.
     * @return The formatted number.
     * @see DecimalFormat
     */
    public static String decimalFormat(final String format, final double number) {
        final DecimalFormat formatter = new DecimalFormat(format);
        return formatter.format(number);
    }

    /**
     * Nick is valid if it consists of between 1 and 10 characters
     * of type [a-Z], [0-9], '-' and '_'.
     *
     * @param nick The nick to check.
     * @return If the nick is valid.
     */
    public static boolean isValidNick(@Nullable final String nick) {
        if (nick == null) {
            return false;
        }

        final Matcher m = VALID_NICK.matcher(nick);
        return m.matches();
    }

    /**
     * Converts a number of bytes into megabytes or kilobytes,
     * depending on the size.
     *
     * @param bytes The number of bytes to convert.
     * @return A string representation of the bytes.
     */
    public static String byteToString(final long bytes) {
        String size = "";
        double kbSize = bytes / 1024.0;

        if (kbSize > 1024) {
            kbSize /= 1024;
            size = decimalFormat("0.00", kbSize) + "MB";
        }

        else {
            size = decimalFormat("0.00", kbSize) + "KB";
        }

        return size;
    }

    /**
     * Returns the number of bytes a String consists of.
     *
     * @param text The text to count the bytes in.
     * @return Number of bytes found in the text.
     */
    public static int getBytes(final String text) {
        try {
            return text.getBytes(Constants.MESSAGE_CHARSET).length;
        }

        catch (final UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
            return 0;
        }
    }

    /**
     * Calls {@link Thread#sleep(long)}, and ignores any exceptions.
     *
     * @param millis Number of milliseconds to sleep.
     */
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        }

        catch (final InterruptedException e) {
            LOG.log(Level.WARNING, e.toString());
        }
    }

    /**
     * Capitalizes the first letter in a word.
     *
     * @param word The word to capitalize the first letter of.
     * @return The modified word.
     */
    @Nullable
    public static String capitalizeFirstLetter(@Nullable final String word) {
        if (word == null) {
            return null;
        }

        if (word.length() == 0) {
            return word;
        }

        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    /**
     * Shortens a word to the specified number of characters.
     *
     * @param word The word to shorten.
     * @param length The max number of characters for the word.
     * @return The modified word.
     */
    @Nullable
    public static String shorten(@Nullable final String word, final int length) {
        if (word == null) {
            return null;
        }

        if (length < 0) {
            return "";
        }

        if (word.length() <= length) {
            return word;
        }

        return word.substring(0, length);
    }

    /**
     * Gets the file extension from a file name. Including the separator dot.
     *
     * @param filename The file name to get the extension from.
     * @return The file extension, or <code>null</code> if file name is <code>null</code>.
     */
    @Nullable
    public static String getFileExtension(@Nullable final String filename) {
        if (filename == null) {
            return null;
        }

        final int dotIndex = filename.lastIndexOf(".");

        if (dotIndex == -1) {
            return "";
        }

        return filename.substring(dotIndex);
    }

    /**
     * Gets the base file name without the extension from a full file name.
     * Without the separator dot.
     *
     * @param filename The file name to get the base name from.
     * @return The base name, or <code>null</code> if file name is <code>null</code>.
     */
    @Nullable
    public static String getFileBaseName(@Nullable final String filename) {
        if (filename == null) {
            return null;
        }

        final int dotIndex = filename.lastIndexOf(".");

        if (dotIndex == -1) {
            return filename;
        }

        return filename.substring(0, dotIndex);
    }

    /**
     * Finds how many percent a fraction is of the total.
     *
     * <p>Example: percent(50, 200) returns 25.</p>
     *
     * @param fraction The fraction of the total to find the percentage of.
     * @param total The total.
     * @return How many percent the fraction is of the total.
     */
    public static double percent(final double fraction, final double total) {
        return (100.0 / total) * fraction;
    }

    /**
     * Finds the fraction from the percent of the total.
     *
     * <p>Example: percentOf(25, 200) returns 50.</p>
     *
     * @param percent How many percent of the total to get the fraction from.
     * @param total The total.
     * @return The fraction as percent of the total.
     */
    public static double percentOf(final double percent, final double total) {
        return (percent / 100.0) * total;
    }

    /**
     * Add padding at the end of the stringToPad, until it reaches stringLength.
     *
     * @param stringToPad The string to add padding to.
     * @param paddedLength The length of the string, after padding.
     * @return The padded string.
     */
    public static String postPadString(final String stringToPad, final int paddedLength) {
        final int missingPadding = paddedLength - stringToPad.length();
        final StringBuilder paddedString = new StringBuilder(stringToPad);

        for (int i = 0; i < missingPadding; i++) {
            paddedString.append(" ");
        }

        return paddedString.toString();
    }

    /**
     * Checks if the string is empty. It's considered empty if it's <code>null</code>,
     * zero length, or contains only spaces.
     *
     * @param string The string to check.
     * @return If the string is empty.
     */
    public static boolean isEmpty(@Nullable final String string) {
        return string == null || string.trim().isEmpty();
    }

    /**
     * Appends the platform specific slash to the path, if missing.
     *
     * @param path The path to append the slash to.
     * @return The path, including a slash on the end.
     */
    public static String appendSlash(final String path) {
        final String fileSeparator = System.getProperty("file.separator");

        if (path.endsWith(fileSeparator)) {
            return path;
        }

        else {
            return path + fileSeparator;
        }
    }

    /**
     * Returns a new file with the same name and path as the existingFile, but with an incremented counter
     * at the end of the name. The name <code>file.txt</code> becomes <code>file_1.txt</code> if it's available.
     * If not, then the counter increments until a free name is found.
     *
     * <p>Returns the original file if it doesn't exist.</p>
     *
     * @param existingFile The (possibly) existing file to find a new free name for.
     * @return A new file, with a free, non existing name, or the same file if it doesn't exist.
     */
    public static File getFileWithIncrementedName(final File existingFile) {
        Validate.notNull(existingFile, "The existing file to increment the name of can not be null");

        if (!existingFile.exists()) {
            return existingFile;
        }

        int counter = 1;
        File newFile;
        final String path;

        if (existingFile.getParent() == null) {
            path = "";
        } else {
            path = existingFile.getParent() + File.separator;
        }

        do {
            final String existingName = existingFile.getName();
            final String newName = String.format("%s%s_%s%s",
                    path, getFileBaseName(existingName), counter, getFileExtension(existingName));

            newFile = new File(newName);
            counter++;
        }
        while (newFile.exists());

        return newFile;
    }

    /**
     * Returns an empty string if the input is null.
     *
     * @param string The string to check.
     * @return An empty string if the input is null, or the input string if not.
     */
    public static String emptyIfNull(@Nullable final String string) {
        if (string == null) {
            return "";
        }

        return string;
    }
}
