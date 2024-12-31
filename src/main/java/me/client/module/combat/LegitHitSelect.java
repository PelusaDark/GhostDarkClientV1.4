package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.client.Minecraft;
import me.client.settings.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class LegitHitSelect extends Module {
    Minecraft mc = Minecraft.getMinecraft();
    private boolean wasHurt = false;
    private long hurtTime = 0;
    //private static final long ATTACK_DELAY = 100;
    private long DELAY;
    private boolean hasAttacked = false;

    public LegitHitSelect() {
        super("LegitHitSelect", "Igual que el hitselect pero legit", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Delay",this,50,5,140,true));
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != Phase.START) return;
        DELAY = (int) Dark.instance.settingsManager.getSettingByName(this,"Delay").getValDouble();
        
        if (mc.thePlayer == null || mc.currentScreen != null || event.player != mc.thePlayer) return;

        if (event.player.hurtTime > 0 && !wasHurt) {
            wasHurt = true;
            hasAttacked = false;
            hurtTime = System.currentTimeMillis();
        } else if (wasHurt && !hasAttacked && System.currentTimeMillis() - hurtTime >= DELAY) {
            KeyBinding keyBindAttack = mc.gameSettings.keyBindAttack;
            int keyCode = keyBindAttack.getKeyCode();

            KeyBinding.setKeyBindState(keyCode, true);
            KeyBinding.onTick(keyCode);
            KeyBinding.setKeyBindState(keyCode, false);
            
            hasAttacked = true;
        } else if (event.player.hurtTime <= 0) {
            wasHurt = false;
            hasAttacked = false;
        }
    }


}
