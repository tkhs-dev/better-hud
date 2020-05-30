package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Paginator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiHudMenu extends GuiMenuScreen {
	// TODO complexity from initial capacity
	private final Map<HudElement<?>, ButtonRow> rows = new HashMap<HudElement<?>, ButtonRow>(HudElements.get().getRegistered().size());
	final Paginator<HudElement<?>> paginator = new Paginator<HudElement<?>>();

	private final GuiActionButton returnToGame = new GuiActionButton(I18n.format("menu.returnToGame")).setCallback(b -> Minecraft.getMinecraft().displayGuiScreen(null));
	private final GuiActionButton toggleAll = new GuiActionButton("").setCallback(b -> setAll(!allEnabled()));
	private final GuiActionButton reorder = new GuiActionButton(I18n.format("betterHud.menu.reorder")).setCallback(b -> Minecraft.getMinecraft().displayGuiScreen(new GuiReorder(this)));

	private final GuiActionButton resetDefaults = new GuiActionButton(I18n.format("betterHud.menu.saveLoad"));

	private final ButtonRow globalRow = new ButtonRow(this, HudElements.GLOBAL);

	private final GuiActionButton lastPage = new GuiActionButton(I18n.format("betterHud.menu.lastPage"))
		.setCallback(b -> {paginator.previousPage(); initGui();});

	private final GuiActionButton nextPage = new GuiActionButton(I18n.format("betterHud.menu.nextPage"))
		.setCallback(b -> {paginator.nextPage(); initGui();});

	private SortField sortCriteria = SortField.ALPHABETICAL;
	private boolean descending;

	public GuiHudMenu(ConfigManager configManager) {
		resetDefaults.setCallback(button -> {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfigSaves(configManager, this));
		});
	}

	public SortField getSortCriteria() {
		return sortCriteria;
	}

	public boolean isDescending() {
		return descending;
	}

	private boolean allEnabled() {
		return HudElements.get().getRegistered().stream().allMatch(HudElement::isEnabled);
	}

	public void initGui() {
		setTitle(I18n.format("betterHud.menu.hudSettings"));

		List<HudElement<?>> pageData = HudElements.get().getRegistered(sortCriteria);
		if (descending != sortCriteria.isInverted()) {
			pageData = Lists.reverse(pageData);
		}

		paginator.setData(pageData);
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 134) / 24)));

		addDefaultButtons();
		Rect buttonRect = new Rect(170, 20).align(getOrigin().add(0, 82), Direction.NORTH);

		for(HudElement<?> element : paginator.getPage()) {
			ButtonRow row = getRow(element);
			buttonList.addAll(row.getButtons());

			row.setBounds(buttonRect);
			row.update();

			buttonRect = buttonRect.withY(buttonRect.getBottom() + 4);
		}
	}

	private void addDefaultButtons() {
		Rect buttons = new Rect(300, 42).align(getOrigin(), Direction.NORTH);
		Rect halfWidth = new Rect((buttons.getWidth() - 2) / 2, 20);
		Rect thirdWidth = new Rect((buttons.getWidth() - 4) / 3, 20);

		returnToGame.setBounds(halfWidth.anchor(buttons, Direction.NORTH_WEST));

		globalRow.setBounds(halfWidth.anchor(buttons, Direction.NORTH_EAST));

		toggleAll.setBounds(thirdWidth.anchor(buttons, Direction.SOUTH_WEST));
		reorder.setBounds(thirdWidth.anchor(buttons,      Direction.SOUTH));
		resetDefaults.setBounds(thirdWidth.anchor(buttons, Direction.SOUTH_EAST));
		toggleAll.displayString = I18n.format(allEnabled() ? "betterHud.menu.disableAll" : "betterHud.menu.enableAll");

		lastPage.enabled = paginator.hasPrevious();
		nextPage.enabled = paginator.hasNext();

		buttons = buttons.align(new Point(width / 2, height - 20 - height / 16), Direction.NORTH);
		lastPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_WEST));
		nextPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_EAST));

		buttonList.clear();

		buttonList.add(returnToGame);
		buttonList.addAll(globalRow.getButtons());
		globalRow.update();

		buttonList.add(toggleAll);
		buttonList.add(reorder);
		buttonList.add(resetDefaults);

		buttonList.add(lastPage);
		buttonList.add(nextPage);

		List<GuiActionButton> indexerControls = getIndexControls(SortField.values());
		Rect sortButton = new Rect(75, 20);
		Rect bounds = sortButton.withWidth((sortButton.getWidth() + SPACER) * indexerControls.size() - SPACER).align(getOrigin().add(0, 58), Direction.NORTH);
		sortButton = sortButton.move(bounds.getPosition());

		for(GuiActionButton button : indexerControls) {
			button.setBounds(sortButton);
			sortButton = sortButton.withX(sortButton.getRight() + SPACER);
		}
		buttonList.addAll(indexerControls);
	}

	private void setAll(boolean enabled) {
		for(HudElement<?> element : HudElements.get().getRegistered()) {
			element.setEnabled(enabled);
		}

		HudElements.get().invalidateSorts(SortField.ENABLED);
		initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		int enabled = (int)HudElements.get().getRegistered().stream().filter(HudElement::isEnabled).count();
		GlUtil.drawString(enabled + "/" + HudElements.get().getRegistered().size() + " enabled", new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

		String page = I18n.format("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
		drawCenteredString(fontRenderer, page, width / 2, height - height / 16 - 13, Color.WHITE.getPacked());
	}

	private List<GuiActionButton> getIndexControls(SortField[] sortValues) {
		List<GuiActionButton> buttons = new ArrayList<GuiActionButton>(sortValues.length);

		for(SortField sortValue : sortValues) {
			buttons.add(new SortButton(this, sortValue));
		}
		return buttons;
	}

	private ButtonRow getRow(HudElement<?> element) {
		return rows.computeIfAbsent(element, e -> new ButtonRow(this, e));
	}

	public void changeSort(SortField sortCriteria) {
		if(this.sortCriteria == sortCriteria) {
			descending = !descending;
		} else {
			this.sortCriteria = sortCriteria;
			descending = sortCriteria.isInverted();
		}

		initGui();
	}
}
