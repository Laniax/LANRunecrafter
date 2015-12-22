package scripts.LANRunecrafter.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LANRunecrafter.Constants.Conditions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Inventory.Inventory;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class EnterAltarStrategy implements IStrategy {

    LogProxy log = new LogProxy("EnterAltarStrategy");

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        int essenceCount;
        if (altar.requirePureEssence())
            essenceCount = Inventory.getCount("Pure essence");
        else
            essenceCount = Inventory.getCount(Filters.Items.nameContains("essence"));

        return essenceCount > 0 && altar.getAltarLocation().distanceTo(Player.getPosition()) < 7;
    }

    @Override
    public void run() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        PaintHelper.statusText = "Entering altar";

                final RSItem[] talisman;
                if ((talisman = Inventory.find(altar.getTalismanID())).length > 0) {

                    String uptext = Game.getUptext();

                    if (uptext == null || !uptext.equalsIgnoreCase("Use "+altar.getRuneName()+" talisman ->")) {
                        talisman[0].click();
                        General.sleep(Antiban.getUtil().DELAY_TRACKER.ITEM_INTERACTION.next());
                    } else {
                        ObjectsHelper.interact("Mysterious ruins", "Use");
                    }
        } else
            ObjectsHelper.interact("Enter");

        Timing.waitCondition(Conditions.UntilInAltar(altar), General.random(4000, 5000));
    }


    @Override
    public int priority() {
        return 5;
    }
}
