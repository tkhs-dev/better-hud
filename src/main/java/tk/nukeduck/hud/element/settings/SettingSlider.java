package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiOptionSliderA;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.ISaveLoad.ISlider;

public class SettingSlider extends SettingAlignable<Double> implements ISlider {
	private GuiOptionSliderA slider;
	private final double min, max, interval;

	private int displayPlaces;
	private String unlocalizedValue;

	private double value;

	public SettingSlider(String name, double min, double max) {
		this(name, min, max, -1);
	}

	public SettingSlider(String name, double min, double max, double interval) {
		super(name, Direction.CENTER);
		this.min = min;
		this.max = max;
		this.interval = interval;

		setDisplayPlaces(interval == (int)interval ? 0 : 1);
		set(getMinimum());
	}

	public SettingSlider setAlignment(Direction alignment) {
		this.alignment = alignment;
		return this;
	}

	public SettingSlider setDisplayPlaces(int displayPlaces) {
		this.displayPlaces = displayPlaces;
		return this;
	}

	public SettingSlider setUnlocalizedValue(String unlocalizedValue) {
		this.unlocalizedValue = unlocalizedValue;
		return this;
	}

	@Override
	public String getDisplayString() {
		return I18n.format("betterHud.setting." + name) + ": " + getDisplayValue(get());
	}

	public String getDisplayValue(double value) {
		String displayValue = FormatUtil.formatToPlaces(value, displayPlaces);

		if(unlocalizedValue != null) {
			displayValue = I18n.format(unlocalizedValue, displayValue);
		}
		return displayValue;
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		slider = new GuiOptionSliderA(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), this);

		parts.add(slider);
		callbacks.put(slider, this);
		return bounds.bottom() + SPACER;
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting<?>> settings) {}

	@Override public Double get() {return value;}

	@Override
	public void set(Double value) {
		this.value = value;
		ISlider.normalize(this);
	}

	@Override
	public String save() {
		return get().toString();
	}

	@Override
	public void load(String save) {
		set(Double.valueOf(save));
		slider.displayString = getDisplayString();
	}

	@Override public Double getMinimum() {return min;}
	@Override public Double getMaximum() {return max;}
	@Override public Double getInterval() {return interval;}
}
