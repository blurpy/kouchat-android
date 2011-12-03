
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

package net.usikkert.kouchat.util;

/**
 * Used for calculating the number of bytes transferred per second.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>Run {@link #prepare()} just before starting the transfer to record the time
 *       when the transfer begins and to reset the counters.</li>
 *   <li>Then run {@link #addBytes(long)} with regular intervals to register the number
 *       of bytes transferred.</li>
 *   <li>After every new second has passed the calculated speed should be
 *       available through {@link #getBytesPerSec()}.</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class ByteCounter
{
	/** Number of milliseconds in one second. */
	private static final int ONE_SECOND = 1000;

	/** Time of the previous update. */
	private long previousTime;

	/** The number of milliseconds counted since the last second was calculated. */
	private long timeCounted;

	/** The current number of bytes per second. */
	private long bytesPerSec;

	/** The number of bytes counted since the last second was calculated. */
	private long bytesCounted;

	/**
	 * Use when starting the transfer to register the current time, and reset the counters.
	 */
	public void prepare()
	{
		previousTime = System.currentTimeMillis();
		timeCounted = 0;
		bytesPerSec = 0;
		bytesCounted = 0;
	}

	/**
	 * Use this to add the number of bytes transferred since last time
	 * {@link #addBytes(long)} or {@link #prepare()} was called.
	 * The speed is calculated every second, so {@link #addBytes(long)} should
	 * be called often for the most accurate result.
	 *
	 * @param bytes Number of bytes transferred since last time.
	 */
	public void addBytes( final long bytes )
	{
		long currentTime = System.currentTimeMillis();
		long timeSpent = updateTimeSpent( currentTime );
		updateCounters( bytes, timeSpent );
	}

	/**
	 * Gets the time spent since the last update, and changes the time
	 * of the last update to current time.
	 *
	 * @param currentTime The time when update was run.
	 * @return Time spent since the last update.
	 */
	protected long updateTimeSpent( final long currentTime )
	{
		long timeSpent = currentTime - previousTime;
		previousTime = currentTime;
		return timeSpent;
	}

	/**
	 * Updates the time and byte counters with the bytes added and the time spent.
	 * If time counted is more than a second, then the bytes per second is calculated.
	 *
	 * @param bytesAdded Number of bytes added since last update.
	 * @param timeSpent The time spent since last update.
	 */
	protected void updateCounters( final long bytesAdded, final long timeSpent )
	{
		timeCounted += timeSpent;
		bytesCounted += bytesAdded;

		if ( timeCounted >= ONE_SECOND )
		{
			if ( timeSpent > ONE_SECOND )
				calculateOnlyTimeSpent( bytesAdded, timeSpent );
			else
				calculateFirstSecond( bytesAdded, timeSpent );
		}
	}

	/**
	 * Time spent since the last update is more than a second.
	 *
	 * <p>The speed is calculated as the average number of bytes added in one
	 * second of the spent time.</p>
	 *
	 * <p>Example: 1024 bytes transferred in 8 seconds is 128 bytes per second on average.</p>
	 *
	 * @param bytesAdded Number of bytes added since last update.
	 * @param timeSpent The time spent since last update.
	 */
	private void calculateOnlyTimeSpent( final long bytesAdded, final long timeSpent )
	{
		bytesPerSec = getBytesAddedInTimeLeft( bytesAdded, timeSpent, ONE_SECOND );
		timeCounted = 0;
		bytesCounted = 0;
	}

	/**
	 * Time spent since the last update is less than a second,
	 * but the total time since last calculation is a second or more.
	 *
	 * <p>The speed is calculated by taking the previously counted bytes
	 * and adding the average number of bytes transferred in the remaining
	 * part of a second. The time and bytes left over are saved for later updates.</p>
	 *
	 * <p>Example: 1024 bytes has been counted earlier in 900ms, and 1024 bytes
	 * was now added inn 200ms. The average bytes added in the 100ms that is left
	 * of a second is 512. Giving 1536 bytes per sec, with 100ms and 512 bytes left.</p>
	 *
	 * @param bytesAdded Number of bytes added since last update.
	 * @param timeSpent The time spent since last update.
	 */
	private void calculateFirstSecond( final long bytesAdded, final long timeSpent )
	{
		long originalTimeCount = timeCounted - timeSpent;
		long originalByteCount = bytesCounted - bytesAdded;
		long timeLeftInSecond = ONE_SECOND - originalTimeCount;
		long bytesAddedInTimeLeft = getBytesAddedInTimeLeft( bytesAdded, timeSpent, timeLeftInSecond );

		bytesPerSec = originalByteCount + bytesAddedInTimeLeft;
		timeCounted %= ONE_SECOND;
		bytesCounted -= bytesPerSec;
	}

	/**
	 * Gets the average number of bytes added in the time that is left.
	 *
	 * <p>Example: 1024 bytes added in 200ms with 100ms left is 512 bytes.</p>
	 *
	 * @param bytesAdded Number of bytes added in the time spent.
	 * @param timeSpent The time spent adding the number of bytes.
	 * @param timeLeft The number of milliseconds left in a second.
	 * @return The average number of bytes added in the remaining milliseconds of a second.
	 */
	private long getBytesAddedInTimeLeft( final long bytesAdded, final long timeSpent, final long timeLeft )
	{
		double percent = Tools.percent( timeLeft, timeSpent );
		return (long) Tools.percentOf( percent, bytesAdded );
	}

	/**
	 * Gets the current number of bytes per seconds.
	 *
	 * @return The current number of bytes per second.
	 */
	public long getBytesPerSec()
	{
		return bytesPerSec;
	}

	/**
	 * Get the number of bytes counted since the last second was calculated.
	 *
	 * @return Bytes counted since last second.
	 */
	public long getBytesCounted()
	{
		return bytesCounted;
	}

	/**
	 * Get the number of milliseconds counted since the last second was calculated.
	 *
	 * @return Milliseconds counted since last second.
	 */
	public long getTimeCounted()
	{
		return timeCounted;
	}
}
