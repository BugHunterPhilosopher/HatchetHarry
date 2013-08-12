package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "Player")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Player implements Serializable
{
	private static final long serialVersionUID = 7963755937946852379L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Index(name = "Player_index")
	private Long playerId;
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.SAVE_UPDATE })
	private Game game = new Game();
	@Column
	private String side;
	@Column
	private String name;
	@Column
	private String jsessionid;
	@Column
	private Long lifePoints;
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL })
	@JoinColumn(name = "deck")
	private Deck deck;
	@Column
	private boolean isHandDisplayed = true;
	@Column
	private boolean isGraveyardDisplayed;

	public Long getId()
	{
		return this.playerId;
	}

	public void setId(final Long _id)
	{
		this.playerId = _id;
	}

	public String getSide()
	{
		return this.side;
	}

	public void setSide(final String _side)
	{
		this.side = _side;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String _name)
	{
		this.name = _name;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

	public void setJsessionid(final String _jsessionid)
	{
		this.jsessionid = _jsessionid;
	}

	public Long getLifePoints()
	{
		return this.lifePoints;
	}

	public void setLifePoints(final Long _lifePoints)
	{
		this.lifePoints = _lifePoints;
	}

	public Game getGame()
	{
		return this.game;
	}

	public void setGame(final Game _game)
	{
		this.game = _game;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.jsessionid == null) ? 0 : this.jsessionid.hashCode());
		result = (prime * result) + ((this.playerId == null) ? 0 : this.playerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		final Player other = (Player)obj;
		if (this.jsessionid == null)
		{
			if (other.jsessionid != null)
			{
				return false;
			}
		}
		else if (!this.jsessionid.equals(other.jsessionid))
		{
			return false;
		}
		if (this.playerId == null)
		{
			if (other.playerId != null)
			{
				return false;
			}
		}
		else if (!this.playerId.equals(other.playerId))
		{
			return false;
		}
		return true;
	}

	public Deck getDeck()
	{
		return this.deck;
	}

	public void setDeck(final Deck _deck)
	{
		this.deck = _deck;
	}

	public boolean isHandDisplayed()
	{
		return this.isHandDisplayed;
	}

	public void setHandDisplayed(final boolean _isHandDisplayed)
	{
		this.isHandDisplayed = _isHandDisplayed;
	}

	public boolean isGraveyardDisplayed()
	{
		return this.isGraveyardDisplayed;
	}

	public void setGraveyardDisplayed(final boolean _isGraveyardDisplayed)
	{
		this.isGraveyardDisplayed = _isGraveyardDisplayed;
	}

}
