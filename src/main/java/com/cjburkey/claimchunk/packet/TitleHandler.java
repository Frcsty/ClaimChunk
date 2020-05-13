package com.cjburkey.claimchunk.packet;

import com.cjburkey.claimchunk.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * Class to handle title packets using reflection. Hopefully version
 * independent.
 *
 * @author bramhaag
 */
public final class TitleHandler
{

    private static final String jsonFormat = "{\"text\":\"%s\",\"color\":\"%s\"}";

    /**
     * Sends a title to the player.
     *
     * @param player       The player to whom send the title.
     * @param text         The text to display.
     * @param fadeInTicks  The fade in time in ticks.
     * @param stayTicks    The stay time in ticks.
     * @param fadeOutTicks The fade out time in ticks.
     * @throws Exception Reflection error.
     */
    public static void showTitle(Player player, String text, int fadeInTicks, int stayTicks,
                                 int fadeOutTicks) throws Exception
    {
        if (player != null)
        {
            showTitle(player, text, fadeInTicks, stayTicks, fadeOutTicks, "TITLE");
        }
    }

    /**
     * Sends a subtitle to the player.
     *
     * @param player       The player to whom send the subtitle.
     * @param text         The text to display.
     * @param fadeInTicks  The fade in time in ticks.
     * @param stayTicks    The stay time in ticks.
     * @param fadeOutTicks The fade out time in ticks.
     * @throws Exception Reflection error.
     */
    public static void showSubTitle(Player player, String text, int fadeInTicks, int stayTicks,
                                    int fadeOutTicks) throws Exception
    {
        showTitle(player, text, fadeInTicks, stayTicks, fadeOutTicks, "SUBTITLE");
    }

    /**
     * Sends an actionbar title to the player.
     *
     * @param player       The player to whom send the actionbar title.
     * @param text         The text to display.
     * @param fadeInTicks  The fade in time in ticks.
     * @param stayTicks    The stay time in ticks.
     * @param fadeOutTicks The fade out time in ticks.
     * @throws Exception Reflection error.
     */
    public static void showActionbarTitle(Player player, String text, int fadeInTicks, int stayTicks,
                                          int fadeOutTicks) throws Exception
    {
        // This may fail if the server is running a version that doesn't support action bars
        // In such a case, unless the action was to clear the action bar, the message will be displyed in the subtitle slot
        //  and a message logged in the console.
        // This may not be necessary but I'm doing it anyway so deal with it
        try
        {
            showTitle(player, text, fadeInTicks, stayTicks, fadeOutTicks, "ACTIONBAR");
        }
        catch (Exception ignored)
        {
            if (!text.trim().isEmpty())
            {
                showSubTitle(player, text, fadeInTicks, stayTicks, fadeOutTicks);
                Utils.err("Error: This server is running a version that does not support actionbars. Please display the 'useActionBar' config option under the 'titles' section in the config file.");
            }
        }
    }

    // Some pretty volatile code here, but if it works? idc.
    private static void showTitle(Player player, String text, int fadeInTicks, int stayTicks,
                                  int fadeOutTicks, String show) throws Exception
    {
        Constructor<?> titleConstructor = PacketHandler.getNMSClass("PacketPlayOutTitle").getConstructor(
                PacketHandler.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
                PacketHandler.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        Object packet = titleConstructor.newInstance(
                PacketHandler.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(show).get(null),
                getTextComponent(text), fadeInTicks, stayTicks, fadeOutTicks);

        if (player instanceof Server) return;

        PacketHandler.sendPacket(player, packet);
    }

    private static Object getTextComponent(String rawText) throws Exception
    {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(Utils.color(rawText)));
        return PacketHandler.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
                .getMethod("a", String.class).invoke(null, ComponentSerializer.toString(component));
    }

}
