package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CollectibleCard implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String title;
	@Column
	private Long deckArchiveId;

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(final String _title)
	{
		this.title = _title;
	}

	public Long getDeckArchiveId()
	{
		return this.deckArchiveId;
	}

	public void setDeckArchiveId(final Long _deckArchiveId)
	{
		this.deckArchiveId = _deckArchiveId;
	}

}
