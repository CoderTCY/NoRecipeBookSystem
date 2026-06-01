package net.codertcy.norecipebooksystem.common.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Strips the "Vanilla Recipe Book" option ({@code accessibility.vanilla_recipe_book})
 * from REI's main config screen at construction time.
 *
 * <p>This mixin intercepts the {@code categories} parameter passed to
 * {@code REIConfigScreen(Screen, List)} and walks the tree
 * {@code category → group → option}, removing the
 * {@code CompositeOption} whose ID equals {@code accessibility.vanilla_recipe_book}.
 *
 * <p>The interception happens at {@code @At("HEAD")} so that
 * {@code CollectionUtils.map(categories, OptionCategory::copy)} inside the
 * constructor naturally produces copied trees that already exclude the option.
 *
 * <p>REI is optional — this mixin only activates when REI is present.
 *
 * @see REIConfigObjectMixin Overrides the getter at the API level
 * @see REISubMenuMixin    Removes the toggle from the gear icon quick menu
 */
@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.gui.config.REIConfigScreen", priority = 2000)
public abstract class REIConfigScreenMixin {
    /** 禁止实例化；此类仅供 Mixin 注入使用。 */
    private REIConfigScreenMixin() {}

    @Unique
    private static final String TARGET_OPTION_ID = "accessibility.vanilla_recipe_book";
    @Unique
    private static final String OPTION_GROUP_CLASS = "me.shedaniel.rei.impl.client.gui.config.options.OptionGroup";
    @Unique
    private static final String COMPOSITE_OPTION_CLASS = "me.shedaniel.rei.impl.client.gui.config.options.CompositeOption";

    /**
     * 在构造函数入口拦截 {@code categories} 参数，
     * 遍历 {@code category → group → option} 树并移除与
     * {@link #TARGET_OPTION_ID} 匹配的条目。
     *
     * <p>选择在 {@code @At("HEAD")} 处修改，因为构造函数内部会立即调用
     * {@code CollectionUtils.map(categories, OptionCategory::copy)}，
     * 确保拷贝后的树已经排除了该选项。
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
            // 反射静默失败——REI 可能不存在或内部结构已变更
        }
        return categories;
    }

    @Unique
    private static Method findMethod(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getMethod(methodName);
        } catch (Exception e) {
            return null;
        }
    }

    @Unique
    private static Object callMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
