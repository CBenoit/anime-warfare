////////////////////////////////////////////////////////////
//
// Anime Warfare
// Copyright (C) 2016 TiWinDeTea - contact@tiwindetea.org
//
// This software is provided 'as-is', without any express or implied warranty.
// In no event will the authors be held liable for any damages arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it freely,
// subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented;
//    you must not claim that you wrote the original software.
//    If you use this software in a product, an acknowledgment
//    in the product documentation would be appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such,
//    and must not be misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//
////////////////////////////////////////////////////////////

package org.tiwindetea.animewarfare.logic;

import org.tiwindetea.animewarfare.logic.buffs.BuffManager;
import org.tiwindetea.animewarfare.logic.capacity.Capacity;
import org.tiwindetea.animewarfare.logic.capacity.CapacityType;
import org.tiwindetea.animewarfare.logic.event.AdvertisingCampaignRightAddedEvent;
import org.tiwindetea.animewarfare.logic.event.AdvertisingCampaignRightRevealedEvent;
import org.tiwindetea.animewarfare.logic.event.NumberOfFansChangedEvent;
import org.tiwindetea.animewarfare.logic.event.StaffPointUpdatedEvent;
import org.tiwindetea.animewarfare.logic.units.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
 * @author Jérôme BOULMIER
 * @author Benoît CORTIER
 */
public class Player {
	private final int ID;

	private int fanNumber;
	private int staffAvailable;
	private int battleCostModifier;
	private int uniqueActionModifier;

	private final FactionType faction;
	private final BuffManager buffManager = new BuffManager();
	private final UnitCounter unitCounter = new UnitCounter();
	private final UnitCostModifier unitCostModifier = new UnitCostModifier();
	private final Map<CapacityType, Capacity> capacities = new HashMap<>();
	private final Set<Unit> unitCaptured = new HashSet<>();
	private final List<AdvertisingCampaignRight> advertisingCampaignRights = new ArrayList<>();

	public Player(int id, FactionType faction) {
		this.ID = id;
		this.faction = faction;
	}

	public int getID() {
		return this.ID;
	}

	public void setStaffAvailable(int staffAvailable) {
		this.staffAvailable = staffAvailable;

		LogicEventDispatcher.send(new StaffPointUpdatedEvent(this.ID, this.staffAvailable));
	}

	public boolean hasRequiredStaffPoints(int actionCost, int numberOfActions) {
		return actionCost * numberOfActions >= this.staffAvailable;
	}

	public boolean hasRequiredStaffPoints(int actionCost) {
		return hasRequiredStaffPoints(actionCost, 1);
	}

	public void decrementStaffPoints(int actionCost, int numberOfActions) {
		assert (hasRequiredStaffPoints(actionCost, numberOfActions));

		setStaffAvailable(this.staffAvailable - actionCost * numberOfActions);
	}

	public void decrementStaffPoints(int actionCost) {
		decrementStaffPoints(actionCost, 1);
	}

	public int getStaffAvailable() {
		return this.staffAvailable;
	}

	public int getFanNumber() {
		return this.fanNumber;
	}

	public void incrementFans(int numberOfFans) {
		if (numberOfFans <= 0) {
			throw new IllegalArgumentException();
		}

		this.fanNumber += numberOfFans;
		LogicEventDispatcher.getInstance().fire(new NumberOfFansChangedEvent(this, numberOfFans, this.fanNumber));
	}

	public void decrementFans(int numberOfFans) {
		if (numberOfFans >= 0) {
			throw new IllegalArgumentException();
		}

		this.fanNumber -= numberOfFans;
		LogicEventDispatcher.getInstance().fire(new NumberOfFansChangedEvent(this, -numberOfFans, this.fanNumber));
	}

	public FactionType getFaction() {
		return this.faction;
	}

	public boolean hasFaction(FactionType factionType) {
		return this.faction == factionType;
	}

	public UnitCounter getUnitCounter() {
		return this.unitCounter;
	}

	public UnitCostModifier getUnitCostModifier() {
		return this.unitCostModifier;
	}

	public boolean addUnitCaptured(Unit unit) {
		return this.unitCaptured.add(unit);

		// TODO: Event
	}

	public Set<Unit> getUnitCaptured() {
		return this.unitCaptured;
	}

	public List<AdvertisingCampaignRight> getAdvertisingCampaignRights() {
		return Collections.unmodifiableList(this.advertisingCampaignRights);
	}

	public void addAdvertisingCampaignRights(AdvertisingCampaignRight advertisingCampaignRight) {
		this.advertisingCampaignRights.add(advertisingCampaignRight);

		LogicEventDispatcher.send(new AdvertisingCampaignRightAddedEvent(this.ID, advertisingCampaignRight.getWeight()));
	}

	public boolean revealAdvertisingCampainRights(int weight) { // FIXME: Don't forget this
		AdvertisingCampaignRight campaignRight = this.advertisingCampaignRights.stream()
		                                                                       .filter(a -> a.getWeight() == weight)
		                                                                       .findFirst()
		                                                                       .orElse(null);

		if (campaignRight != null) {
			this.advertisingCampaignRights.remove(campaignRight);
			LogicEventDispatcher.send(new AdvertisingCampaignRightRevealedEvent(this.ID, campaignRight.getWeight()));
			return true;
		}

		return false;
	}

	public void clearAdvertisingCampaignRights() { // TODO: Is this realy needed ?
		this.advertisingCampaignRights.clear();
	}

	public int getBattleCostModifier() {
		return this.battleCostModifier;
	}

	public void modifyBattleCost(int battleCostModifier) {
		this.battleCostModifier += battleCostModifier;

		// TODO: Event
	}

	public int getUniqueActionModifier() { // FIXME: Don't forget to use this.
		return this.uniqueActionModifier;
	}

	public void modifyUniqueActionCost(int uniqueActionModifier) {
		this.uniqueActionModifier += uniqueActionModifier;

		// TODO: Event
	}

	public void activateCapacity(Capacity capacity) {
		this.capacities.put(capacity.getType(), capacity);
	}

	public boolean hasCapacity(CapacityType type) {
		return this.capacities.containsKey(type);
	}

	public void desactivateCapactiy(Capacity capacity) {
		this.capacities.remove(capacity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Player player = (Player) o;
		return this.ID == player.ID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(this.ID));
	}
}
