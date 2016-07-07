package simplyhorses.client.entities;

import net.minecraft.client.model.ModelBase;

public class RenderClydesdaleSH extends RenderHorseSH {

	public RenderClydesdaleSH(ModelBase modelbase, float f) {
		super(modelbase, f);
        
        passModelGear = new ModelClydesdaleArmorSH(0.2F);
        passModelTack = new ModelClydesdaleArmorSH(0.3F);
	}

}
