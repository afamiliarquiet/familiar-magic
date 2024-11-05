package io.github.afamiliarquiet.familiar_magic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PlaceCandlesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("placecandles")
                .requires(commandSourceStack -> commandSourceStack. hasPermission(2))
                .then(
                        argument("target", EntityArgument.entity())
                                .then(
                                        argument("pos", BlockPosArgument.blockPos())
                                                .executes(
                                                        context ->
                                                            placeCandles(
                                                                    context.getSource(),
                                                                    EntityArgument.getEntity(context, "target"),
                                                                    context.getSource().getLevel(),
                                                                    BlockPosArgument.getBlockPos(context, "pos"),
                                                                    true
                                                            )
                                                )
                                                .then(
                                                        argument("lit", BoolArgumentType.bool())
                                                                .executes(
                                                                        context -> placeCandles(
                                                                                context.getSource(),
                                                                                EntityArgument.getEntity(context, "target"),
                                                                                context.getSource().getLevel(),
                                                                                BlockPosArgument.getBlockPos(context, "pos"),
                                                                                BoolArgumentType.getBool(context, "lit")
                                                                        )
                                                                )
                                                )
                                )
                )
        );
    }

    private static int placeCandles(CommandSourceStack source, Entity target, ServerLevel level, BlockPos pos, boolean lit) {
        SummoningTableBlockEntity.superburn(level, pos, target.getUUID(), lit);
        source.sendSuccess(
                () -> Component.translatable(
                        "commands.familiar_magic.success",
                        target.getDisplayName(),
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                ),
                true
        );
        return 1;
    }
}
