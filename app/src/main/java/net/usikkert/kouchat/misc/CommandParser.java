
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

package net.usikkert.kouchat.misc;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.message.CoreMessages;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileToSend;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.net.TransferList;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.DateTools;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * Parses and executes commands. A command starts with a slash, and can
 * have arguments.
 *
 * @author Christian Ihle
 */
public class CommandParser {

    private static final String WHITESPACE = "\\s"; // Any whitespace character

    private final DateTools dateTools = new DateTools();

    private final Controller controller;
    private final UserInterface ui;
    private final MessageController msgController;
    private final User me;
    private final TransferList tList;
    private final Settings settings;
    private final CoreMessages coreMessages;

    /**
     * Constructor.
     *
     * @param controller The controller.
     * @param ui The user interface.
     * @param settings The settings to use.
     * @param coreMessages The messages to use.
     */
    public CommandParser(final Controller controller, final UserInterface ui, final Settings settings,
                         final CoreMessages coreMessages) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(ui, "UserInterface can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(coreMessages, "Core messages can not be null");

        this.controller = controller;
        this.ui = ui;
        this.settings = settings;
        this.coreMessages = coreMessages;

        msgController = ui.getMessageController();
        me = settings.getMe();
        tList = controller.getTransferList();
    }

    /**
     * Command: <em>/topic &lt;optional new topic&gt;</em>.
     *
     * <p>Prints the current topic if no arguments are supplied,
     * or changes the topic. To remove the topic, use a space as the argument.</p>
     *
     * @param args Nothing, or the new topic.
     */
    private void cmdTopic(final String args) {
        if (args.length() == 0) {
            final Topic topic = controller.getTopic();

            if (topic.getTopic().equals("")) {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.topic.systemMessage.noTopic"));
            }

            else {
                final String date = dateTools.dateToString(new Date(topic.getTime()),
                                                           coreMessages.getMessage("core.dateFormat.topic"));
                msgController.showSystemMessage(coreMessages.getMessage("core.command.topic.systemMessage.topicIs",
                                                                        topic.getTopic(), topic.getNick(), date));
            }
        }

        else {
            try {
                fixTopic(args);
            }

            catch (final CommandException e) {
                msgController.showSystemMessage(e.getMessage());
            }
        }
    }

    /**
     * Command: <em>/away &lt;away message&gt;</em>.
     *
     * <p>Set status to away.</p>
     *
     * @param args The away message.
     */
    private void cmdAway(final String args) {
        if (me.isAway()) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.away.systemMessage.alreadyAway",
                                                                    me.getAwayMsg()));
        }

        else {
            if (args.trim().length() == 0) {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.away.systemMessage.missingArgument"));
            }

            else {
                try {
                    controller.goAway(args.trim());
                }

                catch (final CommandException e) {
                    msgController.showSystemMessage(e.getMessage());
                }
            }
        }
    }

    /**
     * Command: <em>/back</em>.
     *
     * <p>Set status to not away.</p>
     */
    private void cmdBack() {
        if (me.isAway()) {
            try {
                controller.comeBack();
            }

            catch (final CommandException e) {
                msgController.showSystemMessage(e.getMessage());
            }
        }

        else {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.back.systemMessage.notAway"));
        }
    }

    /**
     * Command: <em>/clear</em>.
     *
     * <p>Clear all the text from the chat.</p>
     */
    private void cmdClear() {
        ui.clearChat();
    }

    /**
     * Command: <em>/about</em>.
     *
     * <p>Show information about the application.</p>
     */
    private void cmdAbout() {
        msgController.showSystemMessage(coreMessages.getMessage("core.command.about.systemMessage.about",
                                                                Constants.APP_NAME, Constants.APP_VERSION,
                                                                Constants.AUTHOR_NAME, Constants.AUTHOR_MAIL,
                                                                Constants.APP_WEB));
    }

    /**
     * Command: <em>/help</em>.
     *
     * <p>Shows a list of commands.</p>
     */
    private void cmdHelp() {
        showCommands();
    }

    /**
     * Command: <em>/whois &lt;nick&gt;</em>.
     *
     * <p>Show information about a user.</p>
     *
     * @param args The user to show information about.
     */
    private void cmdWhois(final String args) {
        if (args.trim().length() == 0) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.whois.systemMessage.missingArgument"));
        }

        else {
            final String[] argsArray = args.split(WHITESPACE);
            final String nick = argsArray[1].trim();

            final User user = controller.getUser(nick);

            if (user == null) {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.whois.systemMessage.noSuchUser",
                                                                        nick));
            }

            else {
                String info;

                if (user.isAway()) {
                    info = coreMessages.getMessage("core.command.whois.systemMessage.whois.away", user.getNick());
                } else {
                    info = coreMessages.getMessage("core.command.whois.systemMessage.whois", user.getNick());
                }

                info += "\n" + coreMessages.getMessage("core.command.whois.ipAddress", user.getIpAddress());

                if (user.getHostName() != null) {
                    info += "\n" + coreMessages.getMessage("core.command.whois.hostName", user.getHostName());
                }

                info += "\n" + coreMessages.getMessage("core.command.whois.client", user.getClient());

                if (user.isTcpEnabled()) {
                    info += " " + coreMessages.getMessage("core.command.whois.tcp");
                }

                info += "\n" + coreMessages.getMessage("core.command.whois.operatingSystem", user.getOperatingSystem());
                info += "\n" + coreMessages.getMessage("core.command.whois.online",
                                                       dateTools.howLongFromNow(user.getLogonTime()));

                if (user.isAway()) {
                    info += "\n" + coreMessages.getMessage("core.command.whois.awayMessage", user.getAwayMsg());
                }

                msgController.showSystemMessage(info);
            }
        }
    }

    /**
     * Command: <em>/send &lt;nick&gt; &lt;file&gt;</em>.
     *
     * <p>Send a file to a user.</p>
     *
     * @param args First argument is the user to send to, and the second is the file to send to the user.
     */
    private void cmdSend(final String args) {
        final String[] argsArray = args.split(WHITESPACE);

        if (argsArray.length <= 2) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.send.systemMessage.missingArguments"));
        }

        else {
            final String nick = argsArray[1];
            final User user = controller.getUser(nick);

            if (user != me) {
                if (user == null) {
                    msgController.showSystemMessage(
                            coreMessages.getMessage("core.command.send.systemMessage.noSuchUser", nick));
                }

                else {
                    String file = "";

                    for (int i = 2; i < argsArray.length; i++) {
                        file += argsArray[i] + " ";
                    }

                    file = file.trim();
                    final File sendFile = new File(file);

                    if (sendFile.exists() && sendFile.isFile()) {
                        try {
                            sendFile(user, new FileToSend(sendFile));
                        }

                        catch (final CommandException e) {
                            msgController.showSystemMessage(e.getMessage());
                        }
                    }

                    else {
                        msgController.showSystemMessage(
                                coreMessages.getMessage("core.command.send.systemMessage.noSuchFile", file));
                    }
                }
            }

            else {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.send.systemMessage.userIsMe"));
            }
        }
    }

    /**
     * Command: <em>/receive &lt;nick&gt; &lt;id&gt;</em>.
     *
     * <p>Accept a file transfer request from a user and start the transfer.</p>
     *
     * @param args First argument is the other user in the file transfer, and the second is the id of the file transfer.
     */
    private void cmdReceive(final String args) {
        final String[] argsArray = args.split(WHITESPACE);

        if (argsArray.length != 3) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.missingArguments"));
            return;
        }

        final String nick = argsArray[1];
        final User user = controller.getUser(nick);

        if (user == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.noSuchUser", nick));
            return;
        }

        if (user == me) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.receive.systemMessage.userIsMe"));
            return;
        }

        final Integer id = parseFileTransferId(argsArray[2]);

        if (id == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.invalidFileId", argsArray[2]));
            return;
        }

        final FileReceiver fileReceiver = tList.getFileReceiver(user, id);

        if (fileReceiver == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.noSuchFileIdForUser", id, nick));
            return;
        }

        if (fileReceiver.isAccepted()) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.alreadyReceiving", fileReceiver.getFileName(), nick));
            return;
        }

        final File file = fileReceiver.getFile();

        if (file.exists()) {
            final File newFile = Tools.getFileWithIncrementedName(file);
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.receive.systemMessage.renamingFile", file.getName(), newFile.getName()));
            fileReceiver.setFile(newFile);
        }

        fileReceiver.accept();
    }

    /**
     * Command: <em>/reject &lt;nick&gt; &lt;id&gt;</em>.
     *
     * <p>Reject a file transfer request from a user and abort the transfer.</p>
     *
     * @param args First argument is the other user in the file transfer, and the second is the id of the file transfer.
     */
    private void cmdReject(final String args) {
        final String[] argsArray = args.split(WHITESPACE);

        if (argsArray.length != 3) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.reject.systemMessage.missingArguments"));
            return;
        }

        final String nick = argsArray[1];
        final User user = controller.getUser(nick);

        if (user == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.reject.systemMessage.noSuchUser", nick));
            return;
        }

        if (user == me) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.reject.systemMessage.userIsMe"));
            return;
        }

        final Integer id = parseFileTransferId(argsArray[2]);

        if (id == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.reject.systemMessage.invalidFileId", argsArray[2]));
            return;
        }

        final FileReceiver fileReceiver = tList.getFileReceiver(user, id);

        if (fileReceiver == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.reject.systemMessage.noSuchFileIdForUser", id, nick));
            return;
        }

        if (fileReceiver.isAccepted()) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.reject.systemMessage.alreadyReceiving", fileReceiver.getFileName(), nick));
            return;
        }

        fileReceiver.reject();
    }

    /**
     * Command: <em>/cancel &lt;nick&gt; &lt;id&gt;</em>.
     *
     * <p>Cancel an ongoing file transfer with a user.</p>
     *
     * @param args First argument is the other user in the file transfer, and the second is the id of the file transfer.
     */
    private void cmdCancel(final String args) {
        final String[] argsArray = args.split(WHITESPACE);

        if (argsArray.length != 3) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.cancel.systemMessage.missingArguments"));
            return;
        }

        final String nick = argsArray[1];
        final User user = controller.getUser(nick);

        if (user == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.cancel.systemMessage.noSuchUser", nick));
            return;
        }

        if (user == me) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.cancel.systemMessage.userIsMe"));
            return;
        }

        final Integer id = parseFileTransferId(argsArray[2]);

        if (id == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.cancel.systemMessage.invalidFileId", argsArray[2]));
            return;
        }

        final FileTransfer fileTransfer = tList.getFileTransfer(user, id);

        if (fileTransfer == null) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.cancel.systemMessage.noSuchFileIdForUser", id, nick));
            return;
        }

        if (fileTransfer instanceof FileReceiver) {
            final FileReceiver fileReceiver = (FileReceiver) fileTransfer;

            if (!fileReceiver.isAccepted()) {
                msgController.showSystemMessage(coreMessages.getMessage(
                        "core.command.cancel.systemMessage.notStartedYet", fileReceiver.getFileName(), nick));
                return;
            }
        }

        cancelFileTransfer(fileTransfer);
    }

    @Nullable
    private Integer parseFileTransferId(final String argument) {
        try {
            return Integer.parseInt(argument);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    /**
     * Command: <em>/msg &lt;nick&gt; &lt;msg&gt;</em>.
     *
     * <p>Send a private message to a user.</p>
     *
     * @param args The first argument is the user to send to, and the second is the private message to the user.
     */
    private void cmdMsg(final String args) {
        final String[] argsArray = args.split(WHITESPACE);

        if (argsArray.length <= 2) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.msg.systemMessage.missingArguments"));
        }

        else {
            final String nick = argsArray[1];
            final User user = controller.getUser(nick);

            if (user == null) {
                msgController.showSystemMessage(coreMessages.getMessage(
                        "core.command.msg.systemMessage.noSuchUser", nick));
            }

            else if (user == me) {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.msg.systemMessage.userIsMe"));
            }

            else if (settings.isNoPrivateChat()) {
                msgController.showSystemMessage(coreMessages.getMessage(
                        "core.command.msg.systemMessage.privateChatDisabled"));
            }

            else if (user.getPrivateChatPort() == 0) {
                msgController.showSystemMessage(coreMessages.getMessage(
                        "core.command.msg.systemMessage.noPrivateChatPortNumber", user.getNick()));
            }

            else {
                String privmsg = "";

                for (int i = 2; i < argsArray.length; i++) {
                    privmsg += argsArray[i] + " ";
                }

                privmsg = privmsg.trim();

                try {
                    controller.sendPrivateMessage(privmsg, user);
                    msgController.showPrivateOwnMessage(user, privmsg);
                }

                catch (final CommandException e) {
                    msgController.showSystemMessage(e.getMessage());
                }
            }
        }
    }

    /**
     * Command: <em>/nick &lt;new nick&gt;</em>.
     *
     * <p>Changes your nick name.</p>
     *
     * @param args The nick to change to.
     */
    private void cmdNick(final String args) {
        if (args.trim().length() == 0) {
            msgController.showSystemMessage(coreMessages.getMessage("core.command.nick.systemMessage.missingArgument"));
        }

        else {
            final String[] argsArray = args.split(WHITESPACE);
            final String nick = argsArray[1].trim();

            if (!nick.equals(me.getNick())) {
                if (controller.isNickInUse(nick)) {
                    msgController.showSystemMessage(coreMessages.getMessage(
                            "core.command.nick.systemMessage.nickInUse", nick));
                }

                else if (!Tools.isValidNick(nick)) {
                    msgController.showSystemMessage(coreMessages.getMessage(
                            "core.command.nick.systemMessage.nickInvalid", nick));
                }

                else {
                    try {
                        controller.changeMyNick(nick);
                        msgController.showSystemMessage(coreMessages.getMessage(
                                "core.command.nick.systemMessage.nickChanged", me.getNick()));
                        ui.showTopic();
                    }

                    catch (final CommandException e) {
                        msgController.showSystemMessage(e.getMessage());
                    }
                }
            }

            else {
                msgController.showSystemMessage(coreMessages.getMessage(
                        "core.command.nick.systemMessage.nickIdentical", nick));
            }
        }
    }

    /**
     * Command: <em>/users</em>.
     *
     * <p>Shows a list of connected users.</p>
     */
    private void cmdUsers() {
        final UserList list = controller.getUserList();
        String userList = "";

        for (int i = 0; i < list.size(); i++) {
            final User user = list.get(i);
            userList += user.getNick();

            if (i < list.size() - 1) {
                userList += ", ";
            }
        }

        msgController.showSystemMessage(coreMessages.getMessage("core.command.users.systemMessage.users", userList));
    }

    /**
     * Command: <em>/transfers</em>.
     *
     * <p>Shows a list of all transfers and their status.</p>
     */
    private void cmdTransfers() {
        final List<FileSender> fsList = tList.getFileSenders();
        final List<FileReceiver> frList = tList.getFileReceivers();

        final StringBuilder transferInfo = new StringBuilder();

        if (fsList.size() > 0) {
            transferInfo.append("\n");
            transferInfo.append(coreMessages.getMessage("core.command.transfers.sending"));

            for (final FileSender fs : fsList) {
                appendTransferInfo(fs, transferInfo);
            }
        }

        if (frList.size() > 0) {
            transferInfo.append("\n");
            transferInfo.append(coreMessages.getMessage("core.command.transfers.receiving"));

            for (final FileReceiver fr : frList) {
                appendTransferInfo(fr, transferInfo);
            }
        }

        if (transferInfo.length() == 0) {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.transfers.systemMessage.noFileTransfers"));
        } else {
            msgController.showSystemMessage(coreMessages.getMessage(
                    "core.command.transfers.systemMessage.activeFileTransfers") + transferInfo.toString());
        }
    }

    /**
     * Command: <em>/quit</em>.
     *
     * <p>Quits the application.</p>
     */
    private void cmdQuit() {
        ui.quit();
    }

    /**
     * Adds a new line with information about the file transfer.
     *
     * @param fileTransfer The file transfer to add info about.
     * @param transferInfo The string builder to add the info to.
     */
    private void appendTransferInfo(final FileTransfer fileTransfer, final StringBuilder transferInfo) {
        transferInfo.append("\n  ");

        final int fileTransferId = fileTransfer.getId();
        final String fileName = fileTransfer.getFileName();
        final String fileSize = Tools.byteToString(fileTransfer.getFileSize());
        final int percent = fileTransfer.getPercent();
        final String speed = Tools.byteToString(fileTransfer.getSpeed());
        final String user = fileTransfer.getUser().getNick();

        if (fileTransfer.getDirection() == FileTransfer.Direction.SEND) {
            transferInfo.append(coreMessages.getMessage("core.command.transfers.sendingFile",
                                                        fileTransferId, fileName, fileSize, percent, speed, user));
        } else {
            transferInfo.append(coreMessages.getMessage("core.command.transfers.receivingFile",
                                                        fileTransferId, fileName, fileSize, percent, speed, user));
        }
    }

    /**
     * Command: <em>//&lt;text&gt;</em>.
     *
     * <p>Sends the text as a message, instead of parsing it as a command.</p>
     *
     * @param line The text starting with a slash.
     */
    private void cmdSlash(final String line) {
        final String message = line.replaceFirst("/", "");

        try {
            controller.sendChatMessage(message);
            msgController.showOwnMessage(message);
        }

        catch (final CommandException e) {
            msgController.showSystemMessage(e.getMessage());
        }
    }

    /**
     * Command: <em>/'anything'</em>.
     *
     * <p>The command was not recognized by the parser.</p>
     *
     * @param command The unknown command.
     */
    private void cmdUnknown(final String command) {
        msgController.showSystemMessage(coreMessages.getMessage("core.command.unknown.systemMessage.unknown", command));
    }

    /**
     * Updates the topic. If the new topic is empty, the topic will be removed.
     *
     * @param newTopic The new topic to use.
     * @throws CommandException If there was a problem changing the topic.
     */
    public void fixTopic(final String newTopic) throws CommandException {
        final Topic topic = controller.getTopic();
        final String trimTopic = newTopic.trim();

        if (!trimTopic.equals(topic.getTopic().trim())) {
            controller.changeTopic(trimTopic);

            if (trimTopic.length() > 0) {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.topic.systemMessage.topicChanged",
                                                                        trimTopic));
            } else {
                msgController.showSystemMessage(coreMessages.getMessage("core.command.topic.systemMessage.topicRemoved"));
            }

            ui.showTopic();
        }
    }

    /**
     * Sends a file to a user.
     *
     * @param user The user to send to.
     * @param file The file to send to the user.
     * @throws CommandException If there was a problem sending the file.
     */
    public void sendFile(final User user, final FileToSend file) throws CommandException {
        controller.sendFile(user, file);
        final FileSender fileSend = tList.addFileSender(user, file);
        ui.showTransfer(fileSend);

        final String size = Tools.byteToString(file.length());
        msgController.showSystemMessage(coreMessages.getMessage(
                "core.command.send.systemMessage.sendingFile",
                file.getName(), fileSend.getId(), size, user.getNick()));
    }

    /**
     * Cancels a file transfer, even if the file transfer has not been
     * answered by the other user yet.
     *
     * @param fileTransfer The file transfer to cancel.
     */
    public void cancelFileTransfer(final FileTransfer fileTransfer) {
        fileTransfer.cancel();

        if (fileTransfer instanceof FileSender) {
            final FileSender fs = (FileSender) fileTransfer;

            // This means that the other user has not answered yet
            if (fs.isWaiting()) {
                final FileToSend file = fs.getFile();
                final User user = fs.getUser();

                msgController.showSystemMessage(coreMessages.getMessage("core.command.cancel.systemMessage.cancelled",
                                                                        file.getName(), user.getNick()));
                tList.removeFileSender(fs);
                controller.sendFileAbort(user, file.hashCode(), file.getName());
            }
        }
    }

    /**
     * Shows a list of all the supported commands, with a short description.
     */
    public void showCommands() {
        msgController.showSystemMessage(
                coreMessages.getMessage("core.command.help.systemMessage.commands", Constants.APP_NAME) + "\n" +
                        coreMessages.getMessage("core.command.about.systemMessage.help", Constants.APP_NAME) + "\n" +
                        coreMessages.getMessage("core.command.away.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.back.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.cancel.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.clear.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.help.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.msg.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.nick.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.quit.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.receive.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.reject.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.send.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.topic.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.transfers.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.users.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.whois.systemMessage.help") + "\n" +
                        coreMessages.getMessage("core.command.slash.systemMessage.help"));
    }

    /**
     * Parses the line to split the command from the arguments.
     *
     * <p>The command is then checked against valid options and redirected to the appropriate method.</p>
     *
     * @param line The command in its raw form.
     */
    public void parse(final String line) {
        @NonNls final String command;

        if (line.contains(" ")) {
            command = line.substring(1, line.indexOf(' '));
        } else {
            command = line.substring(1, line.length());
        }

        if (command.length() > 0) {
            final String args = line.replaceFirst("/" + Pattern.quote(command), "");

            if (command.equals("topic")) {
                cmdTopic(args);
            } else if (command.equals("away")) {
                cmdAway(args);
            } else if (command.equals("back")) {
                cmdBack();
            } else if (command.equals("clear")) {
                cmdClear();
            } else if (command.equals("about")) {
                cmdAbout();
            } else if (command.equals("help")) {
                cmdHelp();
            } else if (command.equals("whois")) {
                cmdWhois(args);
            } else if (command.equals("send")) {
                cmdSend(args);
            } else if (command.equals("receive")) {
                cmdReceive(args);
            } else if (command.equals("reject")) {
                cmdReject(args);
            } else if (command.equals("cancel")) {
                cmdCancel(args);
            } else if (command.equals("msg")) {
                cmdMsg(args);
            } else if (command.equals("nick")) {
                cmdNick(args);
            } else if (command.equals("users")) {
                cmdUsers();
            } else if (command.equals("transfers")) {
                cmdTransfers();
            } else if (command.equals("quit")) {
                cmdQuit();
            } else if (command.startsWith("/")) {
                cmdSlash(line);
            } else {
                cmdUnknown(command);
            }
        }

        else {
            cmdUnknown(command);
        }
    }
}
