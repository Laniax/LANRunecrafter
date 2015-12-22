package scripts.LANRunecrafter;

import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;
import scripts.LANRunecrafter.Altars.*;
import scripts.LANRunecrafter.Strategies.*;
import scripts.LANRunecrafter.UI.GUI;
import scripts.LANRunecrafter.UI.PaintInfo;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Script.AbstractScript;

import javax.swing.*;

/**
 * @author Laniax
 */

@ScriptManifest(authors = {"Laniax"}, category = "Runecrafting", name = "[LAN] Runecrafter")
public class LANRunecrafter extends AbstractScript implements Painting, EventBlockingOverride, MouseActions, MousePainting {

    public static AbstractAltar[] ActiveAltars = {
            new AirAltar(),
            new EarthAltar(),
            new FireAltar(),
            new WaterAltar(),
            new BodyAltar(),
    };

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
}