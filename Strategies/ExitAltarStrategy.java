package scripts.LANRunecrafter.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Player;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LANRunecrafter.Constants.Conditions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class ExitAltarStrategy implements IStrategy {

    LogProxy log = new LogProxy("ExitAltarStrategy");

    @Override
    public boolean isValid() {
        AbstractAltar altar = Variables.getInstance().get("altar");

        return altar.getAltarArea().contains(Player.getPosition());
    }

    @Override
    public void run() {

        AbstractAltar altar = Variables.getInstance().get("altar");

        PaintHelper.statusText = "Exiting altar";

        ObjectsHelper.interact("Use");

        Timing.waitCondition(Conditions.UntilNotInAltar(altar), General.random(4000, 5000));
    }


    @Override
    public int priority() {
        return 8;
    }
}
