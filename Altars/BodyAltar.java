package scripts.LANRunecrafter.Altars;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 */
public class BodyAltar extends AbstractAltar{
	
	private final RSArea bankArea = new RSArea(new RSTile(3091, 3494, 0), new RSTile(3094, 3488, 0));
	private final RSArea altarArea = new RSArea(new RSTile(2508, 4849, 0), new RSTile(2538, 4820, 0));
	private final RSTile altarLocation = new RSTile(3052, 3444, 0);

	@Override
	public int index() {
		return 1;
	}

	@Override
	public RSTile getAltarLocation() {
		return altarLocation;
	}

	@Override
	public RSArea getBankArea() {
		return bankArea; // edgeville
	}

	@Override
	public String getRuneName() {
		return "Body";
	}

	@Override
	public int getTiaraID() {
		return 5533;
	}

	@Override
	public int getTalismanID() {
		return 1446;
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
