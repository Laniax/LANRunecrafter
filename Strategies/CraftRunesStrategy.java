package scripts.LANRunecrafter.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.Condition;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class CraftRunesStrategy implements IStrategy {

    LogProxy log = new LogProxy("CraftRunesStrategy");

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        int essenceCount;
        if (altar.requirePureEssence())
            essenceCount = Inventory.getCount("Pure essence");
         else
            essenceCount = Inventory.getCount(Filters.Items.nameContains("essence"));

        return altar.getAltarArea().contains(Player.getPosition()) && essenceCount > 0;
    }

    @Override
    public void run() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        PaintHelper.statusText = "Crafting runes";

        final int preEssence = Inventory.getCount(altar.getRuneName() + " rune");

        ObjectsHelper.interact("Craft-rune");

        Timing.waitCondition(new Condition() {
            public boolean active() {
                General.sleep(50);
                return Inventory.find("essence").length != 0 && Player.getAnimation() == -1;
            }
        }, General.random(4000, 5000));

        int newEssence =  Math.abs(Inventory.getCount(altar.getRuneName() + " rune") - preEssence);

        if (newEssence > 0) {

            int trips = Variables.getInstance().get("trips", 0);
            Variables.getInstance().addOrUpdate("trips", ++trips);

            int runesCrafted = Variables.getInstance().get("runesCrafted", 0) ;
            runesCrafted += newEssence;
            Variables.getInstance().addOrUpdate("runesCrafted", runesCrafted);

            Antiban.doIdleActions();
        }
    }


    @Override
    public int priority() {
        return 9;
    }
}
