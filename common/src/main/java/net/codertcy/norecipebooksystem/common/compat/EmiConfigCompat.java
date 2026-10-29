package net.codertcy.norecipebooksystem.common.compat;

import dev.emi.emi.com.unascribed.qdcss.QDCSS;
import dev.emi.emi.config.ConfigEnum;
import dev.emi.emi.config.EmiConfig;

import java.util.function.Predicate;

/** Loaded only by the optional EMI mixin. */
public final class EmiConfigCompat {
    private EmiConfigCompat() {}

    public static void registerFilter() {
        EmiConfig.FILTERS.put("ui.recipe-book-action",
                (Predicate<ConfigEnum>) value -> value == null || !"default".equals(value.getName()));
    }

    public static boolean shouldSkipAssignment(QDCSS css, String key) {
        return "ui.recipe-book-action".equals(key) && "default".equals(css.get(key).get());
    }
}
