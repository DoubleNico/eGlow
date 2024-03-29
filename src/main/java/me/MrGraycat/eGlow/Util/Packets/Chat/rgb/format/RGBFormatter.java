package me.MrGraycat.eGlow.Util.Packets.Chat.rgb.format;

/**
 * Abstract class for different RGB patterns
 */
public interface RGBFormatter {

    /**
     * Reformats RGB codes in provided text into #RRGGBB format
     *
     * @param   text
     *          text to format
     * @return  reformatted text
     */
    String reformat(String text);
}