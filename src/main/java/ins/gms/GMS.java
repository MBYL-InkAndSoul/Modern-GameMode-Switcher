package ins.gms;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid = GMS.MODID, version = Tags.VERSION, name = "GameModeSwitcher", acceptedMinecraftVersions = "[1.7.10]")
public class GMS {

    public static final String MODID = "gms";
    private static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance
    public static GMS instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide()
            .isClient()) {
            GMS_Config._readConfig();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide()
            .isClient()) {
            GMS_Config._readConfig();
        }
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static boolean keyPressed = false;

    @SubscribeEvent
    public void keyInput(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getMinecraft();

            if (Keyboard.isKeyDown(61) && !keyPressed) {
                keyPressed = true;
                mc.gameSettings.showDebugInfo = false;
            } else if (!Keyboard.isKeyDown(61)) {
                keyPressed = false;
            }

            if (mc.currentScreen == null) {
                if (Keyboard.isKeyDown(61)) {
                    if (Keyboard.isKeyDown(62)) {
                        Field gameMode = null;
                        try {
                            // bje
                            gameMode = mc.playerController.getClass()
                                .getField("currentGameType");
                        } catch (NoSuchFieldException e) {
                            try {
                                gameMode = mc.playerController.getClass()
                                    .getField("field_78779_k");
                            } catch (NoSuchFieldException ex) {
                                throw new RuntimeException(ex);
                            }
                        } finally {
                            // Java 8
                            // noinspection deprecation
                            if (!gameMode.isAccessible()) {
                                gameMode.setAccessible(true);
                            }
                            try {
                                mc.displayGuiScreen(new GMS_Screen(
                                    mc,
                                    (WorldSettings.GameType) (gameMode.get(mc.playerController))));
                            } catch (IllegalAccessException e) {
                                LOG.error("Failed to access currentGameType field", e);
                            }
                            // mc.thePlayer.openGui(GMS_Main.instance, 0, mc.theWorld, mc.thePlayer.chunkCoordX,
                            // mc.thePlayer.chunkCoordY, mc.thePlayer.chunkCoordZ);
                        }

                    } else if (Keyboard.isKeyDown(71)) {
                        // mc.gameSettings.showDebugInfo = !mc.gameSettings.showDebugInfo;
                    }
                }
            }
        }
    }
}
