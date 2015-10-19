package scripts.LANRunecrafter;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;

import scripts.LanAPI.Antiban;
import scripts.LanAPI.Condition;
import scripts.LanAPI.Inventory;
import scripts.LanAPI.Movement;
import scripts.LanAPI.ObjectsHelper;
import scripts.LanAPI.Paint;
import scripts.LanAPI.SkillsHelper;

/**
 * @author Laniax
 */

@ScriptManifest(authors = { "Laniax" }, category = "Runecrafting", name = "[LAN] Runecrafter")
public class LANRunecrafter extends Script implements Painting, EventBlockingOverride, MouseActions, MousePainting {

	public static boolean quitting = false;
	public static boolean waitForGUI = true;

	protected static AbstractAltar altar;

	private static GUI gui;

	public static AbstractAltar[] ActiveAltars = {
		new AirAltar(),
		new EarthAltar(),
		new FireAltar(),
		new WaterAltar(),
		new BodyAltar(),
	};

	public static AbstractAltar getAltar() {
		return altar;
	}

	// singleton
	public static GUI getGUI() {
		return gui = gui == null ? new GUI() : gui;
	}
	
	private static Condition UntilTiaraEquipped = new Condition() {
		public boolean active() {
			General.sleep(50);
			return Equipment.isEquipped(getAltar().getTiaraID());
		}
	};

	private static Condition UntilHasTiaraInInventory = new Condition() {
		public boolean active() {
			General.sleep(50);
			return Inventory.hasItem(getAltar().getTiaraID());
		}
	};

	private static Condition UntilHasTalismanInInventory = new Condition() {
		public boolean active() {
			General.sleep(50);
			return Inventory.hasItem(getAltar().getTalismanID());
		}
	};

	private static Condition UntilInAltar = new Condition() {
		public boolean active() {
			General.sleep(50);
			return getAltar().getAltarArea().contains(Player.getPosition());
		}
	};

	private static Condition UntilNotInAltar = new Condition() {
		public boolean active() {
			General.sleep(50);
			return !getAltar().getAltarArea().contains(Player.getPosition());
		}
	};

	@Override
	public void run() {

		// wait until login bot is done.
		while (Login.getLoginState() != Login.STATE.INGAME)
			sleep(250);

		// We do this after making sure we logged in, otherwise we can't get the xp.
		SkillsHelper.setStartSkills(new SKILLS[] {SKILLS.RUNECRAFTING});

		PaintHelper.showPaint = true;

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				getGUI().setVisible(true);
			}});

		while (waitForGUI)
			sleep(250);

		General.useAntiBanCompliance(true);

		while (!quitting) {
			State state = State.getState();
			if (state != null)
				state.run();
			sleep(50);
		}

		long timeRan = System.currentTimeMillis() - PaintHelper.startTime;

		General.println("Thank you for using [LAN] Runecrafter! You have crafted "+PaintHelper.runesCrafted+" runes in "+ Timing.msToString(timeRan));
	}

	public static void getTiaraOrTalisman() {
		// Check for tiara
		if (!Equipment.isEquipped(getAltar().getTiaraID())) {
			
			if (Banking.isBankScreenOpen()) 
				Banking.close();
			
			if (Inventory.hasItem(getAltar().getTiaraID())) {
				Paint.statusText = "Equipping Tiara";
				if (equipTiara())
					return;
			}
		} else 
			return;

		// Check for talisman
		if (Inventory.hasItem(getAltar().getTalismanID()))
			return;

		// Go to bank if we have neither
		if (!getAltar().getBankArea().contains(Player.getPosition())) {
			Paint.statusText = "Banking for tiara or talisman";
			goToBank();
		} else {

			// Open bank
			if (Banking.openBank()) {

				Timing.waitCondition(Condition.UntilBankOpen, General.random(3000, 4000));

				Banking.depositAll();

				// Check for tiara
				RSItem[] tiaraOrTalisman;
				if ((tiaraOrTalisman = Banking.find(getAltar().getTiaraID())).length > 0) {
					// found a tiara in the bank!
					Banking.withdrawItem(tiaraOrTalisman[0], 1);
					Timing.waitCondition(UntilHasTiaraInInventory, General.random(3000, 4000));
					General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
					Banking.close();
				// Check for talisman
				} else if ((tiaraOrTalisman = Banking.find(getAltar().getTalismanID())).length > 0) {
					// found a talisman in the bank!
					Banking.withdrawItem(tiaraOrTalisman[0], 1);
					Timing.waitCondition(UntilHasTalismanInInventory, General.random(3000, 4000));
					General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
					Banking.close();
				} else {
					// couldn't find a talisman or tiara in the bank or inventory, stopping script.
					General.println("Couldn't find a talisman or tiara in your inventory or bank. Stopping script");
					quitting = true;
					return;
				}
			}
		}
	}

	public static void goToAltar() {

		Paint.statusText = "Going to altar";
		
		Antiban.doIdleActions();

		Movement.walkTo(getAltar().getAltarLocation());
	}

	private static boolean equipTiara() {
		final RSItem[] tiara;
		if ((tiara = Inventory.find(getAltar().getTiaraID())).length > 0) {
			if (tiara[0].click("Wear")) {
				return Timing.waitCondition(UntilTiaraEquipped, General.random(2000, 3500));
			}
		}

		return false;
	}

	public static void enterAltar() {
		
		Paint.statusText = "Entering altar";

		// check tiara or talisman
		if (!Equipment.isEquipped(getAltar().getTiaraID())) {

			if (!equipTiara()) {

				final RSItem[] talisman;
				if ((talisman = Inventory.find(getAltar().getTalismanID())).length > 0) {
					
					String uptext = Game.getUptext();
					
					if (uptext == null || !uptext.equalsIgnoreCase("Use "+altar.getRuneName()+" talisman ->")) {
						talisman[0].click();
						General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
					} else {
						ObjectsHelper.interact("Mysterious ruins", "Use");
					}
				} 
			}
		} else 
			ObjectsHelper.interact("Enter");
		
		Timing.waitCondition(UntilInAltar, General.random(4000, 5000));
	}

	public static void exitAltar() {

		Paint.statusText = "Exiting altar";

		ObjectsHelper.interact("Use");

		Timing.waitCondition(UntilNotInAltar, General.random(4000, 5000));
	}

	public static void craftRunes() {

		Paint.statusText = "Crafting runes";

		final int preEssence = Inventory.getCount(getAltar().getRuneName() + " rune");

		ObjectsHelper.interact("Craft-rune");

		Timing.waitCondition(new Condition() {
			public boolean active() {
				General.sleep(50);
				return Inventory.find("essence").length != 0 && Player.getAnimation() == -1;
			}
		}, General.random(4000, 5000));

		int newEssence =  Math.abs(Inventory.getCount(getAltar().getRuneName() + " rune") - preEssence);

		if (newEssence > 0) {
			PaintHelper.trips++;
			PaintHelper.runesCrafted += newEssence;
			
			Antiban.doIdleActions();
		}
	}

	public static void doBanking() {

		Paint.statusText = "Banking";

		if (Banking.openBank()) {

			Timing.waitCondition(Condition.UntilBankOpen, General.random(3000, 4000));

			if (!Inventory.isEmpty()) {

				boolean hasTiara = Equipment.isEquipped(getAltar().getTiaraID());

				if (hasTiara)
					Banking.depositAll();
				else 
					Banking.depositAllExcept(getAltar().getTalismanID());

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return hasTiara ? Inventory.isEmpty() : Inventory.getAmountOfFreeSpace() == 27;
					}}, General.random(3000, 4000));
			}

			String essence = getAltar().requirePureEssence() ? "Pure essence" : "Rune essence";

			if (Banking.find(Filters.Items.nameContains(essence)).length > 0) {

				final int preWithdraw = Inventory.getCount(essence);

				Banking.withdraw(0, essence);
				
				General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return Inventory.getCount(essence) != preWithdraw;
					}}, General.random(3000, 4000));
			} else {
				General.println("Out of "+essence+"! Stopping script and logging out.");
				Login.logout();
				quitting = true;
				return;
			}

			Banking.close();
		}
	}

	public static void goToBank() {

		if (getAltar().getBankArea().contains(Player.getPosition()))
			return;

		Paint.statusText = "Going to bank";
		
		Antiban.doIdleActions();

		Movement.walkTo(getAltar().getBankArea().getRandomTile());
	}

	// Paint is handled in different file for better readability
	public void onPaint(Graphics g) { PaintHelper.onPaint(g); }

	// Mouse actions below are for hiding/showing paint.
	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_CLICKED) {

			if (PaintHelper.paintToggle.contains(e.getPoint())) {

				PaintHelper.showPaint = !PaintHelper.showPaint;

				e.consume();
				return OVERRIDE_RETURN.DISMISS;
			}
		}

		return OVERRIDE_RETURN.PROCESS;
	}


	public void paintMouse(Graphics g, Point mousePos, Point dragPos) {
		Paint.drawMouse(g, mousePos, dragPos);
	}

	public void mouseClicked(Point p, int button, boolean isBot) {
		Paint.mouseDown = true;
	}

	public void paintMouseSpline(Graphics g, ArrayList<Point> points) {} // remove mouse trail

	// unused overrides
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {return OVERRIDE_RETURN.SEND;}
	public void mouseReleased(Point p, int button, boolean isBot) {}
	public void mouseDragged(Point p, int movePos, boolean dragPos) {}
	public void mouseMoved(Point p, boolean isBot) {}
}