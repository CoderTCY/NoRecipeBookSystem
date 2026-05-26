package net.codertcy.norecipebooksystem.common.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 在 REI 配置屏幕初始化时，从 category 列表中移除
 * "Vanilla Recipe Book" 配置选项（ID: accessibility.vanilla_recipe_book），
 * 使用户无法在 REI 设置界面中看到/操作此选项。
 * REI 是可选依赖，仅在 REI 加载时生效。
 */
@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.config.REIConfigScreen", priority = 2000)
public abstract class REIConfigScreenMixin {

    private static final String TARGET_OPTION_ID = "accessibility.vanilla_recipe_book";
    private static final String OPTION_GROUP_CLASS = "me.shedaniel.rei.impl.client.gui.config.options.OptionGroup";
    private static final String COMPOSITE_OPTION_CLASS = "me.shedaniel.rei.impl.client.gui.config.options.CompositeOption";

    /**
     * 在构造函数执行前拦截 categories 参数，
     * 遍历所有 category -> group -> option，
     * 移除 ID 为 "accessibility.vanilla_recipe_book" 的选项。
     * <p>
     * 之所以在 HEAD 处修改，是因为构造函数随后会调用
     * {@code CollectionUtils.map(categories, OptionCategory::copy)}，
     * 此时原始 categories 中的内容已被清理，copy 出来的新对象自然也不含此选项。
     */
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/client/gui/screens/Screen;Ljava/util/List;)V",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private static List<?> modifyCategoriesParameter(List<?> categories) {
        if (categories == null) {
            return null;
        }
        try {
            // 缓存反射方法，避免重复查找
            Method groupGetOptions = findMethod(OPTION_GROUP_CLASS, "getOptions");
            Method optionGetId = findMethod(COMPOSITE_OPTION_CLASS, "getId");

            for (Object category : categories) {
                List<?> groups = (List<?>) callMethod(category, "getGroups");
                if (groups == null) continue;

                for (Object group : groups) {
                    if (group == null) continue;
                    List<?> options = (List<?>) groupGetOptions.invoke(group);
                    if (options == null) continue;

                    options.removeIf(option -> {
                        if (option == null) return false;
                        try {
                            String id = (String) optionGetId.invoke(option);
                            return TARGET_OPTION_ID.equals(id);
                        } catch (Exception e) {
                            return false;
                        }
                    });
                }
            }
        } catch (Exception ignored) {
            // 反射失败时静默忽略，不影响原版功能
        }
        return categories;
    }

    private static Method findMethod(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getMethod(methodName);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object callMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
