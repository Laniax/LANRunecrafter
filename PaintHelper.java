package scripts.LANRunecrafter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Map.Entry;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills.SKILLS;

import scripts.LanAPI.Paint;
import scripts.LanAPI.SkillsHelper;

/**
 * Helper class that handles the script's paint logic.
 * 
 * @author Laniax
 *
 */
public class PaintHelper {

	private final static Font font = Paint.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/SF%20Electrotome.ttf", 22f);
	private final static Font fontLarge = font.deriveFont(33f);
	private final static Font fontSmall = font.deriveFont(18f);

	private final static Image paint = Paint.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/Runecrafter/paint.png");
	private final static Image paintShow = Paint.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptPaintToggle.png");
	public final static Color colorTransparent = new Color(0, 0, 0, 128);
	public final static Rectangle paintToggle = new Rectangle(406, 465, 99, 26);
	
	public static final long startTime = System.currentTimeMillis();
	public static boolean showPaint = false;
	public static int runesCrafted = 0;
	public static int trips = 0;
	
	private static int[][] textLocations = new int[][] {
		 {60, 433},
		 {60, 458},
		 {60, 483},
		 
		 {240, 433},
		 {240, 458},
		 {240, 483},
	};
	
	public static void onPaint(Graphics g1) {
		
		if (showPaint) {
			
			Graphics2D g = (Graphics2D)g1;

			long timeRan = System.currentTimeMillis() - startTime;
			double secondsRan = (int) (timeRan/1000);
			double hoursRan = secondsRan/3600;
			g.drawImage(paint, 0, 249, null);
			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Paint.drawShadowedText(Paint.statusText, fontLarge, 141, 372, g);
			Paint.drawShadowedText(Timing.msToString(timeRan), font, 115, 406, g);
			
			AbstractAltar mode;
			if ((mode = LANRunecrafter.getAltar()) != null)
				Paint.drawShadowedText("Mode: " +mode.toString(), font, 240, 406, g);
			
			Paint.drawShadowedText(runesCrafted + " runes crafted", fontSmall, textLocations[1][0], textLocations[1][1], g);
			Paint.drawShadowedText("Ran "+ trips + " trips", fontSmall, textLocations[3][0], textLocations[3][1], g);
			
			String averageTripTime = trips == 0 ? "-" : Timing.msToString(timeRan / trips) ;
			Paint.drawShadowedText("Average trip time: "+ averageTripTime, fontSmall, textLocations[4][0], textLocations[4][1], g);
			
			int i = 0;
			for (Entry<SKILLS, Integer> s : SkillsHelper.getStartSkills().entrySet()) {
				
				int xpGained = SkillsHelper.getReceivedXP(s.getKey());
					
				String xp = NumberFormat.getNumberInstance().format(Math.round(xpGained / hoursRan));
				Paint.drawShadowedText(xpGained + " ("+xp+" XP/h)", fontSmall, textLocations[i][0], textLocations[i][1], g);
				i++;
			}
		} else {
			g1.drawImage(paintShow, paintToggle.x - 4 , paintToggle.y - 4, null);
		}
	}
}
