package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "DeckArchive")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeckArchive implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "deckArchiveId")
	private Long deckArchiveId;
	@Column
	private String deckName;

	public Long getDeckArchiveId()
	{
		return this.deckArchiveId;
	}

	public void setDeckArchiveId(final Long _deckArchiveId)
	{
		this.deckArchiveId = _deckArchiveId;
	}

	public String getDeckName()
	{
		return this.deckName;
	}

	public void setDeckName(final String _deckName)
	{
		this.deckName = _deckName;
	}
}