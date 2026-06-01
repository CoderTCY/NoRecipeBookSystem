package net.codertcy.norecipebooksystem.common.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Ensures EMI's {@code recipeBookAction} is never set to {@code DEFAULT}.
 *
 * <p>EMI loads its config from a CSS-like file.  The config parser reads a string
 * value and converts it to an enum constant via {@code ConfigEnum} setter.
 * When the value is {@code "default"}, it matches {@code RecipeBookAction.DEFAULT},
 * which tells EMI to leave the recipe book alone — exactly what we don't want.
 *
 * <p>This mixin applies two countermeasures inside {@code EmiConfig.<clinit>}:
 * <ol>
 *   <li><b>Setter proxy:</b>  Replaces the {@code ConfigEnum} entry in
 *       {@code EmiConfig.SETTERS} with a JDK dynamic proxy.  When the setter is
 *       called for {@code recipeBookAction} and the CSS value is {@code "default"},
 *       the proxy skips the field write, so the field keeps its Java default
 *       of {@code RecipeBookAction.TOGGLE_CRAFTABLES}.</li>
 *   <li><b>UI filter:</b>  Registers a {@code Predicate} in {@code EmiConfig.FILTERS}
 *       under key {@code "ui.recipe-book-action"} that excludes the enum constant
 *       named {@code "default"} from the selection list in EMI's config screen.</li>
 * </ol>
 *
 * <p>EMI is optional — this mixin only activates when EMI is present.
 */
@Pseudo
@Mixin(targets = "dev.emi.emi.config.EmiConfig", priority = 2000)
public abstract class EMIConfigMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private EMIConfigMixin() {}

    private static boolean patched = false;

    @Inject(method = "<clinit>", at = @At("RETURN"), remap = false)
    private static void onClinit(CallbackInfo info) {
        if (patched) return;
        try {
            Class<?> configClass = Class.forName("dev.emi.emi.config.EmiConfig");

            // ===== 1) 替换 ConfigEnum setter 以拦截 "default" 值 =====
            Field settersField = configClass.getDeclaredField("SETTERS");
            settersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Class<?>, Object> setters = (Map<Class<?>, Object>) settersField.get(null);

            Class<?> configEnumClass = Class.forName("dev.emi.emi.config.ConfigEnum");
            Object originalSetter = setters.get(configEnumClass);
            if (originalSetter != null) {
                Class<?> setterIface = Class.forName("dev.emi.emi.config.EmiConfig$Setter");
                Object wrappedSetter = Proxy.newProxyInstance(
                        setterIface.getClassLoader(),
                        new Class<?>[]{setterIface},
                        (obj, method, args) -> {
                            if ("setValue".equals(method.getName())) {
                                Field field = (Field) args[2];
                                if ("recipeBookAction".equals(field.getName())) {
                                    // 通过反射访问 CSS 条目值，避免对 QDCSS 的编译期依赖
                                    Object css = args[0];
                                    String annot = (String) args[1];
                                    Method cssGet = css.getClass().getMethod("get", String.class);
                                    Object entry = cssGet.invoke(css, annot);
                                    Method entryGet = entry.getClass().getMethod("get");
                                    String value = (String) entryGet.invoke(entry);

                                    if ("default".equals(value)) {
                                        // 跳过写入——字段保持 Java 默认值（TOGGLE_CRAFTABLES）
                                        return null;
                                    }
                                }
                            }
                            return method.invoke(originalSetter, args);
                        });
                setters.put(configEnumClass, wrappedSetter);
            }

            // ===== 2) 注册 UI 过滤器，从枚举选择列表中排除 DEFAULT =====
            Field filtersField = configClass.getField("FILTERS");
            @SuppressWarnings("unchecked")
            Map<String, Predicate<?>> filters = (Map<String, Predicate<?>>) filtersField.get(null);
            filters.put("ui.recipe-book-action", (Predicate<Object>) v -> {
                try {
                    Method getName = v.getClass().getMethod("getName");
                    String name = (String) getName.invoke(v);
                    return !"default".equals(name);
                } catch (Exception e) {
                    return true;
                }
            });

            patched = true;
        } catch (Exception ignored) {
            // 静默忽略——EMI 可能不存在或内部结构已变更
        }
    }
}
