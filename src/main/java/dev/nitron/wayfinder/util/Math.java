package dev.nitron.wayfinder.util;

import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Math {
    public static float getLookFactor(PlayerEntity player, BlockPos target, float maxAngle, float tickDelta){
        Vec3d eyePosition = player.getEyePos();
        Vec3d lookDirection = player.getRotationVec(tickDelta).normalize();
        Vec3d targetDir = Vec3d.ofCenter(target).subtract(eyePosition).normalize();
        double dot = lookDirection.dotProduct(targetDir);
        if (dot <= 0){
            return 0.0F; //return because its behind the player.
        }

        double maxAngleRad = java.lang.Math.toRadians(maxAngle);
        double minDot = java.lang.Math.cos(maxAngleRad);

        double factor = (dot - minDot) / (1.0F - minDot);
        float clamped = (float) MathHelper.clamp(factor, 0.0, 1.0);
        if (clamped > 0.95){
            return 1.0F;
        }
        return clamped;

        //IF I HAVE TO REWRITE THIS GOD DAMN METHOD AGAIN
        //THEN IM GOING TO STAB MYSELF IN THE EYES AND
        //EAT UP THE REMAINS!! IM GOING TO GO ON A FUCKING
        //RAMPAGE IF I HAVE TO DO THIS AGAIN. ALL OF IT. ALL OF IT.
        //THIS SHIT IS FUCKING PAINFUL AND NOTHING MAKES IT EASIER

        //TODO: Calm down and get a fucking grip
    }

    @Nullable
    public static BlockPos getLookedAtSignal(PlayerEntity player, List<SignalPlacementsComponent.SignalData> signals, float maxAngle, double maxDistance, SignalscopeComponent component) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();

        BlockPos closestSignal = null;
        double closestAngle = Double.MAX_VALUE;

        for (SignalPlacementsComponent.SignalData signalData : signals) {
            BlockPos signalPos = signalData.pos;

            Vec3d targetVec = Vec3d.ofCenter(signalPos).subtract(eyePos);
            double distance = targetVec.length();

            if (distance > maxDistance) continue;
            if (signalData.isPowered && !player.getUuid().equals(signalData.owner)) continue;
            if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS) && !player.getUuid().equals(signalData.owner)) continue;
            if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS) && player.getUuid().equals(signalData.owner)) continue;
            if (component.lensItemstack().isEmpty()) continue;
            if (component.frequency() != signalData.frequency) continue;

            targetVec = targetVec.normalize();

            double dot = lookVec.dotProduct(targetVec);
            dot = MathHelper.clamp(dot, -1.0F, 1.0F);
            double angle = java.lang.Math.acos(dot) * (180.0 / java.lang.Math.PI);

            if (angle <= maxAngle && angle < closestAngle) {
                closestAngle = angle;
                closestSignal = signalPos;
            }
        }

        return closestSignal;
    }

    public static boolean canShowSignal(PlayerEntity player, SignalPlacementsComponent.SignalData signalData){
        ItemStack mainHand = player.getMainHandStack();
        if (!(mainHand.getItem() instanceof SignalscopeItem)) return false;
        SignalscopeComponent component = mainHand.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
        if (component == null) return false;
        if (signalData.frequency != component.frequency()) return false;
        if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS) && signalData.owner.equals(player.getUuid())) return false;
        if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS) && !signalData.owner.equals(player.getUuid())) return false;

        return true;
    }
}
