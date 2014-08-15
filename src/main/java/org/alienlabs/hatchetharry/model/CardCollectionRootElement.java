package org.alienlabs.hatchetharry.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "cards")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardCollectionRootElement {
	@XmlElement(name = "CardCollection")
	private List<CardCollection> cardCollectionList;

	public List<CardCollection> getCardCollectionList() {
		return this.cardCollectionList;
	}

	public void setCardCollectionList(final List<CardCollection> _cardCollectionList) {
		this.cardCollectionList = _cardCollectionList;
	}

}
