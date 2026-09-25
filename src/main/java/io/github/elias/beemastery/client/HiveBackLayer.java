package io.github.elias.beemastery.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemPortableHive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

/**
 * Draws the portable hive on the player's back: a small 3D beehive model (bottom board with a
 * landing board, brood box, honey super, roof — models/block/portable_hive_back*.json), with the
 * tier's metal corners once upgraded, and a glowing lantern on the roof with the lantern module.
 * Plain block models, so a resource pack can restyle them.
 */
public class HiveBackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    /** One model per tier; registered as additional (standalone) models in {@link PortableHiveClient}. */
    public static final List<ResourceLocation> MODELS = List.of(
            model("portable_hive_back"),
            model("portable_hive_back_reinforced"),
            model("portable_hive_back_draconic"),
            model("portable_hive_back_legendary"));

    /** Hive size in blocks (half a block) and where it sits: centred on the upper back. */
    private static final float SCALE = 0.5f;
    private static final double BACK_Y = 0.375;
    private static final double BACK_Z = 0.125 + SCALE / 2;  // body back face (2px) + half the model
    /** Lantern module: a small lantern standing on the roof's outer corner. */
    private static final float LANTERN_SCALE = 0.5f;
    private static final double LANTERN_X = SCALE / 2 - 0.13, LANTERN_Z = -(SCALE / 2 - 0.13);
    private static final BlockState LANTERN = Blocks.LANTERN.defaultBlockState();

    private final BlockRenderDispatcher blocks;

    public HiveBackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, BlockRenderDispatcher blocks) {
        super(parent);
        this.blocks = blocks;
    }

    private static ResourceLocation model(String name) {
        return new ResourceLocation(ForestryExtras.MOD_ID, "block/" + name);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (player.isInvisible() || !(worn.getItem() instanceof FEItemPortableHive)) {
            return;
        }
        pose.pushPose();
        getParentModel().body.translateAndRotate(pose);   // follows crouching / body rotation
        // chestplate armour sticks out a bit further, so push the hive out with it
        double armorOffset = FEItemPortableHive.isArmored(worn) ? 0.06 : 0.0;
        pose.translate(0.0, BACK_Y, BACK_Z + armorOffset);
        pose.mulPose(Axis.XP.rotationDegrees(180));        // entity model space is upside down: now +y is up, -z (north, the entrance) faces away from the back

        pose.pushPose();
        pose.scale(SCALE, SCALE, SCALE);
        pose.translate(-0.5, -0.5, -0.5);                  // centre the model on the origin
        BakedModel hive = Minecraft.getInstance().getModelManager().getModel(MODELS.get(HiveTier.of(worn).ordinal()));
        blocks.getModelRenderer().renderModel(pose.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()), null, hive,
                1f, 1f, 1f, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, Sheets.cutoutBlockSheet());
        pose.popPose();

        if (new HiveStackContainer(worn).getModules().contains(FEEnumHiveModule.LANTERN)) {
            pose.translate(LANTERN_X, SCALE / 2, LANTERN_Z);  // standing on the roof
            pose.scale(LANTERN_SCALE, LANTERN_SCALE, LANTERN_SCALE);
            pose.translate(-0.5, 0.0, -0.5);
            blocks.renderSingleBlock(LANTERN, pose, buffers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
        pose.popPose();
    }
}
