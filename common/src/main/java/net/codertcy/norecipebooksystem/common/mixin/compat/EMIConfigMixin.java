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
 * 确保 EMI 的 recipeBookAction 始终为非 DEFAULT 值：
 * <ol>
 *   <li>用动态代理替换 {@code EmiConfig.SETTERS} 中的 {@code ConfigEnum} 类型读取器，
 *      让 CSS 值 "default" 永远无法匹配到任何枚举常量——跳过赋值，字段保留 Java 默认值
 *      即 {@code RecipeBookAction.TOGGLE_CRAFTABLES}。</li>
 *   <li>注册 UI 过滤器，从枚举选择列表中排除 DEFAULT 选项。</li>
 * </ol>
 * EMI 是可选依赖，仅在 EMI 加载时生效。
 */
@Pseudo
@Mixin(targets = "dev.emi.emi.config.EmiConfig", priority = 2000)
public abstract class EMIConfigMixin {

    private static boolean patched = false;

    @Inject(method = "<clinit>", at = @At("RETURN"), remap = false)
    private static void onClinit(CallbackInfo info) {
        if (patched) return;
        try {
            Class<?> configClass = Class.forName("dev.emi.emi.config.EmiConfig");

            // ===== 1) 替换 ConfigEnum setter：拦截 "default" 值 =====
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
                                    // 读取 CSS 值；通过反射避免 QDCSS 编译期依赖
                                    Object css = args[0];
                                    String annot = (String) args[1];
                                    Method cssGet = css.getClass().getMethod("get", String.class);
                                    Object entry = cssGet.invoke(css, annot);
                                    Method entryGet = entry.getClass().getMethod("get");
                                    String value = (String) entryGet.invoke(entry);

                                    if ("default".equals(value)) {
                                        // 跳过赋值，字段保持 Java 默认值 TOGGLE_CRAFTABLES
                                        return null;
                                    }
                                }
                            }
                            return method.invoke(originalSetter, args);
                        });
                setters.put(configEnumClass, wrappedSetter);
            }

            // ===== 2) 注册 UI 过滤器：枚举选择页不显示 DEFAULT =====
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
            // EMI 不存在或结构变化时静默跳过
        }
    }
}
