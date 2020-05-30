package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class PortalOverlay extends OverrideElement {
	public PortalOverlay() {
		setRegistryName("portal");
		setUnlocalizedName("portal");
	}

	@Override
	protected ElementType getType() {
		return ElementType.PORTAL;
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return getTimeInPortal(context.getPartialTicks()) > 0;
	}

	private float getTimeInPortal(float partialTicks) {
		return MathUtil.lerp(
			Minecraft.getMinecraft().player.prevTimeInPortal,
			Minecraft.getMinecraft().player.timeInPortal, partialTicks);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		float timeInPortal = getTimeInPortal(context.getPartialTicks());

		if(timeInPortal < 1) {
			timeInPortal *= timeInPortal;
			timeInPortal *= timeInPortal;

			timeInPortal = timeInPortal * 0.8f + 0.2f;
		}

		Color.WHITE.withAlpha(Math.round(timeInPortal * 255)).apply();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		TextureAtlasSprite texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PORTAL.getDefaultState());
		Rect screen = MANAGER.getScreen();
		Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(0, 0, texture, screen.getWidth(), screen.getHeight());

		return null;
	}
}
