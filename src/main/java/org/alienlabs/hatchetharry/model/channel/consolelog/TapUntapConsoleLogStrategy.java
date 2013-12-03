package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class TapUntapConsoleLogStrategy extends ConsoleLogStrategy
{
	private final Boolean cond;
	private final String mc;
	private final String player;
	private final Boolean clearConsole;

	public TapUntapConsoleLogStrategy(final Boolean _cond, final String _mc, final String _player,
			final Boolean _clearConsole)
	{
		this.cond = _cond;
		this.mc = _mc;
		this.player = _player;
		this.clearConsole = _clearConsole;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		String message = "";

		if ((null == this.mc) && (null != this.clearConsole) && (false == this.clearConsole))
		{
			message = this.player + " has untapped all his (her) permanents";
			super.logMessage(target, message, false);
		}
		else if ((null != this.mc) && (null == this.clearConsole) && (null != this.cond))
		{
			message = this.player + " has " + (this.cond ? "tapped " : "untapped ") + " permanent "
					+ this.mc;
			super.logMessage(target, message, null);
		}
	}

}