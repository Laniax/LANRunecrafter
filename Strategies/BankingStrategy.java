package scripts.LANRunecrafter.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LANRunecrafter.LANRunecrafter;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.Condition;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class BankingStrategy implements IStrategy {

    LogProxy log = new LogProxy("BankingStrategy");

    private static int failEssenceWithdraw = 0;

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        int essenceCount;
        if (altar.requirePureEssence())
            essenceCount = Inventory.getCount("Pure essence");
        else
            essenceCount = Inventory.getCount(Filters.Items.nameContains("essence"));

        return altar.getBankArea().contains(Player.getPosition()) && essenceCount < 1;
    }

    @Override
    public void run() {
        AbstractAltar altar = Variables.getInstance().get("altar");
        boolean pureEssenceFallback = Variables.getInstance().get("pureEssenceFallback");

        PaintHelper.statusText = "Banking";

        if (Banking.openBank()) {

            Timing.waitCondition(Condition.UntilBankOpen, General.random(3000, 4000));

            if (!Inventory.isEmpty()) {

                boolean hasTiara = Equipment.isEquipped(altar.getTiaraID());

                if (hasTiara)
                    Banking.depositAll();
                else
                    Banking.depositAllExcept(altar.getTalismanID());

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return hasTiara ? Inventory.isEmpty() : Inventory.getAmountOfFreeSpace() == 27;
                    }}, General.random(3000, 4000));
            }

            String essence = "Pure essence";

            if (!altar.requirePureEssence()) {
                RSItem[] ess = Banking.find("Rune essence");
                // if there is no rune essence left, we should use pure essence (if the setting is checked)
                if (ess.length >= 20 || !pureEssenceFallback) {
                    // we dont have to check if we have enough pure essence, the below code will catch out-of-essence cases.
                    essence = "Rune essence";
                }
            }

            final int preWithdraw = Inventory.getCount(essence);

            if (Banking.withdraw(0, essence)) {

                failEssenceWithdraw = 0;

                final String ess = essence;

                General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return Inventory.getCount(ess) != preWithdraw;
                    }}, General.random(3000, 4000));

            } else {
                failEssenceWithdraw++;

                General.println("Failed to withdraw essence! Trying " + (4 - failEssenceWithdraw) + " more times before quitting.");

                if (failEssenceWithdraw > 3) {
                    General.println("Out of "+essence+"! Stopping script and logging out.");
                    Login.logout();
                    LANRunecrafter.quitting = true;
                    return;
                }
            }

            Banking.close();
        }
    }


    @Override
    public int priority() {
        return 7;
    }
}
