package de.fisch37.cwpsminimaps.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class SortOption extends ClickableWidget {
    private static final Identifier
        TEXTURE_ASCENDING = Identifier.of("cwps", "sort_ascending"),
        TEXTURE_ASCENDING_HIGHLIGHTED = Identifier.of("cwps","sort_ascending_highlighted"),
        TEXTURE_DESCENDING = Identifier.of("cwps","sort_descending"),
        TEXTURE_DESCENDING_HIGHLIGHTED = Identifier.of("cwps","sort_descending_highlighted")
                ;

    private static Identifier getTexture(boolean inverted, boolean hovered) {
        // I thought about doing this with an array lookup, but that would probably have ended up slower in Java.
        // Still, I'd have liked a value table more.
        if (inverted) {
            return hovered ? TEXTURE_DESCENDING_HIGHLIGHTED : TEXTURE_DESCENDING;
        } else {
            return hovered ? TEXTURE_ASCENDING_HIGHLIGHTED : TEXTURE_ASCENDING;
        }
    }

    private boolean inverted = false;
    private boolean active = false;
    private BiConsumer<SortOption, Boolean> callback = (w, b) -> {};

    public SortOption(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    public SortOption callback(BiConsumer<SortOption, Boolean> func) {
        callback = func;
        return this;
    }

    public SortOption setActive(boolean state) {
        active = state;
        return this;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        final int shortestSide = Math.min(width, height);
        if (active || isHovered())
            context.drawGuiTexture(getTexture(inverted, isHovered()), getX(), getY(), shortestSide, shortestSide);
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                getMessage(),
                getX() + shortestSide, getY() + 4,
                0xFFFFFFFF,
                false
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.HINT, Text.translatable("narrator.cwps.sort_option"));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (active) inverted = !inverted;
        active = true;
        callback.accept(this, inverted);
    }
}
