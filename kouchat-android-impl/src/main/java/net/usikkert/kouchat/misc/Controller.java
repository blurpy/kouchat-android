
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

package net.usikkert.kouchat.misc;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.autocomplete.AutoCompleter;
import net.usikkert.kouchat.autocomplete.CommandAutoCompleteList;
import net.usikkert.kouchat.autocomplete.UserAutoCompleteList;
import net.usikkert.kouchat.event.NetworkConnectionListener;
import net.usikkert.kouchat.jmx.JMXBeanLoader;
import net.usikkert.kouchat.net.DefaultMessageResponder;
import net.usikkert.kouchat.net.DefaultPrivateMessageResponder;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.MessageParser;
import net.usikkert.kouchat.net.MessageResponder;
import net.usikkert.kouchat.net.Messages;
import net.usikkert.kouchat.net.NetworkService;
import net.usikkert.kouchat.net.PrivateMessageParser;
import net.usikkert.kouchat.net.PrivateMessageResponder;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This controller gives access to the network and the state of the
 * application, like the user list and the topic.
 * <br><br>
 * When changing state, use the methods available <strong>here</strong> instead
 * of doing it manually, to make sure the state is consistent.
 * <br><br>
 * To connect to the network, use {@link #logOn()}.
 *
 * @author Christian Ihle
 */
public class Controller implements NetworkConnectionListener {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(Controller.class.getName());

    private final ChatState chatState;
    private final UserListController userListController;
    private final NetworkService networkService;
    private final Messages messages;
    private final IdleThread idleThread;
    private final TransferList tList;
    private final WaitingList wList;
    private final User me;
    private final UserInterface ui;
    private final MessageController msgController;
    private final Settings settings;

    /**
     * Constructor. Initializes the controller, but does not log on to
     * the network.
     *
     * @param ui The active user interface object.
     * @param settings The settings to use.
     */
    public Controller(final UserInterface ui, final Settings settings) {
        Validate.notNull(ui, "User interface can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.ui = ui;
        this.settings = settings;

        Runtime.getRuntime().addShutdownHook(new Thread("ControllerShutdownHook") {
            @Override
            public void run() {
                logOff(false);
                shutdown();
            }
        });

        me = settings.getMe();
        userListController = new UserListController(settings);
        chatState = new ChatState();
        tList = new TransferList();
        wList = new WaitingList();
        idleThread = new IdleThread(this, ui, settings);
        networkService = new NetworkService(settings);
        final MessageResponder msgResponder = new DefaultMessageResponder(this, ui, settings);
        final PrivateMessageResponder privmsgResponder = new DefaultPrivateMessageResponder(this, ui, settings);
        final MessageParser msgParser = new MessageParser(msgResponder, settings);
        networkService.registerMessageReceiverListener(msgParser);
        final PrivateMessageParser privmsgParser = new PrivateMessageParser(privmsgResponder, settings);
        networkService.registerUDPReceiverListener(privmsgParser);
        messages = new Messages(networkService, settings);
        networkService.registerNetworkConnectionListener(this);
        msgController = ui.getMessageController();

        new DayTimer(ui);
        idleThread.start();

        msgController.showSystemMessage("Welcome to " + Constants.APP_NAME + " v" + Constants.APP_VERSION + "!");
        final String date = Tools.dateToString(null, "EEEE, d MMMM yyyy");
        msgController.showSystemMessage("Today is " + date);
    }

    /**
     * Gets the current topic.
     *
     * @return The current topic.
     */
    public Topic getTopic() {
        return chatState.getTopic();
    }

    /**
     * Gets the list of online users.
     *
     * @return The user list.
     */
    public UserList getUserList() {
        return userListController.getUserList();
    }

    /**
     * Returns if the application user wrote the last time
     * {@link #changeWriting(int, boolean)} was called.
     *
     * @return If the user wrote.
     * @see ChatState#isWrote()
     */
    public boolean isWrote() {
        return chatState.isWrote();
    }

    /**
     * Updates the write state for the user. This is useful to see which
     * users are currently writing.
     *
     * If the user is the application user, messages will be sent to the
     * other clients to notify of changes.
     *
     * @param code The user code for the user to update.
     * @param writing True if the user is writing.
     */
    public void changeWriting(final int code, final boolean writing) {
        userListController.changeWriting(code, writing);

        if (code == me.getCode()) {
            chatState.setWrote(writing);

            if (writing) {
                messages.sendWritingMessage();
            } else {
                messages.sendStoppedWritingMessage();
            }
        }
    }

    /**
     * Updates whether the user is currently writing or not. This makes sure a star is shown
     * by the nick name in the user list, and sends a notice to other users so they can show the same thing.
     *
     * @param isCurrentlyWriting If the application user is currently writing.
     */
    public void updateMeWriting(final boolean isCurrentlyWriting) {
        if (isCurrentlyWriting) {
            if (!isWrote()) {
                changeWriting(me.getCode(), true);
            }
        }

        else {
            if (isWrote()) {
                changeWriting(me.getCode(), false);
            }
        }
    }

    /**
     * Updates the away status and the away message for the user.
     *
     * @param code The user code for the user to update.
     * @param away If the user is away or not.
     * @param awaymsg The away message for that user.
     * @throws CommandException If there is no connection to the network,
     *         or the user tries to set an away message that is to long.
     */
    public void changeAwayStatus(final int code, final boolean away, final String awaymsg) throws CommandException {
        if (code == me.getCode() && !isLoggedOn()) {
            throw new CommandException("You can not change away mode without being connected");
        } else if (Tools.getBytes(awaymsg) > Constants.MESSAGE_MAX_BYTES) {
            throw new CommandException("You can not set an away message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes");
        }

        if (code == me.getCode()) {
            if (away) {
                messages.sendAwayMessage(awaymsg);
            } else {
                messages.sendBackMessage();
            }
        }

        userListController.changeAwayStatus(code, away, awaymsg);
    }

    /**
     * Checks if the nick is in use by another user.
     *
     * @param nick The nick to check.
     * @return True if the nick is already in use.
     */
    public boolean isNickInUse(final String nick) {
        return userListController.isNickNameInUse(nick);
    }

    /**
     * Checks if the user with that user code is already in the user list.
     *
     * @param code The user code of the user to check.
     * @return True if the user is not in the user list.
     */
    public boolean isNewUser(final int code) {
        return userListController.isNewUser(code);
    }

    /**
     * Changes the nick for the application user, sends a message over the
     * network to notify the other clients of the change, and saves the changes.
     *
     * @param newNick The new nick for the application user.
     * @throws CommandException If the user is away.
     */
    public void changeMyNick(final String newNick) throws CommandException {
        if (me.isAway()) {
            throw new CommandException("You can not change nick while away");
        }

        messages.sendNickMessage(newNick);
        changeNick(me.getCode(), newNick);
        settings.saveSettings();
    }

    /**
     * Changes the nick of the user.
     *
     * @param code The user code for the user.
     * @param nick The new nick for the user.
     */
    public void changeNick(final int code, final String nick) {
        userListController.changeNickName(code, nick);
    }

    /**
     * Gets the user with the specified user code.
     *
     * @param code The user code for the user.
     * @return The user with the specified user code, or <em>null</em> if not found.
     */
    public User getUser(final int code) {
        return userListController.getUser(code);
    }

    /**
     * Gets the user with the specified nick name.
     *
     * @param nick The nick name to check for.
     * @return The user with the specified nick name, or <em>null</em> if not found.
     */
    public User getUser(final String nick) {
        return userListController.getUser(nick);
    }

    /**
     * Sends the necessary network messages to log the user onto the network
     * and query for the users and state.
     */
    private void sendLogOn() {
        messages.sendLogonMessage();
        messages.sendClient();
        messages.sendExposeMessage();
        messages.sendGetTopicMessage();
    }

    /**
     * This should be run after a successful logon, to update the connection state.
     */
    private void runDelayedLogon() {
        final Timer delayedLogonTimer = new Timer("DelayedLogonTimer");
        delayedLogonTimer.schedule(new DelayedLogonTask(), 0);
    }

    /**
     * Logs this client onto the network.
     */
    public void logOn() {
        if (!networkService.isConnectionWorkerAlive()) {
            networkService.connect();
        }
    }

    /**
     * Logs this client off the network.
     *
     * <br /><br />
     *
     * <strong>Note:</strong> removeUsers should not be true when called
     * from a ShutdownHook, as that will lead to a deadlock. See
     * http://bugs.sun.com/bugdatabase/view_bug.do;?bug_id=6261550 for details.
     *
     * @param removeUsers Set to true to remove users from the user list.
     */
    public void logOff(final boolean removeUsers) {
        messages.sendLogoffMessage();
        chatState.setLoggedOn(false);
        chatState.setLogonCompleted(false);
        networkService.disconnect();
        getTopic().resetTopic();
        if (removeUsers) {
            removeAllUsers();
        }
        me.reset();
    }

    /**
     * Cancels all file transfers, sets all users as logged off,
     * and removes them from the user list.
     */
    private void removeAllUsers() {
        final UserList userList = getUserList();

        for (int i = 0; i < userList.size(); i++) {
            final User user = userList.get(i);

            if (!user.isMe()) {
                user.setOnline(false);
                cancelFileTransfers(user);
                userList.remove(user);

                if (user.getPrivchat() != null) {
                    msgController.showPrivateSystemMessage(user, "You logged off");
                    user.getPrivchat().setLoggedOff();
                }

                i--;
            }
        }
    }

    /**
     * Cancels all file transfers for that user.
     *
     * @param user The user to cancel for.
     */
    public void cancelFileTransfers(final User user) {
        final List<FileSender> fsList = tList.getFileSenders(user);
        final List<FileReceiver> frList = tList.getFileReceivers(user);

        for (final FileSender fs : fsList) {
            fs.cancel();
            tList.removeFileSender(fs);
        }

        for (final FileReceiver fr : frList) {
            fr.cancel();
            tList.removeFileReceiver(fr);
        }
    }

    /**
     * Prepares the application for shutdown.
     * Should <strong>only</strong> be called when the application shuts down.
     */
    private void shutdown() {
        idleThread.stopThread();
    }

    /**
     * Sends a message over the network, asking the other clients to identify
     * themselves.
     */
    public void sendExposeMessage() {
        messages.sendExposeMessage();
    }

    /**
     * Sends a message over the network to identify this client.
     */
    public void sendExposingMessage() {
        messages.sendExposingMessage();
    }

    /**
     * Sends a message over the network to ask for the current topic.
     */
    public void sendGetTopicMessage() {
        messages.sendGetTopicMessage();
    }

    /**
     * Sends a message over the network to notify other clients that this
     * client is still alive.
     */
    public void sendIdleMessage() {
        if (isConnected()) {
            messages.sendIdleMessage();
        }
    }

    /**
     * Sends a chat message over the network, to all the other users.
     *
     * @param msg The message to send.
     * @throws CommandException If there is no connection to the network,
     *         or the application user is away,
     *         or the message is empty,
     *         or the message is too long.
     */
    public void sendChatMessage(final String msg) throws CommandException {
        if (!isConnected()) {
            throw new CommandException("You can not send a chat message without being connected");
        } else if (me.isAway()) {
            throw new CommandException("You can not send a chat message while away");
        } else if (msg.trim().length() == 0) {
            throw new CommandException("You can not send an empty chat message");
        } else if (Tools.getBytes(msg) > Constants.MESSAGE_MAX_BYTES) {
            throw new CommandException("You can not send a chat message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes");
        } else {
            messages.sendChatMessage(msg);
        }
    }

    /**
     * Sends a message over the network with the current topic.
     */
    public void sendTopicRequestedMessage() {
        messages.sendTopicRequestedMessage(getTopic());
    }

    /**
     * Changes the topic, and sends a notification to the other clients.
     *
     * @param newTopic The new topic to set.
     * @throws CommandException If there is no connection to the network,
     *         or the application user is away,
     *         or the topic is too long.
     */
    public void changeTopic(final String newTopic) throws CommandException {
        if (!isLoggedOn()) {
            throw new CommandException("You can not change the topic without being connected");
        } else if (me.isAway()) {
            throw new CommandException("You can not change the topic while away");
        } else if (Tools.getBytes(newTopic) > Constants.MESSAGE_MAX_BYTES) {
            throw new CommandException("You can not set a topic with more than " + Constants.MESSAGE_MAX_BYTES + " bytes");
        }

        final long time = System.currentTimeMillis();
        final Topic newTopicObj = new Topic(newTopic, me.getNick(), time);
        messages.sendTopicChangeMessage(newTopicObj);
        final Topic topic = getTopic();
        topic.changeTopic(newTopicObj);
    }

    /**
     * Sends a message over the network to notify the other clients that
     * a client has tried to logon using the nick name of the
     * application user.
     *
     * @param nick The nick that is already in use by the application user.
     */
    public void sendNickCrashMessage(final String nick) {
        messages.sendNickCrashMessage(nick);
    }

    /**
     * Sends a message over the network to notify the file sender that you
     * aborted the file transfer.
     *
     * @param user The user sending a file.
     * @param fileHash The unique hash code of the file.
     * @param fileName The name of the file.
     */
    public void sendFileAbort(final User user, final int fileHash, final String fileName) {
        messages.sendFileAbort(user, fileHash, fileName);
    }

    /**
     * Sends a message over the network to notify the file sender that you
     * accepted the file transfer.
     *
     * @param user The user sending a file.
     * @param port The port the file sender can connect to on this client
     *             to start the file transfer.
     * @param fileHash The unique hash code of the file.
     * @param fileName The name of the file.
     * @throws CommandException If the message was not sent successfully.
     */
    public void sendFileAccept(final User user, final int port, final int fileHash, final String fileName) throws CommandException {
        messages.sendFileAccept(user, port, fileHash, fileName);
    }

    /**
     * Sends a message over the network to notify another user that the
     * application user wants to send a file.
     *
     * @param user The user asked to receive a file.
     * @param file The file to send.
     * @throws CommandException If the specified user is the application user,
     *                          or there is no connection to the network,
     *                          or the application user is away,
     *                          or the specified user is away,
     *                          or the file name is too long.
     */
    public void sendFile(final User user, final File file) throws CommandException {
        Validate.notNull(user, "User can not be null");
        Validate.notNull(file, "File can not be null");

        if (user.isMe()) {
            throw new CommandException("You can not send a file to yourself");
        } else if (!isConnected()) {
            throw new CommandException("You can not send a file without being connected");
        } else if (me.isAway()) {
            throw new CommandException("You can not send a file while away");
        } else if (user.isAway()) {
            throw new CommandException("You can not send a file to a user that is away");
        } else if (Tools.getBytes(file.getName()) > Constants.MESSAGE_MAX_BYTES) {
            throw new CommandException("You can not send a file with a name with more than " + Constants.MESSAGE_MAX_BYTES + " bytes");
        } else {
            messages.sendFile(user, file);
        }
    }

    /**
     * Gets the list of current transfers.
     *
     * @return The list of transfers.
     */
    public TransferList getTransferList() {
        return tList;
    }

    /**
     * Gets the list of unidentified users.
     *
     * @return The list of unidentified users.
     */
    public WaitingList getWaitingList() {
        return wList;
    }

    /**
     * If any users have timed out because of missed idle messages, then
     * send a message over the network to ask all clients to identify
     * themselves again.
     */
    public void updateAfterTimeout() {
        if (userListController.isTimeoutUsers()) {
            messages.sendExposeMessage();
        }
    }

    /**
     * Sends a message over the network with more information about this client.
     */
    public void sendClientInfo() {
        messages.sendClient();
    }

    /**
     * Sends a private chat message over the network, to the specified user.
     *
     * @param privmsg The private message to send.
     * @param user The user to send the private message to.
     * @throws CommandException If there is no connection to the network,
     *                          or the application user is away,
     *                          or the private message is empty,
     *                          or the private message is too long,
     *                          or the specified user has no port to send the private message to,
     *                          or the specified user is away or offline.
     */
    public void sendPrivateMessage(final String privmsg, final User user) throws CommandException {
        if (!isConnected()) {
            throw new CommandException("You can not send a private chat message without being connected");
        } else if (me.isAway()) {
            throw new CommandException("You can not send a private chat message while away");
        } else if (privmsg.trim().length() == 0) {
            throw new CommandException("You can not send an empty private chat message");
        } else if (Tools.getBytes(privmsg) > Constants.MESSAGE_MAX_BYTES) {
            throw new CommandException("You can not send a private chat message with more than " + Constants.MESSAGE_MAX_BYTES + " bytes");
        } else if (user.getPrivateChatPort() == 0) {
            throw new CommandException("You can not send a private chat message to a user with no available port number");
        } else if (user.isAway()) {
            throw new CommandException("You can not send a private chat message to a user that is away");
        } else if (!user.isOnline()) {
            throw new CommandException("You can not send a private chat message to a user that is offline");
        } else if (settings.isNoPrivateChat()) {
            throw new CommandException("You can not send a private chat message when private chat is disabled");
        } else {
            messages.sendPrivateMessage(privmsg, user);
        }
    }

    /**
     * Updates if the user has unread private messages for the
     * application user.
     *
     * @param code The user code for the user to update.
     * @param newMsg True if the user has unread private messages.
     */
    public void changeNewMessage(final int code, final boolean newMsg) {
        userListController.changeNewMessage(code, newMsg);
    }

    /**
     * Returns if the client is logged on to the chat and connected to the network.
     *
     * @return True if the client is connected.
     */
    public boolean isConnected() {
        return networkService.isNetworkUp() && isLoggedOn();
    }

    /**
     * Checks the state of the network, and tries to keep the best possible
     * network connection up.
     */
    public void checkNetwork() {
        networkService.checkNetwork();
    }

    /**
     * Returns if the client is logged on to the chat.
     *
     * @return True if the client is logged on to the chat.
     */
    public boolean isLoggedOn() {
        return chatState.isLoggedOn();
    }

    /**
     * This timer task sleeps for 1.5 seconds before updating the
     * {@link WaitingList} to set the status to logged on if the
     * client was successful in connecting to the network.
     *
     * @author Christian Ihle
     */
    private class DelayedLogonTask extends TimerTask {
        /**
         * The task runs as a thread.
         */
        @Override
        public void run() {
            try {
                Thread.sleep(1500);
            }

            catch (final InterruptedException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }

            if (networkService.isNetworkUp()) {
                chatState.setLogonCompleted(true);
                // To stop the timer from running in the background
                cancel();
            }
        }
    }

    /**
     * Creates a new instance of the {@link AutoCompleter}, with
     * a {@link CommandAutoCompleteList} and a {@link UserAutoCompleteList}.
     *
     * @return A new instance of a ready-to-use AutoCompleter.
     */
    public AutoCompleter getAutoCompleter() {
        final AutoCompleter autoCompleter = new AutoCompleter();
        autoCompleter.addAutoCompleteList(new CommandAutoCompleteList());
        autoCompleter.addAutoCompleteList(new UserAutoCompleteList(getUserList()));

        return autoCompleter;
    }

    @Override
    public void beforeNetworkCameUp() {
        // Nothing to do here
    }

    /**
     * Makes sure the application reacts when the network is available.
     *
     * @param silent If true, wont show the "you are connected..." message to the user.
     */
    @Override
    public void networkCameUp(final boolean silent) {
        // Network came up after a logon
        if (!isLoggedOn()) {
            runDelayedLogon();
            sendLogOn();
        }

        // Network came up after a timeout
        else {
            ui.showTopic();

            if (!silent) {
                msgController.showSystemMessage("You are connected to the network again");
            }

            messages.sendTopicRequestedMessage(getTopic());
            messages.sendExposingMessage();
            messages.sendGetTopicMessage();
            messages.sendExposeMessage();
            messages.sendIdleMessage();
        }
    }

    /**
     * Makes sure the application reacts when the network is unavailable.
     *
     * @param silent If true, wont show the "you lost contact..." message to the user.
     */
    @Override
    public void networkWentDown(final boolean silent) {
        ui.showTopic();

        if (isLoggedOn()) {
            if (!silent) {
                msgController.showSystemMessage("You lost contact with the network");
            }
        }

        else {
            msgController.showSystemMessage("You logged off");
        }
    }

    /**
     * Gets the chat state.
     *
     * @return The chat state.
     */
    public ChatState getChatState() {
        return chatState;
    }

    /**
     * Creates an instance of a JMX bean loader, and returns it.
     *
     * @return A JMX bean loader.
     */
    public JMXBeanLoader createJMXBeanLoader() {
        return new JMXBeanLoader(this, networkService.getConnectionWorker(), settings);
    }

    public void registerNetworkConnectionListener(final NetworkConnectionListener listener) {
        networkService.registerNetworkConnectionListener(listener);
    }
}
