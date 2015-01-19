package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class OkForMulliganButOneLessConsoleLogStrategy extends ConsoleLogStrategy
{
	private static final long serialVersionUID = 1L;

	private final String player;
	private final Long gameId;
	private final String targetPlayer;

	public OkForMulliganButOneLessConsoleLogStrategy(final Long _gameId, final String _player,
			final String _targetPlayer)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
		this.targetPlayer = _targetPlayer;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		super.logMessage(target, this.player + " agrees for mulligan with one card less. "
				+ this.targetPlayer + " can draw " + this.gameId + " cards.", null, this.gameId);
	}

}
