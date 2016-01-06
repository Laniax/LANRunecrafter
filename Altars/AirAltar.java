package scripts.LANRunecrafter.Altars;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 */
public class AirAltar extends AbstractAltar{
	
	private final RSArea bankArea = new RSArea(new RSTile(3009, 3358, 0), new RSTile(3018, 3355, 0));
	private final RSArea altarArea = new RSArea(new RSTile(2833, 4843, 0), new RSTile(2855, 4819, 0));
	private final RSTile altarLocation = new RSTile(2984, 3291, 0);

	@Override
	public int index() {
		return 0;
	}

	@Override
	public RSTile getAltarLocation() {
		return altarLocation;
	}

	@Override
	public RSArea getBankArea() {
		return bankArea; // falador east
	}

	@Override
	public String getRuneName() {
		return "Air";
	}

	@Override
	public int getTiaraID() {
		return 5527;
	}

	@Override
	public int getTalismanID() {
		return 1438;
	}

	@Override
	public RSArea getAltarArea() {
		return altarArea;
	}

	@Override
	public boolean requirePureEssence() {
		return false;
	}
}
