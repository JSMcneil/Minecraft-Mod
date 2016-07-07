package simplyhorses.common.entities;

public interface ISteerableSH {

	public void handleSpeedUp();
	
	public void handleSpeedDown();
	
	public void handleReversing();
	
	public void handleHardStop(int i);
	
	/**Boolean value determines if the method was called on keydown (true) or keyup (false)*/
	public void handleTurnLeft(Boolean flag);
	
	/**Boolean value determines if the method was called on keydown (true) or keyup (false)*/
	public void handleTurnRight(Boolean flag);
	
	/**Boolean value determines if the method was called on keydown (true) or keyup (false)*/
	public void handleJumping(Boolean flag);
	
	/**Boolean value determines if the method was called on keydown (true) or keyup (false)*/
	public void handleDropping(Boolean Flag);
}
