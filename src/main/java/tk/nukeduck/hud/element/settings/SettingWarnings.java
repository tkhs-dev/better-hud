package tk.nukeduck.hud.element.settings;

import tk.nukeduck.hud.util.Direction;

public class SettingWarnings extends SettingStub<Integer[]> {
	private final SettingBoolean active = new SettingBoolean("enabled");
	private final SettingSlider[] sliders;

	public SettingWarnings(String name) {
		this(name, 3);
	}

	public SettingWarnings(String name, int warnings) {
		super(name);
		add(new Legend("damageWarning"));

		sliders = new SettingSlider[warnings];
		for(int i = 0; i < sliders.length; i++) {
			add(sliders[i] = new SettingPercentage(String.valueOf(i), 0.01) {
				@Override
				public boolean enabled() {
					return SettingWarnings.this.active.get() && super.enabled();
				}
			}.setAlignment((i & 1) == 1 ? Direction.EAST : Direction.WEST));
		}
	}

	public void setActive(boolean active) {
		this.active.set(active);
	}

	@Override
	public void set(Integer[] values) {
		for(int i = 0; i < sliders.length; i++) {
			if(values[i] >= 0) {
				sliders[i].set(Double.valueOf(values[i]));
			}
		}
	}

	@Override
	public Integer[] get() {
		Integer[] values = new Integer[sliders.length];

		for(int i = 0; i < sliders.length; i++) {
			values[i] = sliders[i].get().intValue();
		}
		return values;
	}

	public int getWarning(float value) {
		if(!active.get()) return 0;

		for(int i = sliders.length - 1; i >= 0; i++) {
			if(value <= sliders[i].get()) return i + 1;
		}
		return 0;
	}
}
