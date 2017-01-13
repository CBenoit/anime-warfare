package org.tiwindetea.animewarfare.gui.game.gameboard.ItemFilters;

import javafx.scene.control.MenuItem;
import org.tiwindetea.animewarfare.MainApp;
import org.tiwindetea.animewarfare.gui.game.CostModifierMonitor;
import org.tiwindetea.animewarfare.gui.game.GameLayoutController;
import org.tiwindetea.animewarfare.logic.FactionType;
import org.tiwindetea.animewarfare.logic.units.UnitLevel;
import org.tiwindetea.animewarfare.logic.units.UnitType;
import org.tiwindetea.animewarfare.net.networkrequests.client.NetInvokeUnitRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HimejiInvocation extends AbstractZoneFilter {
	@Override
	public List<MenuItem> apply(FactionType factionType, Integer zone) {
		if (GameLayoutController.getMap().getStudiosOf(UnitType.HIMEJI_MIZUKI.getDefaultFaction()).isEmpty()) {
			return Collections.emptyList();
		}

		if (checkHimejiInvocationConditions(factionType, zone)) {
			int cost = UnitType.HIMEJI_MIZUKI.getDefaultCost() + CostModifierMonitor.getUnitCostModifier(UnitType.HIMEJI_MIZUKI);

			if (GameLayoutController.getLocalPlayerInfoPane()
			                        .getStaffCounter()
			                        .getValue() >= cost) {
				MenuItem menuItem = new MenuItem("Invoke Himeji");
				menuItem.setOnAction(e -> MainApp.getGameClient()
				                                 .send(new NetInvokeUnitRequest(UnitType.HIMEJI_MIZUKI,
						                                 zone)));

				return Arrays.asList(menuItem);
			}
		}

		return Collections.emptyList();
	}

	private boolean checkHimejiInvocationConditions(FactionType factionType, Integer zone) {
		return GameLayoutController.getMap().getUnits(zone)
		                           .stream()
		                           .filter(u -> u.getFaction() == factionType && u
				                           .getType()
				                           .isLevel(UnitLevel.MASCOT))
		                           .count() >= 2;
	}

	@Override
	public String getName() {
		return "himeji invocation";
	}

	@Override
	public void destroy() {

	}
}