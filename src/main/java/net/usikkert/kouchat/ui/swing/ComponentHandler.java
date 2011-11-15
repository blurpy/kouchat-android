
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import net.usikkert.kouchat.util.Validate;

/**
 * This class is just a compact parameter list, for transfer
 * between {@link KouChatFrame} and {@link SwingMediator}.
 *
 * @author Christian Ihle
 */
public class ComponentHandler
{
	/** The side panel. */
	private SidePanel sidePanel;

	/** The settings dialog. */
	private SettingsDialog settingsDialog;

	/** The system tray. */
	private SysTray sysTray;

	/** The menu bar. */
	private MenuBar menuBar;

	/** The button panel. */
	private ButtonPanel buttonPanel;

	/** The main application frame. */
	private KouChatFrame gui;

	/** The main panel. */
	private MainPanel mainPanel;

	/**
	 * Gets the side panel.
	 *
	 * @return The side panel.
	 */
	public SidePanel getSidePanel()
	{
		return sidePanel;
	}

	/**
	 * Sets the side panel.
	 *
	 * @param sidePanel The side panel.
	 */
	public void setSidePanel( final SidePanel sidePanel )
	{
		Validate.notNull( sidePanel, "Side panel can not be null" );
		this.sidePanel = sidePanel;
	}

	/**
	 * Gets the settings dialog.
	 *
	 * @return The settings dialog.
	 */
	public SettingsDialog getSettingsDialog()
	{
		return settingsDialog;
	}

	/**
	 * Sets the settings dialog.
	 *
	 * @param settingsDialog The settings dialog.
	 */
	public void setSettingsDialog( final SettingsDialog settingsDialog )
	{
		Validate.notNull( settingsDialog, "Settings dialog can not be null" );
		this.settingsDialog = settingsDialog;
	}

	/**
	 * Gets the system tray.
	 *
	 * @return The system tray.
	 */
	public SysTray getSysTray()
	{
		return sysTray;
	}

	/**
	 * Sets the system tray.
	 *
	 * @param sysTray The system tray.
	 */
	public void setSysTray( final SysTray sysTray )
	{
		Validate.notNull( sysTray, "System tray can not be null" );
		this.sysTray = sysTray;
	}

	/**
	 * Gets the menu bar.
	 *
	 * @return The menu bar.
	 */
	public MenuBar getMenuBar()
	{
		return menuBar;
	}

	/**
	 * Sets the menu bar.
	 *
	 * @param menuBar The menu bar.
	 */
	public void setMenuBar( final MenuBar menuBar )
	{
		Validate.notNull( menuBar, "Menu bar can not be null" );
		this.menuBar = menuBar;
	}

	/**
	 * Gets the button panel.
	 *
	 * @return The button panel.
	 */
	public ButtonPanel getButtonPanel()
	{
		return buttonPanel;
	}

	/**
	 * Sets the button panel.
	 *
	 * @param buttonPanel The button panel.
	 */
	public void setButtonPanel( final ButtonPanel buttonPanel )
	{
		Validate.notNull( buttonPanel, "Button panel can not be null" );
		this.buttonPanel = buttonPanel;
	}

	/**
	 * Gets the kouchat frame.
	 *
	 * @return The kouchat frame.
	 */
	public KouChatFrame getGui()
	{
		return gui;
	}

	/**
	 * Sets the kouchat frame.
	 *
	 * @param gui The kouchat frame.
	 */
	public void setGui( final KouChatFrame gui )
	{
		Validate.notNull( gui, "Kouchat frame can not be null" );
		this.gui = gui;
	}

	/**
	 * Gets the main panel.
	 *
	 * @return The main panel.
	 */
	public MainPanel getMainPanel()
	{
		return mainPanel;
	}

	/**
	 * Sets the main panel.
	 *
	 * @param mainPanel The main panel.
	 */
	public void setMainPanel( final MainPanel mainPanel )
	{
		Validate.notNull( mainPanel, "Main panel can not be null" );
		this.mainPanel = mainPanel;
	}

	/**
	 * Checks that all the components are set to a non-null value.
	 * Throws IllegalArgumentException If any of the components are null.
	 */
	public void validate()
	{
		Validate.notNull( sidePanel, "Side panel can not be null" );
		Validate.notNull( settingsDialog, "Settings dialog can not be null" );
		Validate.notNull( sysTray, "System tray can not be null" );
		Validate.notNull( menuBar, "Menu bar can not be null" );
		Validate.notNull( buttonPanel, "Button panel can not be null" );
		Validate.notNull( gui, "GUI can not be null" );
		Validate.notNull( mainPanel, "Main panel can not be null" );
	}
}
