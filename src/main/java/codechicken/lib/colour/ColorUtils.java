package codechicken.lib.colour;

import com.gtnewhorizon.gtnhlib.color.ColorResource;

public class ColorUtils {

    private static final ColorResource.Factory color = new ColorResource.Factory("codechickencore");

    public static final ColorResource
    // spotless:off

        textFieldText           = color.rgb("textFieldText",            "0xE0E0E0"),
        textFieldTextDisabled   = color.rgb("textFieldTextDisabled",    "0x707070"),
        itemQuantityText        = color.rgb("itemQuantityText",         "0xFFFFFF"),
        modDescriptionText      = color.rgb("modDescriptionText",       "0xDDDDDD"),

        buttonTextDisabled      = color.argb("buttonTextDisabled",      "0xFFA0A0A0"),
        buttonTextHover         = color.argb("buttonTextHover",         "0xFFFFFFA0"),
        buttonText              = color.argb("buttonText",              "0xFFE0E0E0"),
        textFieldBorder         = color.argb("textFieldBorder",         "0xFFA0A0A0"),
        textFieldBackground     = color.argb("textFieldBackground",     "0xFF000000"),
        scrollPaneBackground    = color.argb("background",              "0xFF000000"),
        scrollPaneOverlayTop    = color.argb("overlayTop",              "0xFFA0A0A0"),
        scrollPaneOverlayBottom = color.argb("overlayBottom",           "0xFFA0A0A0"),
        scrollPaneOverlayLeft   = color.argb("overlayLeft",             "0xFFA0A0A0"),
        scrollPaneOverlayRight  = color.argb("overlayRight",            "0xFFA0A0A0"),
        scrollbarCorners        = color.argb("scrollbarCorners",        "0xFF8B8B8B"),
        scrollbarTopLeft        = color.argb("scrollbarTopLeft",        "0xFFF0F0F0"),
        scrollbarBottomRight    = color.argb("scrollbarBottomRight",    "0xFF555555"),
        scrollbarFill           = color.argb("scrollbarFill",           "0xFFC6C6C6"),
        scrollbarGuide          = color.argb("scrollbarGuide",          "0xFF808080"),
        tooltipBgStart          = color.argb("tooltipBgStart",          "0xF0100010"),
        tooltipBgEnd            = color.argb("tooltipBgEnd",            "0xF0100010"),
        tooltipBorderStart      = color.argb("tooltipBorderStart",      "0x505000FF"),
        tooltipBorderEnd        = color.argb("tooltipBorderEnd",        "0x5028007F"),
        tooltipText             = color.argb("tooltipText",             "0xFFFFFFFF");
    // spotless:on
}
