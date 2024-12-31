package me.client.module.combat;

import me.client.Dark;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import io.netty.util.internal.ThreadLocalRandom;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoClicker extends Module {
    Minecraft mc = Minecraft.getMinecraft();
    private long lastClick;
    private long hold;

    private double speed;
    private double holdLength;
    private int min;
    private int max;
    private boolean swordActivado;

    public AutoClicker() {
        super("AutoClicker", "Clickea Por Ti y Tiene Problemas de Compatibilidad", Category.COMBAT);

        Dark.instance.settingsManager.rSetting(new Setting("LMinCPS", this, 8, 1, 25, true));
        Dark.instance.settingsManager.rSetting(new Setting("LMaxCPS", this, 12, 1, 25, true));
        Dark.instance.settingsManager.rSetting(new Setting("OnlySword", this, false));
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if (Dark.instance.destructed) {
            return;
        }

        if (mc.thePlayer != null && mc.currentScreen == null) {
            boolean onlySwordConfig = Dark.instance.settingsManager.getSettingByName(this, "OnlySword").getValBoolean();

            if (onlySwordConfig != swordActivado) {
                swordActivado = onlySwordConfig;
            }

            if (!swordActivado || isSwordInHand()) {
                try {
                    if (Mouse.isButtonDown(0)) {
                        if (System.currentTimeMillis() - lastClick > speed * 1000) {
                            lastClick = System.currentTimeMillis();
                            if (hold < lastClick) {
                                hold = lastClick;
                            }
                            int key = mc.gameSettings.keyBindAttack.getKeyCode();
                            KeyBinding.setKeyBindState(key, true);
                            KeyBinding.onTick(key);
                            this.updateVals();
                        } else if (System.currentTimeMillis() - hold > holdLength * 1000) {
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                            this.updateVals();
                        }
                    } else {
                        // Si el botón no está presionado, asegurarse de que se libere el estado
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.updateVals();
    }

    private void updateVals() {
        min = (int) Dark.instance.settingsManager.getSettingByName(this, "LMinCPS (Low Recomended)").getValDouble();
        max = (int) Dark.instance.settingsManager.getSettingByName(this, "LMaxCPS (Low Recomended)").getValDouble();

        // Validar los valores min y max
        if (min >= max) {
            max = min + 1; // Asegurarse de que max sea mayor que min
        }

        // Calcular velocidad y tiempo de espera
        speed = 1.0 / ThreadLocalRandom.current().nextDouble(min - 0.2, max);
        holdLength = speed / ThreadLocalRandom.current().nextDouble(min, max);
    }

    private boolean isSwordInHand() {
        if (mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
}