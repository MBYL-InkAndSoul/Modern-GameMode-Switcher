package ins.gms;

import net.minecraft.client.Minecraft;
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
import ins.gms.mixins.PlayerControllerMPAccessor;

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

            if (mc.currentScreen == null && mc.playerController != null) {
                if (Keyboard.isKeyDown(61)) {
                    if (Keyboard.isKeyDown(62)) {
                        mc.displayGuiScreen(
                            new GMS_Screen(mc, ((PlayerControllerMPAccessor) mc.playerController).currentGameType()));
                    } else if (Keyboard.isKeyDown(71)) {
                        // mc.gameSettings.showDebugInfo = !mc.gameSettings.showDebugInfo;
                    }
                }
            }
        }
    }
}
