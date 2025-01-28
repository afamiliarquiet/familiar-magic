package io.github.afamiliarquiet.familiar_magic;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class FamiliarConfig extends WrappedConfig {
    @Comment("Whether to require the use of a \"willing\" tag on entities for them to be summonable")
    @Comment("Intended for use in showcase servers, where summoning could interfere with other showcases")
    @Comment("To apply this tag, use /data merge entity @n[type=!player] {\"fabric:attachments\": {\"familiar_magic:willing_familiar\": true}}")
    public Boolean useWillingTag = false;
    // maybe later this'll have other options, like you have to designate pattern to be summonable, and setting for designation to only happen if tamed

    // .. it's that easy, huh? i think i like kaleido.
}
