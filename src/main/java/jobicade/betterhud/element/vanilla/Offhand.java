package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.SPACER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class Offhand extends OverlayElement {
    private SettingPosition position;

    public Offhand() {
        super("offhand");

        settings.addChild(position = new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NONE));
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return !Minecraft.getMinecraft().player.getHeldItemOffhand().isEmpty()
            && !Minecraft.getMinecraft().playerController.isSpectator();
    }

    @Override
    public Rect render(OverlayContext context) {
        ItemStack offhandStack = Minecraft.getMinecraft().player.getHeldItemOffhand();
        EnumHandSide offhandSide = Minecraft.getMinecraft().player.getPrimaryHand().opposite();
        Direction offhand = offhandSide == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;

        Rect bounds = new Rect(22, 22);
        Rect texture = new Rect(24, 23, 22, 22);

        if(position.isDirection(Direction.SOUTH)) {
            bounds = bounds.align(OverlayElements.HOTBAR.getLastBounds().grow(SPACER).getAnchor(offhand), offhand.mirrorCol());
        } else {
            bounds = position.applyTo(bounds);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
        GlUtil.drawRect(bounds, texture);

        GlUtil.renderHotbarItem(bounds.translate(3, 3), offhandStack, context.getPartialTicks());
        return bounds;
    }
}
