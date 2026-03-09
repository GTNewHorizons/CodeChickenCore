package codechicken.lib.colour;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.config.ConfigTag.IConfigType;
import codechicken.lib.math.MathHelper;
import codechicken.lib.util.Copyable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Colour implements Copyable<Colour> {

    public static final class LocalizedColours {

        private static final String KEY_PREFIX = "codechickencore.color.";

        public static int BUTTON_TEXT_DISABLED = 0xFFA0A0A0;
        public static int BUTTON_TEXT_HOVER = 0xFFFFFFA0;
        public static int BUTTON_TEXT = 0xFFE0E0E0;
        public static int TEXT_FIELD_BORDER = 0xFFA0A0A0;
        public static int TEXT_FIELD_BACKGROUND = 0xFF000000;
        public static int TEXT_FIELD_TEXT = 0xE0E0E0;
        public static int TEXT_FIELD_TEXT_DISABLED = 0x707070;
        public static int SCROLL_PANE_BACKGROUND = 0xFF000000;
        public static int SCROLL_PANE_OVERLAY_TOP = 0xFFA0A0A0;
        public static int SCROLL_PANE_OVERLAY_BOTTOM = 0xFFA0A0A0;
        public static int SCROLL_PANE_OVERLAY_LEFT = 0xFFA0A0A0;
        public static int SCROLL_PANE_OVERLAY_RIGHT = 0xFFA0A0A0;
        public static int SCROLLBAR_CORNERS = 0xFF8B8B8B;
        public static int SCROLLBAR_TOP_LEFT = 0xFFF0F0F0;
        public static int SCROLLBAR_BOTTOM_RIGHT = 0xFF555555;
        public static int SCROLLBAR_FILL = 0xFFC6C6C6;
        public static int SCROLLBAR_GUIDE = 0xFF808080;
        public static int TOOLTIP_BG_START = 0xF0100010;
        public static int TOOLTIP_BG_END = 0xF0100010;
        public static int TOOLTIP_BORDER_START = 0x505000FF;
        public static int TOOLTIP_BORDER_END = 0x5028007F;
        public static int TOOLTIP_TEXT = 0xFFFFFFFF;
        public static int ITEM_QUANTITY_TEXT = 0xFFFFFF;
        public static int MOD_DESCRIPTION_TEXT = 0xDDDDDD;

        private LocalizedColours() {}

        public static void reloadLocalizedColours() {
            BUTTON_TEXT_DISABLED = getLocalizedColor("buttonTextDisabled", BUTTON_TEXT_DISABLED);
            BUTTON_TEXT_HOVER = getLocalizedColor("buttonTextHover", BUTTON_TEXT_HOVER);
            BUTTON_TEXT = getLocalizedColor("buttonText", BUTTON_TEXT);
            TEXT_FIELD_BORDER = getLocalizedColor("textFieldBorder", TEXT_FIELD_BORDER);
            TEXT_FIELD_BACKGROUND = getLocalizedColor("textFieldBackground", TEXT_FIELD_BACKGROUND);
            TEXT_FIELD_TEXT = getLocalizedColor("textFieldText", TEXT_FIELD_TEXT);
            TEXT_FIELD_TEXT_DISABLED = getLocalizedColor("textFieldTextDisabled", TEXT_FIELD_TEXT_DISABLED);
            SCROLL_PANE_BACKGROUND = getLocalizedColor("background", SCROLL_PANE_BACKGROUND);
            SCROLL_PANE_OVERLAY_TOP = getLocalizedColor("overlayTop", SCROLL_PANE_OVERLAY_TOP);
            SCROLL_PANE_OVERLAY_BOTTOM = getLocalizedColor("overlayBottom", SCROLL_PANE_OVERLAY_BOTTOM);
            SCROLL_PANE_OVERLAY_LEFT = getLocalizedColor("overlayLeft", SCROLL_PANE_OVERLAY_LEFT);
            SCROLL_PANE_OVERLAY_RIGHT = getLocalizedColor("overlayRight", SCROLL_PANE_OVERLAY_RIGHT);
            SCROLLBAR_CORNERS = getLocalizedColor("scrollbarCorners", SCROLLBAR_CORNERS);
            SCROLLBAR_TOP_LEFT = getLocalizedColor("scrollbarTopLeft", SCROLLBAR_TOP_LEFT);
            SCROLLBAR_BOTTOM_RIGHT = getLocalizedColor("scrollbarBottomRight", SCROLLBAR_BOTTOM_RIGHT);
            SCROLLBAR_FILL = getLocalizedColor("scrollbarFill", SCROLLBAR_FILL);
            SCROLLBAR_GUIDE = getLocalizedColor("scrollbarGuide", SCROLLBAR_GUIDE);
            TOOLTIP_BG_START = getLocalizedColor("tooltipBgStart", TOOLTIP_BG_START);
            TOOLTIP_BG_END = getLocalizedColor("tooltipBgEnd", TOOLTIP_BG_END);
            TOOLTIP_BORDER_START = getLocalizedColor("tooltipBorderStart", TOOLTIP_BORDER_START);
            TOOLTIP_BORDER_END = getLocalizedColor("tooltipBorderEnd", TOOLTIP_BORDER_END);
            TOOLTIP_TEXT = getLocalizedColor("tooltipText", TOOLTIP_TEXT);
            ITEM_QUANTITY_TEXT = getLocalizedColor("itemQuantityText", ITEM_QUANTITY_TEXT);
            MOD_DESCRIPTION_TEXT = getLocalizedColor("modDescriptionText", MOD_DESCRIPTION_TEXT);
        }

        /**
         * Resolve a optional localized ARGB color from lang files. Expected value format: AARRGGBB (for example
         * FF555555).
         */
        public static int getLocalizedColor(String key, int defaultColor) {
            String fullKey = KEY_PREFIX + key;
            if (!StatCollector.canTranslate(fullKey)) {
                return defaultColor;
            }

            String raw = StatCollector.translateToLocal(fullKey);
            if (raw == null) {
                return defaultColor;
            }

            String hex = raw.trim();
            if (hex.startsWith("0x") || hex.startsWith("0X")) {
                hex = hex.substring(2);
            } else if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }

            try {
                return (int) Long.parseLong(hex, 16);
            } catch (NumberFormatException ignored) {
                return defaultColor;
            }
        }
    }

    public static IConfigType<Colour> configRGB = new IConfigType<Colour>() {

        @Override
        public String configValue(Colour entry) {
            String s = Long.toString(((long) entry.rgb()) << 32 >>> 32, 16);
            while (s.length() < 6) s = "0" + s;
            return "0x" + s.toUpperCase();
        }

        private final Pattern patternRGB = Pattern.compile("(\\d+),(\\d+),(\\d+)");

        @Override
        public Colour valueOf(String text) throws Exception {
            Matcher matcherRGB = patternRGB.matcher(text.replaceAll("\\s", ""));
            if (matcherRGB.matches()) return new ColourRGBA(
                    Integer.parseInt(matcherRGB.group(1)),
                    Integer.parseInt(matcherRGB.group(2)),
                    Integer.parseInt(matcherRGB.group(3)),
                    0xFF);

            int hex = (int) Long.parseLong(text.replace("0x", ""), 16);
            return new ColourRGBA(hex << 8 | 0xFF);
        }
    };

    public byte r;
    public byte g;
    public byte b;
    public byte a;

    public Colour(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
    }

    public Colour(Colour colour) {
        r = colour.r;
        g = colour.g;
        b = colour.b;
        a = colour.a;
    }

    @SideOnly(Side.CLIENT)
    public void glColour() {
        GL11.glColor4ub(r, g, b, a);
    }

    @SideOnly(Side.CLIENT)
    public void glColour(int a) {
        GL11.glColor4ub(r, g, b, (byte) a);
    }

    public abstract int pack();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[0x" + Integer.toHexString(pack()).toUpperCase() + "]";
    }

    public Colour add(Colour colour2) {
        a += colour2.a;
        r += colour2.r;
        g += colour2.g;
        b += colour2.b;
        return this;
    }

    public Colour sub(Colour colour2) {
        int ia = (a & 0xFF) - (colour2.a & 0xFF);
        int ir = (r & 0xFF) - (colour2.r & 0xFF);
        int ig = (g & 0xFF) - (colour2.g & 0xFF);
        int ib = (b & 0xFF) - (colour2.b & 0xFF);
        a = (byte) (ia < 0 ? 0 : ia);
        r = (byte) (ir < 0 ? 0 : ir);
        g = (byte) (ig < 0 ? 0 : ig);
        b = (byte) (ib < 0 ? 0 : ib);
        return this;
    }

    public Colour invert() {
        a = (byte) (0xFF - (a & 0xFF));
        r = (byte) (0xFF - (r & 0xFF));
        g = (byte) (0xFF - (g & 0xFF));
        b = (byte) (0xFF - (b & 0xFF));
        return this;
    }

    public Colour multiply(Colour colour2) {
        a = (byte) ((a & 0xFF) * ((colour2.a & 0xFF) / 255D));
        r = (byte) ((r & 0xFF) * ((colour2.r & 0xFF) / 255D));
        g = (byte) ((g & 0xFF) * ((colour2.g & 0xFF) / 255D));
        b = (byte) ((b & 0xFF) * ((colour2.b & 0xFF) / 255D));
        return this;
    }

    public Colour scale(double d) {
        a = (byte) ((a & 0xFF) * d);
        r = (byte) ((r & 0xFF) * d);
        g = (byte) ((g & 0xFF) * d);
        b = (byte) ((b & 0xFF) * d);
        return this;
    }

    public Colour interpolate(Colour colour2, double d) {
        return this.add(colour2.copy().sub(this).scale(d));
    }

    public Colour multiplyC(double d) {
        r = (byte) MathHelper.clip((r & 0xFF) * d, 0, 255);
        g = (byte) MathHelper.clip((g & 0xFF) * d, 0, 255);
        b = (byte) MathHelper.clip((b & 0xFF) * d, 0, 255);

        return this;
    }

    public abstract Colour copy();

    public int rgb() {
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public int argb() {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public int rgba() {
        return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | (a & 0xFF);
    }

    public Colour set(Colour colour) {
        r = colour.r;
        g = colour.g;
        b = colour.b;
        a = colour.a;
        return this;
    }

    public boolean equals(Colour colour) {
        return colour != null && rgba() == colour.rgba();
    }
}
