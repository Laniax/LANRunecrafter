package scripts.LANRunecrafter.Strategies;

import org.tribot.api2007.ext.Filters;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class TravelToAltarStrategy implements IStrategy {

    LogProxy log = new LogProxy("TravelToAltarStrategy");

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        int essenceCount;
        if (altar.requirePureEssence())
            essenceCount = Inventory.getCount("Pure essence");
        else
            essenceCount = Inventory.getCount(Filters.Items.nameContains("essence"));

        return essenceCount > 0;
    }

    @Override
    public void run() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        PaintHelper.statusText = "Going to altar";

        Antiban.doIdleActions();

        Movement.walkTo(altar.getAltarLocation());
    }


    @Override
    public int priority() {
        return 5;
    }
}
