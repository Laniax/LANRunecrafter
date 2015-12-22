package scripts.LANRunecrafter.UI;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import scripts.LANRunecrafter.Altars.AbstractAltar;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Painting.PaintString;
import scripts.LanAPI.Game.Persistance.Variables;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Laniax
 */
public class PaintInfo extends AbstractPaintInfo {

    final Image _bg = PaintHelper.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/paint.png");
    final Image _toggle = PaintHelper.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptPaintToggle.png");

    final Font fontMed = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/SF%20Electrotome.ttf", 22f);
    final Font fontSmall = fontMed.deriveFont(18f);
    final Font fontLarge = fontMed.deriveFont(33f);

    private final int[][] skillXPLocations = new int[][] {
            {60, 433},
            {60, 458},
            {60, 483},

            {240, 433},
            {240, 458},
            {240, 483},
    };

    final Point runtimePos = new Point(115, 406);
    final Point statusPos = new Point(141, 372);
    final Point modePos = new Point(240, 406);
    final Point craftedPos = new Point(skillXPLocations[1][0], skillXPLocations[1][1]);
    final Point tripsPos = new Point(skillXPLocations[3][0], skillXPLocations[3][1]);
    final Point tripsHourPos = new Point(skillXPLocations[4][0], skillXPLocations[4][1]);

    @Override
    public Image getBackground() {
        return _bg;
    }

    @Override
    public Image getButtonPaintToggle() {
        return _toggle;
    }

    @Override
    public List<PaintString> getText(long runTime) {

        List<PaintString> result = new ArrayList<>();

        result.add(new PaintString(PaintHelper.statusText, statusPos, fontLarge, Color.WHITE, true));

        result.add(new PaintString(Timing.msToString(runTime), runtimePos, fontMed, Color.WHITE, true));

        AbstractAltar altar;
        if ((altar = Variables.getInstance().get("altar")) != null)
            result.add(new PaintString(String.format("Mode: %s", altar.toString()), modePos, fontMed, Color.WHITE, true));

        int runesCrafted = Variables.getInstance().get("runesCrafted", 0);
        result.add(new PaintString(String.format("%d runes crafted", runesCrafted), craftedPos, fontSmall, Color.WHITE, true));

        int trips = Variables.getInstance().get("trips", 0);
        result.add(new PaintString(String.format("Ran %d trips", trips), tripsPos, fontSmall, Color.WHITE, true));

        String averageTripTime = trips == 0 ? "-" : Timing.msToString(runTime / trips) ;
        result.add(new PaintString(averageTripTime, tripsHourPos, fontSmall, Color.WHITE, true));


        int i = 0;
        for (Map.Entry<Skills.SKILLS, Integer> s : SkillsHelper.getStartSkills().entrySet()) {

            int xpGained = SkillsHelper.getReceivedXP(s.getKey());

            double hours = runTime / 3600000.0;

            String xpHour = NumberFormat.getNumberInstance().format(Math.round(xpGained / hours));

            String str = String.format("%d (%s / hour)", xpGained, xpHour);
            Point pos = new Point(skillXPLocations[i][0], skillXPLocations[i][1]);

            result.add(new PaintString(str, pos, fontSmall, Color.WHITE, true));
            i++;
        }

        return result;
    }
}
