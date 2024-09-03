package de.fisch37.cwpsminimaps.gui;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

public class SortOption extends BaseComponent {
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

    public static final int ICON_SIZE = WaypointListComponent.SORT_HEIGHT;
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final Text title;
    private boolean inverted = false;
    private boolean active = false;
    private BiConsumer<SortOption, Boolean> callback = (w, b) -> {};

    public SortOption(Text sortName) {
        title = sortName;
    }

    public SortOption callback(BiConsumer<SortOption, Boolean> func) {
        callback = func;
        return this;
    }

    public SortOption setActive(boolean state) {
        active = state;
        return this;
    }

    public int getPreCalcWidth() {
        return determineHorizontalContentSize(Sizing.content());
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        final boolean isClick = button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        if (isClick) {
            this.onClick();
        }

        return super.onMouseDown(mouseX, mouseY, button) | isClick;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        final boolean isEnter = keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
        if (isEnter) {
            this.onClick();
        }

        return super.onKeyPress(keyCode, scanCode, modifiers) | isEnter;
    }

    private void onClick() {
        if (active) inverted = !inverted;
        active = true;
        callback.accept(this, inverted);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0, 1 / MinecraftClient.getInstance().getWindow().getScaleFactor(), 0);

        // Could have just used height directly, but this keeps IntelliJ off my ass
        final int imageSize = height;
        if (active || hovered)
            context.drawGuiTexture(getTexture(inverted, hovered), x, y, imageSize, imageSize);
        context.drawText(
                textRenderer,
                title,
                x + imageSize, y + 4,
                0xFFFFFFFF,
                false
        );

        matrices.pop();
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        // A little dirty, but there's no good way to know the height at this point
        // (field "height" is 0)
        return textRenderer.getWidth(title) + determineVerticalContentSize(sizing);
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return Math.max(textRenderer.fontHeight, ICON_SIZE);
    }
}
