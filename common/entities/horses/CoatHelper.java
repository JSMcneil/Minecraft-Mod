package simplyhorses.common.entities.horses;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagList;

public class CoatHelper{
	
	//The phenotype fields
	private int[] color;
	private String coat;
	private int coatDegree;
	private String mask;
	private int maskDegree;
	private int mutation;
	
	//Misc
	private static Random rand = new Random();
	private static final String[] COATS = new String[]{"test"};
	private static final String[] MASKS = new String[]{"tobiano"};
	private static BufferedImage COLORCHART;
	private static int[] colors;
	
	public CoatHelper(){
		this(null);
	}
	
	public CoatHelper(EntityHorseSH horse){
		randomNewCoat(horse);
	}
	
	public CoatHelper(CoatHelper cHelper, boolean sameBreed){
		setColor(cHelper.color);
		mutation = cHelper.mutation;
		if (sameBreed){
			coat = cHelper.coat;
			coatDegree = cHelper.coatDegree;
			mask = cHelper.mask;
			maskDegree = cHelper.maskDegree;
		}
	}
	
	public static void loadCoatColors(){
		try {
			File file = new File("mods/SimplyHorses/textures/horses/testing/coatcolor.png");
			if (file.exists()){
				System.out.println("coatcolor.png found!");
			}
			else{
				System.out.println("Cannot find coatcolor.png: " + file.getAbsolutePath());
			}
			COLORCHART = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("Unable to load coatcolor.png!!");
			e.printStackTrace();
		}
	}
	
	public static void setColorizer(int[] colorizer){
		colors = colorizer;
	}
	
	/** TODO Randomizes a new coat for the horse dependant on its breed unless the horse is null*/
	public CoatHelper randomNewCoat(EntityHorseSH horse){
		color = new int[]{5, 5, 5};
		coat = "test";
		coatDegree = 4;
		mask = "tobiano";
		maskDegree = 5;
		mutation = 5;
		return this;
	}
	
	/** TODO mixes the genetics of two parents' coats (this and the passed in arg) and returns a new one for a foal*/
	public static CoatHelper breed(CoatHelper parent2){
		
		return new CoatHelper();
	}

	/**returns the coordinates of the individual horses's color*/
	public int[] getColor() {
		return color;
	}

	/**sets the individual horse's color RBG values*/
	public CoatHelper setColor(int[] color) {
		if (color.length != 3){
			return this;
		}
		
		this.color = new int[3];
		for (int i = 0; i < 3; i++){
			this.color[i] = color[i];
		}
		
		return this;
	}
	
	/**finds the pixel in coatcolor.png by the passed-in coordinates and sets
	 * the coat array to its RGB values*/
	public CoatHelper setColorByCoordinates(int[] color){
		return this;
	}

	public String getCoat() {
		return coat;
	}

	public int getCoatDegree() {
		return coatDegree;
	}

	public CoatHelper setCoatAndDegree(String coat, int degree) {
		this.coat = coat;
		this.coatDegree = degree;
		
		return this;
	}

	public String getMask() {
		return mask;
	}

	public int getMaskDegree() {
		return maskDegree;
	}

	public CoatHelper setMaskAndDegree(String mask, int degree) {
		this.mask = mask;
		this.maskDegree = degree;
		
		return this;
	}

	public int getMutation() {
		return mutation;
	}

	public CoatHelper setMutation(int mutation) {
		this.mutation = mutation;
		
		return this;
	}
	
	public NBTTagList writeCoatToNBT(NBTTagList taglist){
		return taglist;
	}
	
	public void readCoatFromNBT(NBTTagList taglist){
		
	}
	
	public void writeCoatData(ByteArrayDataOutput data){
		//color
		data.writeInt(color[0]);
		data.writeInt(color[1]);
		data.writeInt(color[2]);
		
		//coat
		data.writeInt(coat.length());
		data.writeChars(coat);
		//coatdegree
		data.writeInt(coatDegree);
		
		//mask
		data.writeInt(mask.length());
		data.writeChars(mask);
		
		//maskdegree
		data.writeInt(maskDegree);
		
		//mutation
		data.writeInt(mutation);
	}
	
	public void readCoatData(ByteArrayDataInput data){
		//color
		color[0] = data.readInt();
		color[1] = data.readInt();
		color[2] = data.readInt();
		
		int i;
		String s;
		
		//coat
		i = data.readInt();
		s = "";
		for (int j = 0; j < i; j++){
			s = s.concat(Character.toString(data.readChar()));
		}
		coat = s;
		
		//coatDegree
		coatDegree = data.readInt();
		
		//mask
		i = data.readInt();
		s = "";
		for (int j = 0; j < i; j++){
			s = s.concat(Character.toString(data.readChar()));
		}
		mask = s;
		
		//maskDegree
		maskDegree = data.readInt();
		
		//mutation
		mutation = data.readInt();
	}
	

}
