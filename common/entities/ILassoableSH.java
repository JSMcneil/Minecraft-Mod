package simplyhorses.common.entities;

import net.minecraft.entity.Entity;

public interface ILassoableSH {

	/**called from the LassoHelperSH when a lasso is used on this entity.
	 * This allows the entity to decide what to do with the entity passed in. If it chooses to do nothing, 
	 * this method returns false and LassoHelperSH does not spawn a new lasso entity*/
	public boolean handleLasso(Entity par1Entity);
	
	/**should return the entity, if any, that this entity is tied to*/
	public Entity getTiedToEntity();
	
	/**should tie this entity to the passed in entity*/
	public void setTiedToEntity(Entity par1Entity);

	/**should be used to release entity from being tied directly to the player*/
	public void releaseFromLasso();

	public double[] getLassoHookPosition();
}
