package com.icyl2extra.mixin.pandora;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PandoraMixinPlugin implements IMixinConfigPlugin {

    private static final String PANDORA_MODID = "curseofpandora";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return LoadingModList.get().getModFileById(PANDORA_MODID) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode transformedClass, String mixinClassName, IMixinInfo mixin) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode transformedClass, String mixinClassName, IMixinInfo mixin) {
    }
}
