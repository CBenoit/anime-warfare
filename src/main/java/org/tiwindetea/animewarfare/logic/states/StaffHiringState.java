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

package org.tiwindetea.animewarfare.logic.states;

import org.lomadriel.lfc.event.EventDispatcher;
import org.lomadriel.lfc.statemachine.State;
import org.tiwindetea.animewarfare.logic.GameBoard;
import org.tiwindetea.animewarfare.logic.Player;
import org.tiwindetea.animewarfare.logic.states.events.PhaseChangedEvent;
import org.tiwindetea.animewarfare.logic.units.Studio;
import org.tiwindetea.animewarfare.logic.units.Unit;
import org.tiwindetea.animewarfare.logic.units.UnitLevel;

import java.util.List;
import java.util.Objects;

public class StaffHiringState extends GameState {
	public StaffHiringState(GameBoard gameBoard) {
		super(gameBoard);
	}

	@Override
	public void onEnter() {
		computeStaffAvailable();

		EventDispatcher.getInstance().fire(new PhaseChangedEvent(PhaseChangedEvent.Phase.STAFF_HIRING));
	}

	@Override
	public void update() {
		// TODO
	}

	@Override
	public void onExit() {
		this.gameBoard.getPlayers().forEach(this::releaseCapturedUnits);
	}

	@Override
	public State next() {
		return new FirstPlayerSelectionState(this.gameBoard);
	}

	private void computeStaffAvailable() {
		// TODO

		List<Studio> studios = this.gameBoard.getMap().getStudios();

		int numberOfNonControlledPortal = getNumberOfNonControlledPortal(studios);
		int maxStaffPoints = 0;


		for (Player player : this.gameBoard.getPlayers()) {
			int numberOfCapturedMascot = (int) player.getUnitCaptured()
			                                         .stream()
			                                         .filter(unit -> unit.isLevel(UnitLevel.MASCOT))
			                                         .count();

			int staffPoints = 2 * getNumberOfControlledPortal(studios, player)
					+ player.getUnitCounter().getNumberOfUnits(UnitLevel.MASCOT)
					+ numberOfCapturedMascot
					+ numberOfNonControlledPortal;

			player.setStaffAvailable(staffPoints);

			if (staffPoints > maxStaffPoints) {
				maxStaffPoints = staffPoints;
			}
		}

		this.gameBoard.setCachedMaxStaffPoints(maxStaffPoints);

		// Increment maxStaffPoints to an even number.
		if (maxStaffPoints % 2 == 1) {
			++maxStaffPoints;
		}

		adjustNumberOfStaffMembersTo(maxStaffPoints / 2);
	}

	private void releaseCapturedUnits(Player player) {
		for (Unit unit : player.getUnitCaptured()) {
			this.gameBoard.getPlayer(unit.getFaction()).getUnitCounter().removeUnit(unit.getType());
		}

		player.getUnitCaptured().clear();
	}

	private void adjustNumberOfStaffMembersTo(int minStaffPoints) {
		this.gameBoard.getPlayers().stream()
		              .filter(player -> player.getStaffAvailable() < minStaffPoints)
		              .forEach(player -> player.setStaffAvailable(minStaffPoints));
	}

	private static int getNumberOfNonControlledPortal(List<Studio> studios) {
		return (int) studios.stream().filter(Objects::isNull).count();
	}

	private static int getNumberOfControlledPortal(List<Studio> studios, Player player) {
		return (int) studios.stream().map(Studio::getCurrentFaction)
				.filter(faction -> faction == player.getFaction()).count();
	}
}
