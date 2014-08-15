/*
 * $Id: PlayerDaoImpl.java 1056 2006-10-27 22:49:28Z ivaynberg $
 * $Revision: 1056 $
 * $Date: 2006-10-27 15:49:28 -0700 (Fri, 27 Oct 2006) $
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.alienlabs.hatchetharry.persistence.dao;

import java.util.List;

import org.alienlabs.hatchetharry.model.Player;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements {@link PlayerDao}.
 *
 * @author igor
 */
public class PlayerDaoImpl implements PlayerDao {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private SessionFactory factory;

	public PlayerDaoImpl() {
	}

	/**
	 * Setter for session factory. Spring will use this to inject the session
	 * factory into the dao.
	 *
	 * @param _factory hibernate session factory
	 */
	@Required
	public void setSessionFactory(final SessionFactory _factory) {
		this.factory = _factory;
	}

	/**
	 * Helper method for retrieving hibernate session
	 *
	 * @return hibernate session
	 */
	@Override
	public Session getSession() {
		return this.factory.getCurrentSession();
	}

	/**
	 * Load a {@link Player} from the DB, given it's <tt>id</tt> .
	 *
	 * @param id The id of the Player to load.
	 * @return Player
	 */
	@Override
	@Transactional(readOnly = true)
	public Player load(final long id) {
		return (Player) this.getSession().get(Player.class, Long.valueOf(id));
	}

	/**
	 * Save the Player to the DB
	 *
	 * @param Player
	 * @return persistent instance of Player
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void save(final Player Player) {
		this.getSession().save(Player);
	}

	/**
	 * Delete a {@link Player} from the DB, given it's <tt>id</tt>.
	 *
	 * @param id The id of the Player to delete.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(final long id) {
		this.getSession().delete(this.load(id));
	}

	/**
	 * Returns a list of unique last names
	 */
	@Override
	@Transactional(readOnly = true)
	public List<String> getUniqueLastNames() {
		return this
					   .getSession()
					   .createQuery(
										   "select distinct target.lastname "
												   + " from Player target order by target.lastname").list();
	}

}
