package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;

public class SettingDirection extends Setting {
	public GuiToggleButton topLeft, topCenter, topRight,
		middleLeft, middleCenter, middleRight,
		bottomLeft, bottomCenter, bottomRight;

	public SettingDirection(String name) {
		this(name, Direction.ALL);
	}

	public SettingDirection(String name, Direction... options) {
		this(name, Direction.flags(options));
	}

	public SettingDirection(String name, int options) {
		super(name);
		this.options = options;
	}

	private final int options;
	public Direction value;

	public boolean isValid(Direction direction) {
		return direction.in(options);
	}

	public void set(Direction direction) {
		if(isValid(direction)) value = direction;
	}

	protected GuiToggleButton[] radios;

	@Override
	public int getGuiParts(java.util.List<Gui> parts, java.util.Map<Gui,Setting> callbacks, int width, int y) {
		List<GuiToggleButton> radios = new ArrayList<GuiToggleButton>(9);

		if((options & Direction.TOP) != 0) {
			radios.add(topLeft = new GuiToggleButton(Direction.NORTH_WEST.ordinal(), width / 2 - 100, y, 20, 20, "", false));
			radios.add(topCenter = new GuiToggleButton(Direction.NORTH.ordinal(), width / 2 - 78, y, 20, 20, "", false));
			radios.add(topRight = new GuiToggleButton(Direction.NORTH_EAST.ordinal(), width / 2 - 56, y, 20, 20, "", false));
			y += 22;
		}

		if((options & Direction.HORIZONTAL) != 0 || (options & (Direction.TOP | Direction.BOTTOM)) != 0) {
			radios.add(middleLeft = new GuiToggleButton(Direction.WEST.ordinal(), width / 2 - 100, y, 20, 20, "", false));
			radios.add(middleCenter = new GuiToggleButton(Direction.CENTER.ordinal(), width / 2 - 78, y, 20, 20, "", false));
			radios.add(middleRight = new GuiToggleButton(Direction.EAST.ordinal(), width / 2 - 56, y, 20, 20, "", false));
			y += 22;
		}

		if((options & Direction.BOTTOM) != 0) {
			radios.add(bottomLeft = new GuiToggleButton(Direction.SOUTH_WEST.ordinal(), width / 2 - 100, y, 20, 20, "", false));
			radios.add(bottomCenter = new GuiToggleButton(Direction.SOUTH.ordinal(), width / 2 - 78, y, 20, 20, "", false));
			radios.add(bottomRight = new GuiToggleButton(Direction.SOUTH_EAST.ordinal(), width / 2 - 56, y, 20, 20, "", false));
			y += 22;
		}

		for(GuiToggleButton radio : radios) {
			parts.add(radio);
			callbacks.put(radio, this);
		}
		this.radios = radios.toArray(new GuiToggleButton[radios.size()]);
		otherAction(null);
		return y + SPACER;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		((GuiToggleButton)button).set(true);
		this.value = Direction.values()[button.id];
	}

	@Override
	public void otherAction(Collection<Setting> settings) {
		boolean enabled = enabled();

		for(GuiToggleButton button : radios) {
			button.enabled = enabled && isValid(Direction.values()[button.id]);
			button.set(button.id == value.ordinal());
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void draw() {
		final String text = I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.setting." + this.value.getUnlocalizedName()));
		final int x = middleRight.x + middleRight.width + 5;
		final int y = middleRight.y + (middleRight.height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) / 2;

		MC.fontRenderer.drawStringWithShadow(text, x, y, Colors.WHITE);
	}

	@Override
	public String save() {
		return value.getUnlocalizedName();
	}

	@Override
	public void load(String val) {
		value = Direction.fromUnlocalizedName(val);

		if(!isValid(value)) {
			value = Direction.NORTH_WEST;
		}
	}
}