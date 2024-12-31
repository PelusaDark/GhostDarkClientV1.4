package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OneHitSelect extends Module {
    private boolean wasHurt = false;

    public OneHitSelect() {
        super("OneHitSelect", "Realiza un ataque al golpear haciendo que toques el suelo primero", Category.COMBAT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
       
        if (Minecraft.getMinecraft().thePlayer != null && 
            Minecraft.getMinecraft().currentScreen == null && 
            event.player == Minecraft.getMinecraft().thePlayer) {
            
            if (event.player.hurtTime > 0 && !wasHurt) {
                wasHurt = true; 
             
                    KeyBinding keyBindAttack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
                    final int keyCode = keyBindAttack.getKeyCode();

                    KeyBinding.setKeyBindState(keyCode, true); // Simula que presiona el botón de ataque
                    KeyBinding.onTick(keyCode); // Llama al método de actualización de la tecla
                    KeyBinding.setKeyBindState(keyCode, false); // Simula que suelta el botón de ataque
                    
            } else if (event.player.hurtTime <= 0) {
                wasHurt = false; // Reinicia el estado al dejar de recibir daño
            }
        }
    }
}
