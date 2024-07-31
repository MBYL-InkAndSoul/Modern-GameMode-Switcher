package ins.gms.mixins;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.world.WorldSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface PlayerControllerMPAccessor {

    @Accessor("currentGameType")
    WorldSettings.GameType currentGameType();
}
