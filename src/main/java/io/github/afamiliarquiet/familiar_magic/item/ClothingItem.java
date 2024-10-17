package io.github.afamiliarquiet.familiar_magic.item;

import com.google.common.base.Suppliers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClothingItem extends Item implements Equipable {
    // can't just make an armor item, nooo, because that's an armor and it has trims and is rendered by the armor layer.
    // no no, can't have strange creatures on the internet adding their own shapes to armor items. would hate for that.
    // WELL TOO BAD AHAHAHA

    protected final ArmorItem.Type type;
    protected final Holder<ArmorMaterial> material;
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public ClothingItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        super(properties);
        this.material = material;
        this.type = type;
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        this.defaultModifiers = Suppliers.memoize(
                () -> new ItemAttributeModifiers(
                        List.of(new ItemAttributeModifiers.Entry(Attributes.ARMOR,
                                new AttributeModifier(
                                        ResourceLocation.withDefaultNamespace("armor." + type.getName()),
                                        material.value().getDefense(type),
                                        AttributeModifier.Operation.ADD_VALUE
                                ),
                                EquipmentSlotGroup.bySlot(type.getSlot())
                        )),
                        true
                )
        );
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.material.value().enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return this.material.value().repairIngredient().get().test(repairCandidate);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return this.defaultModifiers.get();
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    @Override
    public Holder<SoundEvent> getEquipSound() {
        return this.material.value().equipSound();
    }
}
