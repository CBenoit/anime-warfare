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

package org.tiwindetea.animewarfare.logic.buffs;

import org.tiwindetea.animewarfare.logic.LogicEventDispatcher;
import org.tiwindetea.animewarfare.logic.Zone;
import org.tiwindetea.animewarfare.logic.units.Unit;
import org.tiwindetea.animewarfare.logic.units.events.UnitMovedEvent;
import org.tiwindetea.animewarfare.logic.units.events.UnitMovedEventListener;

/**
 * Cease-fire buff.
 * In french: "cessez-le-feu".
 *
 * No combat in the zone during one action turn.
 *
 * @author Benoît CORTIER
 */
public class CeaseFireBuff extends Buff implements UnitMovedEventListener {
	private static final BuffMask BUFF_MASK = new BuffMask();
	static {
		BUFF_MASK.canAttack = false;
	}

	private final Zone zone;

	public CeaseFireBuff(Zone zone) {
		super(1);
		this.zone = zone;

		// apply buff mask on all units in the zone.
		for (Unit unit : this.zone.getUnits()) {
			unit.getUnitBuffedCharacteristics().addBuffMask(CeaseFireBuff.BUFF_MASK);
		}

		LogicEventDispatcher.getInstance().addListener(UnitMovedEvent.class, this);
	}

	@Override
	boolean isActionBuff() {
		return true;
	}

	@Override
	void destroy() {
		// remove buff mask on all units in the zone.
		for (Unit unit : this.zone.getUnits()) {
			unit.getUnitBuffedCharacteristics().removeBuffMask(CeaseFireBuff.BUFF_MASK);
		}

		LogicEventDispatcher.getInstance().removeListener(UnitMovedEvent.class, this);
	}

	@Override
	public void handleUnitMoved(UnitMovedEvent event) {
		if (event.getSource().equals(this.zone)) {
			event.getUnit().getUnitBuffedCharacteristics().removeBuffMask(CeaseFireBuff.BUFF_MASK);
		} else if (event.getDestination().equals(this.zone)) {
			event.getUnit().getUnitBuffedCharacteristics().addBuffMask(CeaseFireBuff.BUFF_MASK);
		}
	}
}
