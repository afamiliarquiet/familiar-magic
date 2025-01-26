package io.github.afamiliarquiet.familiar_magic;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class FamiliarConfig extends WrappedConfig {
    @Comment("Whether to require the use of a \"willing\" tag on entities for them to be summonable")
    @Comment("Intended for use in showcase servers, where summoning could interfere with other showcases")
    @Comment("To apply this tag, use /data merge entity @n[type=!player] {\"fabric:attachments\": {\"familiar_magic:willing_familiar\": true}}")
    public Boolean useWillingTag = false;
    // .. it's that easy, huh? i think i like kaleido.
}
