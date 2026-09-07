/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodComponent;
import net.dries007.tfc.common.component.food.FoodDefinition;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.heat.HeatDefinition;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.accessor.PatchedDataComponentMapAccessor;

/**
 * This exists to class-load-isolate {@link TFCComponents} from methods called too early via {@link ItemStack},
 * and to delay them until past resource reload when we know TFC data will be accurate
 */
public final class ItemStackHooks
{
    static boolean ENABLED = false;

    /**
     * Modifies components attached to an item stack on the creation of the item stack. This is done as some components <strong>need</strong> a reference
     * to the owning object to know if it should attach or not. Note that these cannot be added as default components, as the default value would then
     * not be able to be item-stack-agnostic.
     * <p>
     * This mainly occurs from two places:
     * <ul>
     *     <li>When stacks are created from serialization - the components may or may not be present, but will be missing a stack reference. We update
     *     them in this case</li>
     *     <li>When the stack is copied - we just validate that the component is present, and then do no further modifications</li>
     * </ul>
     * Note: this is isolated to {@link TFCComponents} via class-load because it involves (1) querying component types potentially before they are
     * registered, and (2) querying about components before they are
     * @param stack A new item stack, freshly constructed
     */
    public static void onModifyItemStackComponents(ItemStack stack)
    {
        if (TFCComponents.HEAT.holder().isBound())
        {
            final @Nullable HeatComponent heat = stack.get(TFCComponents.HEAT);
            if (heat != null)
            {
                // A heat component already exists, but we might need to populate the heat definition
                // We delay actually querying the definition here, as it might be (1) already present, i.e. from a `copy()`,
                // or (2) a builtin component that has and needs no definition
                heat.capture(stack);
            }
            else
            {
                // No heat component exists, so query for a definition and if we find one, attach a component
                final @Nullable HeatDefinition def = HeatCapability.getDefinition(stack);
                if (def != null)
                {
                    stack.set(TFCComponents.HEAT, HeatComponent.of(def));
                }
            }
        }

        if (TFCComponents.FOOD.holder().isBound())
        {
            // Food components are similar to the above
            final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
            if (food != null)
            {
                // If there is an existing food capability, we capture the definition first. Note there are no 'builtin'
                // food components like heat has, and if needed, update it on create
                food.capture(stack);
            }
            else
            {
                // If not, we query for a definition and if we find one, attach a component
                final @Nullable FoodDefinition def = FoodCapability.getDefinition(stack);
                if (def != null)
                {
                    stack.set(TFCComponents.FOOD, new FoodComponent(def));
                }
            }
        }
    }

    /**
     * Components stored in a {@link PatchedDataComponentMap} may not have their
     * equality methods called by {@link PatchedDataComponentMap#equals},
     * so we must sanitize them first here.
     * @see HeatComponent#sanitize()
     * @see FoodComponent#sanitize()
     */
    public static void onCompareItemStackComponents(ItemStack stack, ItemStack other)
    {
        final DataComponentMap components = stack.getComponents();
        final DataComponentMap otherComponents = other.getComponents();

        if (TFCComponents.HEAT.holder().isBound())
        {
            final @Nullable HeatComponent heat = components.get(TFCComponents.HEAT.get());
            if (heat != null && components instanceof PatchedDataComponentMap patched)
            {
                patched.set(TFCComponents.HEAT.get(), heat.sanitize());
            }
            final @Nullable HeatComponent otherHeat = otherComponents.get(TFCComponents.HEAT.get());
            if (otherHeat != null && otherComponents instanceof PatchedDataComponentMap otherPatched)
            {
                otherPatched.set(TFCComponents.HEAT.get(), otherHeat.sanitize());
            }
        }
        if (TFCComponents.FOOD.holder().isBound())
        {
            final @Nullable FoodComponent food = components.get(TFCComponents.FOOD.get());
            if (food != null && components instanceof PatchedDataComponentMap patched)
            {
                patched.set(TFCComponents.FOOD.get(), food.sanitize());
            }
            final @Nullable FoodComponent otherFood = otherComponents.get(TFCComponents.FOOD.get());
            if (otherFood != null && otherComponents instanceof PatchedDataComponentMap otherPatched)
            {
                otherPatched.set(TFCComponents.FOOD.get(), otherFood.sanitize());
            }
        }
    }

    private static final ThreadLocal<Boolean> IN_FOOD_STACK_CHECK = ThreadLocal.withInitial(() -> false);

    /**
     * Check if foods with slightly different expiration dates should be able to be stacked
     * together anyways.
     * @return true if the food should stack, false to defer to vanilla logic
     */
    public static boolean shouldFoodStacksStack(ItemStack stack, ItemStack other)
    {
        // Prevent recursion
        if (IN_FOOD_STACK_CHECK.get())
        {
            return false;
        }

        if (TFCComponents.FOOD.holder().isBound())
        {
            final @Nullable FoodComponent food = stack.get(TFCComponents.FOOD);
            final @Nullable FoodComponent otherFood = other.get(TFCComponents.FOOD);
            if (food != null && otherFood != null)
            {
                try
                {
                    IN_FOOD_STACK_CHECK.set(true);
                    // This would call back into isSameItemSameComponents, so we need to prevent recursion
                    if (!FoodCapability.areStacksStackableExceptCreationDate(stack, other))
                    {
                        return false;
                    }
                }
                finally 
                {
                    IN_FOOD_STACK_CHECK.set(false);
                }

                final long creationDate = food.getCreationDate();
                final long otherCreationDate = otherFood.getCreationDate();

                if (creationDate == otherCreationDate)
                {
                    return true;
                }

                if (creationDate < 0 || otherCreationDate < 0)
                {
                    // If one of the creation dates is a special flag, do not stack
                    return false;
                }

                return Math.abs(creationDate - otherCreationDate) <= TFCConfig.SERVER.foodDecayStackTicks.get();
            }
        }
        return false;
    }

    /**
     * Modifies the attached components whenever an item stack is copied
     * <p>
     * Since we edit the default components of item stacks on the fly, we also want to ensure that the stack reflects the updated {@code prototype}
     * components of the item. This will work properly for newly created stacks, but stacks created through a {@link ItemStack#copy()} just copy the
     * prototype directly. So we have to (potentially) perform this modification.
     * @param stack The original stack - we do not modify this stack
     * @param map The patched components of the original stack
     */
    public static PatchedDataComponentMap onCopyItemStackComponents(ItemStack stack, PatchedDataComponentMap map)
    {
        final DataComponentMap prevPrototype = ((PatchedDataComponentMapAccessor) (Object) map).accessor$getPrototype();
        final DataComponentMap newPrototype = stack.getItem().components();
        if (prevPrototype == newPrototype)
        {
            // If both prototypes are the same, then we do nothing, just do the original copy of the map
            return map.copy();
        }

        // In the case both maps are not the same instance, that means the prototype has been updated. We then need to do a better check,
        // namely, if the patch is sanitized w.r.t the underlying map (no patches which are empty on top of underlying empty values,
        // or containing a value the same as the default)
        //
        // Fortunately, vanilla has a method that performs this validation, as fast as possible, and returns us a new map which is either
        // as fast plain copy, or a full copy with sanitized patch values. It also handles marking both maps as copyOnWrite=true
        return PatchedDataComponentMap.fromPatch(newPrototype, map.asPatch());
    }
}
