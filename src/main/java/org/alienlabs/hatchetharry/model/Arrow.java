package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Table(name = "Arrow", indexes = { @Index(columnList = "gameId"), @Index(columnList = "source"),
		@Index(columnList = "target") })
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Arrow implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "VERSION", length = 20)
	private String version;
	@Column
	private Long gameId;
	@Column
	private String source;
	@Column
	private String target;

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

	public void setGame(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public String getSource()
	{
		return this.source;
	}

	public void setSource(final String _source)
	{
		this.source = _source;
	}

	public String getTarget()
	{
		return this.target;
	}

	public void setTarget(final String _target)
	{
		this.target = _target;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(final String _version)
	{
		this.version = _version;
	}

}
