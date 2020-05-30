package jobicade.betterhud.element.text;

import java.util.Date;

public class SystemClock extends Clock {
	public SystemClock() {
		setRegistryName("system_clock");
		setUnlocalizedName("systemClock");
	}

	@Override
	protected Date getDate() {
		return new Date();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		setEnabled(false);
	}
}
