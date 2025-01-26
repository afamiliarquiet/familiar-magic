package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.item.material.ClothingMaterial;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

public class ClothingItem extends Item implements Equipment {
    // i really hope i don't have to use this soon but idk. it works enough

    protected final ArmorItem.Type type;
    protected final ArmorMaterial material = ClothingMaterial.INSTANCE;
    private final AttributeModifiersComponent defaultModifiers;

    public ClothingItem(ArmorItem.Type type, Settings settings) {
        super(settings);
        this.type = type;
        this.defaultModifiers = new AttributeModifiersComponent(
                List.of(new AttributeModifiersComponent.Entry(EntityAttributes.GENERIC_ARMOR,
                        new EntityAttributeModifier(
                                Identifier.ofVanilla("armor." + type.getName()),
                                material.getProtection(type),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.forEquipmentSlot(type.getEquipmentSlot())
                )),
                true
        );
    }

    @Override
    public int getEnchantability() {
        return this.material.enchantability();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.repairIngredient().get().test(ingredient);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    public AttributeModifiersComponent getDefaultModifiers() {
        return this.defaultModifiers;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return this.type.getEquipmentSlot();
    }

    @Override
    public RegistryEntry<SoundEvent> getEquipSound() {
        return this.material.equipSound();
    }
}
