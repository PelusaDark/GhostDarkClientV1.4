package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class AimAssist extends Module {

    private NumberValue speedValue;
    private NumberValue fovValue;
    private NumberValue distanceValue;

    private Minecraft mc;
    private int tru;
    private boolean tre = true;
    private boolean activadoTT;
    private boolean swordActivado;
    private boolean clickactivado;
    /*Recuerda Dar Creditos a PelusaDark*/
    public AimAssist() {
        super("AimAssist", "Ayuda a Apuntar Mejor", Category.COMBAT);  
        this.mc = Minecraft.getMinecraft();

        this.speedValue = new NumberValue("Speed", 45.0, 10.0, 50.0);
        this.fovValue = new NumberValue("FOV", 55.0, 0.0, 180.0);
        this.distanceValue = new NumberValue("Distance", 4.3, 1.0, 10.0);

        Dark.instance.settingsManager.rSetting(new Setting("Speed", this, 10.0, 10.0, 50.0, false));
        Dark.instance.settingsManager.rSetting(new Setting("FOV", this, 90, 10.0, 360.0, false));
        Dark.instance.settingsManager.rSetting(new Setting("Distance", this, 4.3, 1.0, 10.0, false));
        Dark.instance.settingsManager.rSetting(new Setting("Teamscheck", this, false));
        Dark.instance.settingsManager.rSetting(new Setting("OnlySworld", this, false));
        Dark.instance.settingsManager.rSetting(new Setting("OnlyClick", this, false));
    }

    public double entityPosCompare(final Entity ent) {
        return ((this.mc.thePlayer.rotationYaw - rotationUntilTarget(ent)) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    private float rotationUntilTarget(Entity ent) {
        double diffX = ent.posX - this.mc.thePlayer.posX;
        double diffZ = ent.posZ - this.mc.thePlayer.posZ;
        return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
    }

    public boolean isFovLargeEnough(final Entity en, float a) {
        a *= 0.5;
        final double v = ((this.mc.thePlayer.rotationYaw - rotationUntilTarget(en)) % 360.0 + 540.0) % 360.0 - 180.0;
        return (v > 0.0 && v < a) || (-a < v && v < 0.0);
    }

    @SubscribeEvent
    public void tickEvent(final TickEvent.RenderTickEvent event) {
        float speed = (float) Dark.instance.settingsManager.getSettingByName(this, "Speed").getValDouble();
        float fov = (float) Dark.instance.settingsManager.getSettingByName(this, "FOV").getValDouble();
        float distance = (float) Dark.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();
        boolean onlySworldConfig = Dark.instance.settingsManager.getSettingByName(this, "OnlySworld").getValBoolean();
        boolean teamsActivated = Dark.instance.settingsManager.getSettingByName(this, "Teamscheck").getValBoolean();
        boolean clickonlyoption = Dark.instance.settingsManager.getSettingByName(this, "OnlyClick").getValBoolean();

        if (teamsActivated != activadoTT) {
            activadoTT = teamsActivated;
        }
        if (onlySworldConfig != swordActivado) {
            swordActivado = onlySworldConfig;
        }
        if (clickonlyoption != clickactivado) {
            clickactivado = clickonlyoption;
        }

        if ((!swordActivado || isSwordInHand()) && (!clickactivado || Mouse.isButtonDown(0))) {
            if (this.mc.thePlayer != null && this.tre && mc.currentScreen == null) {
                if (this.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    this.tre = true;
                }
                if (this.tre) {
                    ++this.tru;
                }

                if (this.tru >= 200) {
                    this.tru = 0;
                    return;
                }
                final Entity h = this.ent();
                if (h != null) {
                    final float distanceTo = h.getDistanceToEntity(this.mc.thePlayer);
                    if (entityPosCompare(h) > 1.0 || entityPosCompare(h) < -1.0) {
                        final boolean i = entityPosCompare(h) > 0.0;
                        this.mc.thePlayer.setAngles((float) (i ? (-(Math.abs(entityPosCompare(h)) * (speed / 50.0))) : (Math.abs(entityPosCompare(h)) * speed / 50.0)), 0.0f);
                    }
                }
            }
        }
    }

    private Entity ent() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;
        float fov = (float) Dark.instance.settingsManager.getSettingByName(this, "FOV").getValDouble();

        for (Object obj : this.mc.theWorld.loadedEntityList) {
            if (obj instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) obj;

                if (entity.equals(this.mc.thePlayer)) {
                    continue;
                }

                if (Dark.instance.moduleManager.getModule("AntiBot").isToggled() && AntiBot.bots.contains(entity)) {
                    continue;
                }

                double distance = this.mc.thePlayer.getDistanceToEntity(entity);
                if (distance < this.distanceValue.getValue() && isFovLargeEnough(entity, fov)) {
                    if (isSameTeam(entity) && activadoTT) {
                        continue;
                    }
                    if (distance < closestDistance) {
                        closestEntity = entity;
                        closestDistance = distance;
                    }
                }
            }
        }
        return closestEntity;
    }

    private boolean isSwordInHand() {
        if (mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean isSameTeam(Entity entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer otherPlayer = (EntityPlayer) entity;
        ScorePlayerTeam otherTeam = (ScorePlayerTeam) otherPlayer.getTeam();
        ScorePlayerTeam myTeam = (ScorePlayerTeam) this.mc.thePlayer.getTeam();

        return myTeam != null && otherTeam != null && myTeam.equals(otherTeam);
    }

    public static class NumberValue {
        private String name;
        private double value;
        private double min;
        private double max;

        public NumberValue(String name, double value, double min, double max) {
            this.name = name;
            this.value = value;
            this.min = min;
            this.max = max;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(double value) {
            if (value < this.min) {
                this.value = this.min;
            } else if (value > this.max) {
                this.value = this.max;
            } else {
                this.value = value;
            }
        }
    }
}
