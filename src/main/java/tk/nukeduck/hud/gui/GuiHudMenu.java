package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;
import tk.nukeduck.hud.util.Paginator;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.SortField;

@SideOnly(Side.CLIENT)
public class GuiHudMenu extends GuiScreen {
	private final Map<HudElement, ButtonRow> rows = new HashMap<HudElement, ButtonRow>(HudElement.ELEMENTS.size());
	final Paginator<HudElement> paginator = new Paginator<HudElement>();

	private final GuiButton returnToGame = new GuiActionButton(I18n.format("menu.returnToGame")).setCallback(() -> MC.displayGuiScreen(null));
	private final GuiButton enableAll = new GuiActionButton(I18n.format("betterHud.menu.enableAll")).setCallback(() -> setAll(true));
	private final GuiButton disableAll = new GuiActionButton(I18n.format("betterHud.menu.disableAll")).setCallback(() -> setAll(false));

	private final GuiButton resetDefaults = new GuiActionButton(I18n.format("betterHud.menu.saveLoad"))
		.setCallback(() -> MC.displayGuiScreen(new GuiConfigSaves(this)));

	private final GuiToggleButton globalToggle   = new GuiElementToggle(this, HudElement.GLOBAL);
	private final GuiActionButton globalSettings = new GuiOptionButton(this, HudElement.GLOBAL);

	private final GuiButton lastPage = new GuiActionButton(I18n.format("betterHud.menu.lastPage"))
		.setCallback(() -> {paginator.previousPage(); initGui();});

	private final GuiButton nextPage = new GuiActionButton(I18n.format("betterHud.menu.nextPage"))
		.setCallback(() -> {paginator.nextPage(); initGui();});

	private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
	private boolean descending;

	public SortField<HudElement> getSortCriteria() {
		return sortCriteria;
	}

	public boolean isDescending() {
		return descending;
	}

	public void initGui() {
		paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 110) / 24)));

		addDefaultButtons();
		Bounds buttonBounds = new Bounds(170, 20).align(new Point(width / 2, height / 16 + 78), Direction.NORTH);

		for(HudElement element : paginator.getPage()) {
			ButtonRow row = getRow(element);
			buttonList.addAll(row.getButtons());

			row.setBounds(buttonBounds);
			row.update();

			buttonBounds = buttonBounds.withY(buttonBounds.getBottom() + 4);
		}
	}

	private void addDefaultButtons() {
		Bounds buttons = new Bounds(300, 42).align(new Point(width / 2, height / 16 + 20), Direction.NORTH);
		Bounds halfWidth = new Bounds((buttons.getWidth() - 2) / 2, 20);
		Bounds thirdWidth = new Bounds((buttons.getWidth() - 4) / 3, 20);

		moveButton(returnToGame,   halfWidth.anchor(buttons, Direction.NORTH_WEST));

		Bounds global = halfWidth.anchor(buttons, Direction.NORTH_EAST);
		moveButton(globalToggle,   halfWidth.withWidth(halfWidth.getWidth() - 20).anchor(global, Direction.NORTH_WEST));
		moveButton(globalSettings, halfWidth.withWidth(20).anchor(global, Direction.NORTH_EAST));

		moveButton(enableAll,     thirdWidth.anchor(buttons, Direction.SOUTH_WEST));
		moveButton(disableAll,    thirdWidth.anchor(buttons,      Direction.SOUTH));
		moveButton(resetDefaults, thirdWidth.anchor(buttons, Direction.SOUTH_EAST));

		lastPage.enabled = paginator.hasPrevious();
		nextPage.enabled = paginator.hasNext();

		buttons = buttons.align(new Point(width / 2, height - 20 - height / 16), Direction.NORTH);
		moveButton(lastPage, thirdWidth.anchor(buttons, Direction.NORTH_WEST));
		moveButton(nextPage, thirdWidth.anchor(buttons, Direction.NORTH_EAST));

		buttonList.clear();

		buttonList.add(returnToGame);
		buttonList.add(globalToggle);
		globalToggle.updateText();
		buttonList.add(globalSettings);

		buttonList.add(enableAll);
		buttonList.add(disableAll);
		buttonList.add(resetDefaults);

		buttonList.add(lastPage);
		buttonList.add(nextPage);

		List<GuiActionButton> indexerControls = getIndexControls(SortType.values());
		Bounds bounds = new Bounds(5, height - 25, 50, 20);

		for(GuiActionButton button : indexerControls) {
			button.setBounds(bounds);
			bounds = bounds.withX(bounds.getRight() + SPACER);
		}
		buttonList.addAll(indexerControls);
	}

	private void moveButton(GuiButton button, Bounds bounds) {
		button.x = bounds.getX();
		button.y = bounds.getY();
		button.width = bounds.getWidth();
		button.height = bounds.getHeight();
	}

	@Override
	public void onGuiClosed() {
		BetterHud.CONFIG.saveSettings();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
		}
	}

	private void setAll(boolean enabled) {
		for(HudElement element : HudElement.ELEMENTS) {
			element.setEnabled(enabled);
		}

		HudElement.SORTER.markDirty(SortType.ENABLED);
		initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		int enabled = 0;
		for(HudElement element : HudElement.ELEMENTS) {
			if(element.settings.get()) ++enabled;
		}

		drawCenteredString(fontRenderer, I18n.format("betterHud.menu.hudSettings"), width / 2, height / 16 + 5, Colors.WHITE);
		drawString(fontRenderer, enabled + "/" + HudElement.ELEMENTS.size() + " enabled", 5, 5, Colors.WHITE);

		String page = I18n.format("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
		drawCenteredString(fontRenderer, page, width / 2, height - height / 16 - 13, Colors.WHITE);
	}

	private List<GuiActionButton> getIndexControls(SortField<HudElement>[] sortValues) {
		List<GuiActionButton> buttons = new ArrayList<GuiActionButton>(sortValues.length);

		for(SortField<HudElement> sortValue : sortValues) {
			buttons.add(new SortButton(this, sortValue));
		}
		return buttons;
	}

	private ButtonRow getRow(HudElement element) {
		return rows.computeIfAbsent(element, e -> new ButtonRow(this, e));
	}

	public boolean showArrows() {
		return sortCriteria == SortType.PRIORITY;
	}

	public void swapPriority(HudElement element, int delta) {
		if(sortCriteria != SortType.PRIORITY) {
			throw new IllegalStateException("Must be sorting by priority");
		}

		List<HudElement> data = paginator.getData();
		swapPriority(element, data.get(data.indexOf(element) + delta));
	}

	public void swapPriority(int first, int second) {
		if(sortCriteria != SortType.PRIORITY) {
			throw new IllegalStateException("Must be sorting by priority");
		}

		List<HudElement> data = paginator.getData();
		swapPriority(data.get(first), data.get(second));
	}

	public void swapPriority(HudElement first, HudElement second) {
		IGetSet.swap(first.settings.priority, second.settings.priority);

		HudElement.SORTER.markDirty(SortType.PRIORITY);
		initGui();
	}

	public void changeSort(SortField<HudElement> sortCriteria) {
		if(this.sortCriteria == sortCriteria) {
			descending = !descending;
		} else {
			this.sortCriteria = sortCriteria;
			descending = sortCriteria.isInverted();
		}

		initGui();
	}
}
