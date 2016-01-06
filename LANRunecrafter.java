package scripts.LANRunecrafter;

import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.*;
import scripts.LANRunecrafter.Altars.*;
import scripts.LANRunecrafter.Strategies.*;
import scripts.LANRunecrafter.UI.GUI;
import scripts.LANRunecrafter.UI.PaintInfo;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Persistance.Variables;
import scripts.LanAPI.Game.Script.AbstractScript;
import scripts.LanAPI.Network.Connectivity.Signature;

import javax.swing.*;
import java.util.HashMap;

/**
 * @author Laniax
 */

@ScriptManifest(authors = {"Laniax"}, category = "Runecrafting", name = "[LAN] Runecrafter")
public class LANRunecrafter extends AbstractScript implements Painting, EventBlockingOverride, MouseActions, MousePainting, Ending {

    public static AbstractAltar[] ActiveAltars = {
            new AirAltar(),
            new EarthAltar(),
            new FireAltar(),
            new WaterAltar(),
            new BodyAltar(),
    };
    private boolean isEnded = false;

    @Override
    public IStrategy[] getStrategies() {
        return new IStrategy[]{new BankingStrategy(), new CraftRunesStrategy(), new EnterAltarStrategy(), new ExitAltarStrategy(), new FetchTiaraOrTalismanStrategy(), new TravelToAltarStrategy(), new TravelToBankStrategy()};
    }

    @Override
    public JFrame getGUI() {
        return new GUI();
    }

    /**
     * This method is called once when the script starts and we are logged ingame, just before the paint/gui shows.
     */
    @Override
    public void onInitialize() {
        SkillsHelper.setStartSkills(new SKILLS[]{SKILLS.RUNECRAFTING});
    }

    @Override
    public AbstractPaintInfo getPaintInfo() {
        return new PaintInfo();
    }

    @Override
    public void onEnd() {

        if (isEnded)
            return;

        AbstractAltar altar = Variables.getInstance().get("altar");
        int runesCrafted = Variables.getInstance().get("runesCrafted", 0);
        int trips = Variables.getInstance().get("trips", 0);

        HashMap<String, Integer> vars = new HashMap<>();
        vars.put("xp", SkillsHelper.getReceivedXP(SKILLS.RUNECRAFTING));
        vars.put("mode", altar.index());
        vars.put("runesCrafted", runesCrafted);
        vars.put("trips", trips);

        if (Signature.send("http://laniax.eu/scripts/Runecrafter/signature/update", this.getRunningTime(), vars))
            log.debug("Succesfully posted signature data.");

        isEnded = true;
    }
}