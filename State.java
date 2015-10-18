package scripts.LANRunecrafter;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.LanAPI.Inventory;

/**
 * @author Laniax
 */

public enum State {
	GET_TIARA_OR_TALISMAN {
		@Override
		public void run() { LANRunecrafter.getTiaraOrTalisman(); }
	},
	BANK {
		@Override
		public void run() { LANRunecrafter.doBanking(); }
	},
	GO_TO_ALTAR {
		@Override
		public void run() { LANRunecrafter.goToAltar(); }
	},
	ENTER_ALTAR {
		@Override
		public void run() { LANRunecrafter.enterAltar(); }
	},
	EXIT_ALTAR {
		@Override
		public void run() { LANRunecrafter.exitAltar(); }
	},
	CRAFT_RUNES {
		@Override
		public void run() { LANRunecrafter.craftRunes(); }
	},
	GO_TO_BANK {
		@Override
		public void run() { LANRunecrafter.goToBank(); }
	};

	public abstract void run();

	public static State getState() {
		
		RSTile playerPos = Player.getPosition();
		String essence = LANRunecrafter.getAltar().requirePureEssence() ? "Pure essence" : "Rune essence";
		int essenceCount = Inventory.getCount(essence);
		AbstractAltar altar = LANRunecrafter.getAltar();
		
		// Check if we have a valid tiara or talisman
		if (!Equipment.isEquipped(altar.getTiaraID()) && !Inventory.hasItem(altar.getTalismanID())) 
			return GET_TIARA_OR_TALISMAN;
		
		// If we are inside the altar, decide what to do.
		if (altar.getAltarArea().contains(playerPos)) {
			if (essenceCount > 0) 
				return CRAFT_RUNES;
			 else 
				return EXIT_ALTAR;
		}
		
		// If we are inside the bank, decide what to do.
		if (altar.getBankArea().contains(playerPos)) {
			if (essenceCount > 0) {
				// We are in the bank, with atleast 1 essence we can use.
				return GO_TO_ALTAR;
			} else 
				return BANK;
		}
		
		// If we are anywhere else, go to the alter if we have essence, otherwise go to the bank.
		if (essenceCount > 0) {
			if (altar.getAltarLocation().distanceTo(playerPos) < 7) {
				// if we are near the altar, enter it.
				return ENTER_ALTAR;
			} else 
				return GO_TO_ALTAR;
		} else 
			return GO_TO_BANK;
	}
}