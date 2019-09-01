
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

package net.usikkert.kouchat.android.notification

/**
 * Notification groups are used on Android N and newer to avoid automatic
 * grouping of all notifications together with the low priority service notification.
 *
 * <p>For some reason, only notifications without a group are automatically grouped by Android.
 * Notifications with a group are not grouped, unless a summary notification is used,
 * which I'd like to avoid because of complexity.</p>
 *
 * <p>Another reason to only use groups on Android N and newer is that older versions
 * won't show grouped notifications at all without a summary notification.</p>
 *
 * <p>Since private chat notifications are the only notifications that are useful to
 * group automatically, they are the only ones to not have a group. Also, automatic grouping
 * doesn't happen until 4 or more ungrouped notifications are shown.</p>
 *
 * @author Christian Ihle
 */
enum class NotificationGroup {

    SERVICE,
    MAIN_CHAT,
//    PRIVATE_CHAT,
    FILE_TRANSFER;
}
