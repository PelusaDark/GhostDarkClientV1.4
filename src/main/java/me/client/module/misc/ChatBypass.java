package me.client.module.misc;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.NetworkManager;

public class ChatBypass extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ChatBypass() {    
        super("ChatBypass", "Evita el filtro del chat", Category.MISC);
        //Dark.instance.settingsManager.rSetting(new Setting("Universocraft", this, true));
        //Dark.instance.settingsManager.rSetting(new Setting("Hycraft", this, true));
    }
    //Proxima update...
    @SubscribeEvent
    public void onPacketSend(FMLNetworkEvent.ClientCustomPacketEvent event) {
        if (!this.isToggled()) return;

        NetworkManager networkManager = event.manager;
        if (networkManager == null) return;

        ByteBuf buf = event.packet.payload();
        PacketBuffer packetBuffer = new PacketBuffer(buf);

        try {
            int packetId = packetBuffer.readVarIntFromBuffer();
            if (packetId == 0x01) { // ID for C01PacketChatMessage
                String message = packetBuffer.readStringFromBuffer(32767);

                if (!message.startsWith("/")) {
                    String modifiedMessage = "_" + message;
                    C01PacketChatMessage newPacket = new C01PacketChatMessage(modifiedMessage);
                    networkManager.sendPacket(newPacket);
                    event.setCanceled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

