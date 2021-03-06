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

package org.tiwindetea.animewarfare.net.networkrequests.server;

import org.tiwindetea.animewarfare.logic.battle.event.BattleEvent;
import org.tiwindetea.animewarfare.net.GameClientInfo;
import org.tiwindetea.animewarfare.net.networkevent.BattleNetevent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucas Lazare
 * @author Benoît CORTIER
 * @since 0.1.0
 */
public class NetBattle implements NetReceivable {
    private final GameClientInfo attacker;
    private final GameClientInfo defender;
    private final int zone;
    private final BattleNetevent.Type type;

    private final Map<GameClientInfo, Integer> attack = new HashMap<>();
    private final Map<GameClientInfo, Integer> numberOfWoundeds = new HashMap<>();
    private final Map<GameClientInfo, Integer> numberOfDeads = new HashMap<>();

    /**
     * Default constructor, required by Kryo.net
     */
    public NetBattle() {
        this.attacker = this.defender = null;
        this.zone = 0;
        this.type = null;
    }

    public NetBattle(BattleEvent event, BattleNetevent.Type type, GameClientInfo attacker, GameClientInfo defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.zone = event.getBattleContext().getZone().getID();
        this.type = type;

        this.attack.put(this.attacker, event.getBattleContext().getAttacker().getAttack());
        this.attack.put(this.defender, event.getBattleContext().getDefender().getAttack());
        this.numberOfWoundeds.put(this.attacker, event.getBattleContext().getAttacker().getNumberOfWoundeds());
        this.numberOfWoundeds.put(this.defender, event.getBattleContext().getDefender().getNumberOfWoundeds());
        this.numberOfDeads.put(this.attacker, event.getBattleContext().getAttacker().getNumberOfDeads());
        this.numberOfDeads.put(this.defender, event.getBattleContext().getDefender().getNumberOfDeads());
    }

    public GameClientInfo getAttacker() {
        return this.attacker;
    }

    public GameClientInfo getDefender() {
        return this.defender;
    }

    public int getZone() {
        return this.zone;
    }

    public BattleNetevent.Type getType() {
        return this.type;
    }

    public Map<GameClientInfo, Integer> getAttack() {
        return this.attack;
    }

    public Map<GameClientInfo, Integer> getNumberOfWoundeds() {
        return this.numberOfWoundeds;
    }

    public Map<GameClientInfo, Integer> getNumberOfDeads() {
        return this.numberOfDeads;
    }
}
