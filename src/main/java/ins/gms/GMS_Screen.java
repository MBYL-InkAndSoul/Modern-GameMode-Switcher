package ins.gms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GMS_Screen extends GuiScreen {

    public static final int BG_WIDTH = 125;
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(
        "gms",
        "textures/gui/container/gamemode_switcher.png");
    private static final ResourceLocation NEI_BUTTON_TEXTURE = new ResourceLocation(
        "gms",
        "textures/gui/nei_sprites.png");
    private static final int[][] NEI_BUTTON_COORDS = { { 24, 24 }, { 96, 48 }, { 96, 24 } };

    private enum GameMode {

        CREATIVE {

            @Override
            public void drawVanillaIcon(GMS_Screen gui, int x, int y) {
                ItemStack stack = new ItemStack(Blocks.grass);
                gui.drawItem(stack, x, y);
            }
        },
        SURVIVAL {

            @Override
            public void drawVanillaIcon(GMS_Screen gui, int x, int y) {
                ItemStack stack = new ItemStack(Items.iron_shovel);
                gui.drawItem(stack, x, y);
            }
        },
        ADVENTURE {

            @Override
            public void drawVanillaIcon(GMS_Screen gui, int x, int y) {
                ItemStack stack = new ItemStack(Items.map);
                gui.drawItem(stack, x, y);
            }
        };

        public String getCommand() {
            return switch (this) {
                case SURVIVAL -> GMS_Config.survivalCommand();
                case CREATIVE -> GMS_Config.creativeCommand();
                case ADVENTURE -> GMS_Config.adventureCommand();
            };
        }

        public String getDisplayName() {
            return switch (this) {
                case SURVIVAL -> I18n.format("gameMode.survival");
                case CREATIVE -> I18n.format("gameMode.creative");
                case ADVENTURE -> I18n.format("gameMode.adventure");
            };
        }

        public abstract void drawVanillaIcon(GMS_Screen gui, int x, int y);
    }

    private static class ModeButton {

        private final Minecraft mc;
        private final GameMode mode;
        private final GMS_Screen gui;
        private final int x;
        private final int y;

        public ModeButton(GMS_Screen gui, GameMode mode, int x, int y) {
            this.gui = gui;
            this.mode = mode;
            this.mc = gui.mc;
            this.x = x;
            this.y = y;
        }

        public void drawButton(int mouseX, int mouseY) {
            mc.getTextureManager()
                .bindTexture(BG_TEXTURE);
            gui.drawTexture(x, y, 0, 75, 26, 26, 128, 128);

            if (GMS_Config.iconStyle() == 1) {
                mc.getTextureManager()
                    .bindTexture(NEI_BUTTON_TEXTURE);
                int[] uv = NEI_BUTTON_COORDS[mode.ordinal()];
                gui.drawTexture(x, y, uv[0], uv[1], 16, 16, 512, 512);
            } else if (GMS_Config.iconStyle() == 0) {
                mode.drawVanillaIcon(gui, x + ((26 - 16) >> 1), y + ((26 - 16) >> 1));
            }

            if (mouseX != gui.lastMouseX || mouseY != gui.lastMouseY) {
                if (mouseX >= x && mouseX < x + 26 && mouseY >= y && mouseY < y + 26) {
                    gui.selectedMode = mode.ordinal();
                }
            }
        }
    }

    private int selectedMode = 0;

    public GMS_Screen(Minecraft mc, WorldSettings.GameType gameMode) {
        this.mc = mc;
        selectedMode = switch (gameMode) {
            case CREATIVE -> 0;
            case SURVIVAL -> 1;
            case ADVENTURE -> 2;
            default -> throw new IllegalArgumentException("Invalid game mode: " + gameMode);
        };
        initGui();
    }

    private final ModeButton[] buttons = new ModeButton[3];
    private int left, top;
    private int lastMouseX = 0, lastMouseY = 0;
    private boolean keyDown = false;

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        left = (width - BG_WIDTH) >> 1;
        top = (height - 75) >> 1;

        for (int i = 0; i < 3; i++) {
            buttons[i] = new ModeButton(this, GameMode.values()[i], getRenderX(i), top + 28);
        }
    }

    private int getRenderX(int index) {
        return switch (index) {
            case 0 -> left + 10;
            case 1 -> left + ((BG_WIDTH - 26) >> 1);
            case 2 -> left + BG_WIDTH - 26 - 10;
            default -> 0;
        };
    }

    @Override
    public void updateScreen() {
        if (!Keyboard.isKeyDown(61)) {
            mc.displayGuiScreen(null);
            mc.thePlayer.sendChatMessage(GameMode.values()[selectedMode].getCommand());
        }
    }

    @Override
    public void handleKeyboardInput() {
        super.handleKeyboardInput();
        if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(62) && !keyDown) {
            keyDown = true;
            selectedMode = (selectedMode + 1) > 2 ? 0 : selectedMode + 1;
        } else {
            keyDown = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        {
            mc.getTextureManager()
                .bindTexture(BG_TEXTURE);
            drawTexture(left, top, 0, 0, 125, 75, 128, 128);

            for (ModeButton button : buttons) {
                button.drawButton(mouseX, mouseY);
            }
            GL11.glEnable(GL11.GL_BLEND);

            mc.getTextureManager()
                .bindTexture(BG_TEXTURE);
            drawTexture(getRenderX(selectedMode), top + 28, 26, 75, 26, 26, 128, 128);
        }
        GL11.glDisable(GL11.GL_BLEND);

        drawCenteredString(
            fontRendererObj,
            GameMode.values()[selectedMode].getDisplayName(),
            left + (BG_WIDTH >> 1),
            top + 7,
            0xFFFFFF);
        drawCenteredString(fontRendererObj, I18n.format("gameMode.switch"), left + (BG_WIDTH >> 1), top + 64, 0xFFFFFF);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    private void drawTexture(int x, int y, float u, float v, int w, int h, float tw, float th) {
        func_146110_a(x, y, u, v, w, h, tw, th);
    }

    private void addVertexWithUV(double x, double y, double u, double v) {
        Tessellator.instance.addVertexWithUV(x, y, this.zLevel, u, v);
    }

    // itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), stack, x, y);
    // itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), stack, x, y - (this.draggedStack == null ?
    // 0 : 8), altText);
    protected void drawItem(ItemStack stack, int x, int y) {
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y);
        itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y, true);
        // itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), stack, x, y);
        GL11.glDisable(GL11.GL_LIGHTING);
    }
}
