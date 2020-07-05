package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/** A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name */
public abstract class Setting {
    private Setting parent = null;
    protected final List<Setting> children = new ArrayList<>();
    public final String name;

    private String unlocalizedName;

    /** Set to {@code true} to hide the setting from the GUI
     * @see #getGuiParts(List, Map, Point) */
    private boolean hidden = false;

    private BooleanSupplier enableOn = () -> true;

    public Setting(String name) {
        this.name = name;
        if(name != null) this.unlocalizedName = "betterHud.setting." + name;
    }

    public String getName() {
        return name;
    }

    public List<Setting> getChildren() {
        return children;
    }

    public abstract boolean hasValue();
    public abstract String getStringValue();

    /**
     * If {@code stringValue} is valid, sets the value accordingly.
     *
     * @param stringValue A string as generated by {@link #getStringValue()}.
     * @return {@code true} if the value was updated.
     */
    public abstract void loadStringValue(String stringValue) throws SettingValueException;

    public void setEnableOn(BooleanSupplier enableOn) {
        this.enableOn = enableOn;
    }

    public void setHidden() {
        this.hidden = true;
    }

    public final void addChild(Setting setting) {
        children.add(setting);
        setting.parent = this;
    }

    public final void addChildren(Iterable<Setting> settings) {
        for (Setting setting : settings) {
            addChild(setting);
        }
    }

    @SafeVarargs
    public final void addChildren(Setting... settings) {
        for (Setting setting : settings) {
            addChild(setting);
        }
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void setUnlocalizedName(String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    /** @return {@code true} if this element and its ancestors are enabled */
    public final boolean enabled() {
        return (parent == null || parent.enabled()) && enableOn.getAsBoolean();
    }

    /**
     * Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
     * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
     *
     * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, Point)})
     * populates {@code parts} and {@code callbacks} with those of the element's children
     *
     * @param topAnchor The top center anchor for GUI parts being added
     * @return The bottom center anchor directly below this setting's parts
     */
    public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point topAnchor) {
        for (Setting setting : children) {
            if (!setting.hidden) {
                topAnchor = setting.getGuiParts(parts, callbacks, topAnchor);
            }
        }
        return topAnchor;
    }

    /** Renders extra parts of this GUI */
    public void draw() {
        for(Setting setting : children) {
            setting.draw();
        }
    }

    // TODO why here?
    /** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
     * @param button The GuiButton that was pressed. */
    public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);

    /** Updates the GUI elements based on the state of other settings.
     * This is called when any button tied to a setting callback is pressed */
    public void updateGuiParts(Collection<Setting> settings) {
        for(Setting setting : children) {
            setting.updateGuiParts(settings);
        }
    }
}
