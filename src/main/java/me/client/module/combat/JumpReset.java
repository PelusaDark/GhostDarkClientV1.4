package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static me.client.module.util.utilities.MathUtil.random;

public class JumpReset extends Module {

    private boolean wasHurt;
    Minecraft mc = Minecraft.getMinecraft();
    public JumpReset() {
        super("JumpReset", "Reduce el knockback de manera legítima", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Chanse", this, 0.5, 0.1, 1.0, false));
        // Dark.instance.settingsManager.rSetting(new Setting("OnlyForWard", this, true));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        
        if (mc.thePlayer != null && mc.currentScreen == null && event.player == mc.thePlayer && mc.thePlayer.onGround) {
            double chance = Dark.instance.settingsManager.getSettingByName(this, "Chanse").getValDouble();

            if (event.player.hurtTime > 0 && !wasHurt) {
                wasHurt = true;
                if (random.nextDouble() <= chance) {
                    mc.thePlayer.jump();
                }
            } else if (event.player.hurtTime <= 0) {
                wasHurt = false; // Reinicia el estado al dejar de recibir daño
            }
        }
    }
}
