package scripts.LANRunecrafter.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LANRunecrafter.Constants.Conditions;
import scripts.LANRunecrafter.LANRunecrafter;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.Condition;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class FetchTiaraOrTalismanStrategy implements IStrategy {

    LogProxy log = new LogProxy("FetchTiaraOrTalismanStrategy");

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        return !Equipment.isEquipped(altar.getTiaraID()) && !Inventory.hasItem(altar.getTalismanID());
    }

    @Override
    public void run() {

        AbstractAltar altar = Variables.getInstance().get("altar");

        // Check for tiara
        if (!Equipment.isEquipped(altar.getTiaraID())) {

            if (Banking.isBankScreenOpen())
                Banking.close();

            if (Inventory.hasItem(altar.getTiaraID())) {
                PaintHelper.statusText = "Equipping Tiara";
                if (equipTiara(altar))
                    return;
            }
        } else
            return;

        // Check for talisman
        if (Inventory.hasItem(altar.getTalismanID()))
            return;

        // Go to bank if we have neither
        if (!altar.getBankArea().contains(Player.getPosition())) {
            PaintHelper.statusText = "Banking for tiara or talisman";
            Movement.walkTo(altar.getBankArea().getRandomTile());
        }

        // Open bank
        if (Banking.openBank()) {

            Timing.waitCondition(Condition.UntilBankOpen, General.random(3000, 4000));

            Banking.depositAll();

            // Check for tiara
            RSItem[] tiaraOrTalisman;
            if ((tiaraOrTalisman = Banking.find(altar.getTiaraID())).length > 0) {
                // found a tiara in the bank!
                if (Banking.withdrawItem(tiaraOrTalisman[0], 1)) {
                    Timing.waitCondition(Conditions.UntilHasTiaraInInventory(altar), General.random(3000, 4000));
                    General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
                }
                Banking.close();
                // Check for talisman
            } else if ((tiaraOrTalisman = Banking.find(altar.getTalismanID())).length > 0) {
                // found a talisman in the bank!
                if (Banking.withdrawItem(tiaraOrTalisman[0], 1)) {
                    Timing.waitCondition(Conditions.UntilHasTalismanInInventory(altar), General.random(3000, 4000));
                    General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
                }
                Banking.close();
            } else {
                // couldn't find a talisman or tiara in the bank or inventory, stopping script.
                log.error("Couldn't find a talisman or tiara in your inventory or bank. Stopping script");
                LANRunecrafter.quitting = true;
                return;
            }
        }

    }

    private boolean equipTiara(AbstractAltar altar) {
        final RSItem[] tiara;
        if ((tiara = Inventory.find(altar.getTiaraID())).length > 0) {
            if (tiara[0].click("Wear")) {
                return Timing.waitCondition(Conditions.UntilTiaraEquipped(altar), General.random(2000, 3500));
            }
        }

        return false;
    }


    @Override
    public int priority() {
        return 10;
    }
}
