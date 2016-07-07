package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;

public class RenderArabianSH extends RenderHorseSH {

	public RenderArabianSH(ModelBase modelbase, float f) {
		super(modelbase, f);
		
		passModelGear = new ModelArabianArmorSH(0.2F);
        passModelTack = new ModelArabianArmorSH(0.3F);
	}
}
