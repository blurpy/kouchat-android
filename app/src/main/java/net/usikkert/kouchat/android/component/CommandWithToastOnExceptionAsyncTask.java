
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

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.util.Validate;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * An {@link AsyncTask} that runs a {@link Command}, and shows a toast with the exception if it occurs.
 *
 * <p>{@link #get()} returns <code>true</code> if the command succeeded, and <code>false</code>
 * if it failed with an exception.</p>
 *
 * @author Christian Ihle
 */
public class CommandWithToastOnExceptionAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;
    private final Command command;

    private CommandException exception;

    /**
     * Initializes this {@link AsyncTask}.
     *
     * @param context The context to use for showing the toast.
     * @param command The command to run.
     */
    public CommandWithToastOnExceptionAsyncTask(final Context context, final Command command) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(command, "Command can not be null");

        this.context = context;
        this.command = command;
    }

    @Override
    protected Boolean doInBackground(final Void... params) {
        try {
            command.runCommand();
            return true;
        } catch (final CommandException e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean aBoolean) {
        if (exception != null) { // Toast needs to be on UI thread, like in onPostExecute()
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
