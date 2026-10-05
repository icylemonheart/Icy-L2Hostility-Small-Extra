package com.icyl2extra.mixin.pandora;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class PandoraMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            return net.neoforged.fml.loading.LoadingModList.get()
                    .getModFileById("curseofpandora") != null;
        } catch (Throwable e) {
            return false;
        }
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
