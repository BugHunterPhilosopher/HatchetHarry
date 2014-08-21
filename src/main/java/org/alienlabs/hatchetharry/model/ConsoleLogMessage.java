package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ConsoleLogMessage", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"gameId", "message" }) })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ConsoleLogMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "gameId")
	private Long gameId;
	@Column(name = "message")
	private String message;

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public String getMessage()
	{
		return this.message;
	}

	public void setMessage(final String _message)
	{
		this.message = _message;
	}

}
