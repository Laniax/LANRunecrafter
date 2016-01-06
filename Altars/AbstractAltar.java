package scripts.LANRunecrafter.Altars;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

/**
 * @author: Laniax
 */
public abstract class AbstractAltar {

	public abstract int index();
	
	public abstract RSTile getAltarLocation();
	
	public abstract RSArea getBankArea();
	
	public abstract RSArea getAltarArea();
	
	public abstract String getRuneName();
	
	public abstract int getTiaraID();
	
	public abstract int getTalismanID();
	
	public abstract boolean requirePureEssence();
	
	// purely used by the settings panel
	public String toString() {
		return getRuneName() + " runes";
	}
	
}

