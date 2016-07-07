package simplyhorses.common;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import simplyhorses.common.entities.*;
import simplyhorses.common.entities.horses.*;
import simplyhorses.common.entities.vehicles.*;
import simplyhorses.common.items.*;
import simplyhorses.common.worldgen.BiomeGenPrairieSH;
import simplyhorses.common.worldgen.BlockPrairieGrassSH;
import simplyhorses.client.ClientProxy;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

@Mod(
		modid = "SimplyHorses",
		name = "Simply Horses",
		version = "0.6.3"
		)
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false, 
		channels = {"SimplyHorses"},
		packetHandler = PacketHandlerSH.class
		)

public class SimplyHorses{
	@Instance("SimplyHorses")
	public static SimplyHorses instance;
	
	@SidedProxy(
			clientSide = "simplyhorses.client.ClientProxy",
			serverSide = "simplyhorses.common.CommonProxy"
			)
	public static CommonProxy proxy;
	
	//Creative Tab
	public static CreativeTabs horsesTab;
	
	//Blocks
	public static Block prairieGrass;

	//Items
	public static Item lasso;
	public static Item spawnLasso;
	public static Item mBridle;
	public static Item aShoes;
	public static Item cYoke;
	public static Item sCubes;
	public static Item cart;
	public static Item wagon;
	public static Item vardo;
	public static Item harness;
	public static Item prairieSeeds;
	
	//Biomes
	public static BiomeGenBase horsePlains;
	
	//Horse Herds
	public static Set<HorseHerdSH> Herds = new HashSet<HorseHerdSH>();
	public static Set safeSet1 = Collections.synchronizedSet(Herds);
	
	//DraughtHelpers
	public static Set<DraughtHelper> DraughtHelpers = new HashSet<DraughtHelper>();
	public static Set safeSet2 = Collections.synchronizedSet(DraughtHelpers);
	
	//Config fields
	public static int maxHerdLimit;
	public static int[] itemIDs = new int[11];
	public static int[] blockIDs = new int[1];
	public static boolean addNonIronLasso;
	public static boolean addSpawnerLasso;
	public static boolean doLassoRespawning;
	public static boolean showLeadRopes;
	public static boolean tfcON;
	public static boolean prairieON;
	public static boolean replacePlains;
	
	//Stuff
	public static int eggIDs = 300;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
		proxy.registerTickHandler();
		MinecraftForge.EVENT_BUS.register(new EventHooksSH());
		
		//Do Configuration stuff!
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        itemIDs[0] = config.get("Item IDs", "Lasso_ID", 900).getInt();
        itemIDs[1] = config.get("Item IDs", "SpawnLasso_ID", 901).getInt();
        itemIDs[2] = config.get("Item IDs", "MustangBridle_ID", 902).getInt();
        itemIDs[3] = config.get("Item IDs", "ArabianShoes_ID", 903).getInt();
        itemIDs[4] = config.get("Item IDs", "ClydesdaleYoke_ID", 904).getInt();
        itemIDs[5] = config.get("Item IDs", "SugarCubes_ID", 905).getInt();
        itemIDs[6] = config.get("Item IDs", "Cart_ID", 906).getInt();
        itemIDs[7] = config.get("Item IDs", "Wagon_ID", 907).getInt();
        itemIDs[8] = config.get("Item IDs", "Vardo_ID", 908).getInt();
        itemIDs[9] = config.get("Item IDs", "Harness_ID", 909).getInt();
        itemIDs[10] = config.get("Item IDs", "PrairieSeeds_ID", 910).getInt();
        
        blockIDs[0] = config.get("Block IDs", "PrairieGrass_ID", 900).getInt();
        
        maxHerdLimit = config.get("Simply Horses General", "MaxWildHerdSize", 15).getInt(15);
        tfcON = config.get("Simply Horses General", "TFC_on", false).getBoolean(false);
        addNonIronLasso = config.get("Simply Horses General", "NonIronLasso_on", false).getBoolean(false);
        addSpawnerLasso = config.get("Simply Horses General", "SurvivalSpawnerLasso_on", true).getBoolean(false);
        doLassoRespawning = config.get("Simply Horses General", "RespawnLassos_on", true).getBoolean(true);
        showLeadRopes = config.get("Simply Horses General", "LeadRopes_on", false).getBoolean(false);
        prairieON = config.get("Simply Horses General", "prairie_on", true).getBoolean(true);
        replacePlains = config.get("Simply Horses General", "replacePlainsWithPrairie", false).getBoolean(false);
        
        config.save();
	}
	
	@Init
	public void load(FMLInitializationEvent event){
		//Register Renders
		proxy.registerRenderInformation();
		
		//Register Gui Handler
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandlerSH());
		
		//Register Biome
		if (prairieON){
			GameRegistry.addBiome(horsePlains = new BiomeGenPrairieSH(23).setColor(9286496).setBiomeName("HorsePlains").setTemperatureRainfall(0.8F, 0.4F).setMinMaxHeight(0.104F, 0.105F));
		}
		if (replacePlains && !tfcON){
			GameRegistry.removeBiome(BiomeGenBase.plains);
		}
		
		//Initialize CreativeTab
		/*horsesTab = new CreativeTabs("SimplyHorses"){
			@Override
			public int getTabIconItemIndex()
		    {
		        return SimplyHorses.lasso.shiftedIndex;
		    }
		};
		LanguageRegistry.instance().addStringLocalization("itemGroup.SimplyHorses", "Simply Horses");*/
		
		//Register Entities
		int IDs = 600;
		
		//Register Horse Entities
		EntityRegistry.registerModEntity(EntityHorseWildSH.class, "WildHorse", IDs++, SimplyHorses.instance, 64, 1, true);
			addMobEgg(EntityHorseWildSH.class, 0x966124, 0xEDEDED);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.WildHorse.name", "en_US", "Wild Horse");
			EntityRegistry.addSpawn(EntityHorseWildSH.class, 7, 1, 1, EnumCreatureType.creature, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS));
		EntityRegistry.registerModEntity(EntityMustangSH.class, "Mustang", IDs++, SimplyHorses.instance, 64, 1, true);
			addMobEgg(EntityMustangSH.class, 0x966124, 0xEDEDED);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Mustang.name", "en_US", "Mustang");
		EntityRegistry.registerModEntity(EntityArabianSH.class, "Arabian", IDs++, SimplyHorses.instance, 64, 1, true);
			addMobEgg(EntityArabianSH.class, 0x966124, 0xEDEDED);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Arabian.name", "en_US", "Arabian");
		EntityRegistry.registerModEntity(EntityClydesdaleSH.class, "Clydesdale", IDs++, SimplyHorses.instance, 64, 1, true);
			addMobEgg(EntityClydesdaleSH.class, 0x966124, 0xEDEDED);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Clydesdale.name", "en_US", "Clydesdale");
		EntityRegistry.registerModEntity(EntityFoalSH.class, "Foal", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Foal.name", "en_US", "Foal");
		
		//Register Draught Entities	
		EntityRegistry.registerModEntity(EntityVehicleSH.class, "Vehicle", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Vehicle.name", "en_US", "Vehicle");
		EntityRegistry.registerModEntity(EntityWhippletreeSH.class, "Whippletree", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Whippletree.name", "en_US", "Whippletree");
		EntityRegistry.registerModEntity(EntitySwingletreeSH.class, "Swingletree", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Swingletree.name", "en_US", "Swingletree");
		EntityRegistry.registerModEntity(EntityCartSH.class, "Cart", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Cart.name", "en_US", "Horse Cart");
		EntityRegistry.registerModEntity(EntityWagonSH.class, "Wagon", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Wagon.name", "en_US", "Horse Wagon");
		EntityRegistry.registerModEntity(EntityVardoSH.class, "Vardo", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Vardo.name", "en_US", "Horse Vardo");
			
		EntityRegistry.registerModEntity(EntityVehicleSeatSH.class, "VehicleSeat", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.VehicleSeat.name", "en_US", "VehicleSeat");
		EntityRegistry.registerModEntity(EntityWagonChestSH.class, "WagonChest", IDs++, SimplyHorses.instance, 64, 1, false);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.WagonChest.name", "en_US", "WagonChest");
		
		//Register Misc. Entities
		EntityRegistry.registerModEntity(EntityLassoSH.class, "Lasso", IDs++, SimplyHorses.instance, 64, 1, true);
			LanguageRegistry.instance().addStringLocalization("entity.SimplyHorses.Lasso.name", "en_US", "Lasso");
		
		//Register Blocks
		prairieGrass = new BlockPrairieGrassSH(blockIDs[0]);	
			GameRegistry.registerBlock(prairieGrass, "prairieGrass");
			
		//Register Items
		lasso = (new ItemLassoSH(itemIDs[0])).setName("lasso").setUnlocalizedName("lasso");
			LanguageRegistry.addName(lasso, "Lasso");
			if (addNonIronLasso){
				GameRegistry.addRecipe(new ItemStack(lasso, 1), new Object [] {" SS", " SS", "S  ", "S", Item.silk});
			}
			else{
				GameRegistry.addRecipe(new ItemStack(lasso, 1), new Object [] {" SS", " IS", "S  ", 'S', Item.silk, 'I', Item.ingotIron});
			}
		spawnLasso = (new ItemSpawnerLassoSH(itemIDs[1])).setName("spawnlasso").setUnlocalizedName("spawnlasso");
			LanguageRegistry.addName(spawnLasso, "Spawner Lasso");
			if (addSpawnerLasso){
				GameRegistry.addRecipe(new ItemStack(spawnLasso, 1), new Object [] {" SS", " IS", "S  ", 'S', Item.silk, 'I', Item.stick});
			}
		mBridle = (new ItemBreedifierSH(itemIDs[2], EntityMustangSH.class)).setName("bridle").setUnlocalizedName("bridle");
			LanguageRegistry.addName(mBridle, "Mustang's Bridle");
			GameRegistry.addRecipe(new ItemStack(mBridle, 1), new Object [] {"LLL", "L L", "III", 'L', Item.leather, 'I', Item.ingotIron});
		aShoes = (new ItemBreedifierSH(itemIDs[3], EntityArabianSH.class)).setName("shoes").setUnlocalizedName("shoes");
			LanguageRegistry.addName(aShoes, "Arabian's Shoes");
			GameRegistry.addRecipe(new ItemStack(aShoes, 1), new Object [] {" I ", "I I", 'I', Item.ingotIron});
		cYoke = (new ItemBreedifierSH(itemIDs[4], EntityClydesdaleSH.class)).setName("yoke").setUnlocalizedName("yoke");
			LanguageRegistry.addName(cYoke, "Clydesdale's Yoke");
			GameRegistry.addRecipe(new ItemStack(cYoke, 1), new Object [] {"WIW", "I I", 'W', Block.planks, 'I', Item.ingotIron});
		sCubes = (new ItemSH(itemIDs[5])).setName("sugarcubes").setUnlocalizedName("sugarcubes");
			LanguageRegistry.addName(sCubes, "Sugarcubes");
			GameRegistry.addRecipe(new ItemStack(sCubes, 4), new Object [] {"S", 'S', Item.sugar});
		cart = (new ItemVehicleSH(itemIDs[6], 1)).setName("cart").setUnlocalizedName("cart");
			LanguageRegistry.addName(cart, "Horse Cart");
			GameRegistry.addRecipe(new ItemStack(cart, 1), new Object [] {"SCS", "WBW", Character.valueOf('S'), Item.stick, Character.valueOf('C'), Block.chest, Character.valueOf('W'), Block.planks, Character.valueOf('B'), Item.boat});
		wagon = (new ItemVehicleSH(itemIDs[7], 2)).setName("wagon").setUnlocalizedName("wagon");
			LanguageRegistry.addName(wagon, "Wagon");
			GameRegistry.addRecipe(new ItemStack(wagon, 1), new Object [] {" S ", "ICI", "WKW", Character.valueOf('S'), Item.stick, Character.valueOf('I'), Item.ingotIron, Character.valueOf('C'), Block.chest, Character.valueOf('W'), Block.planks, Character.valueOf('K'), cart});
		vardo = (new ItemVehicleSH(itemIDs[8], 3)).setName("vardo").setUnlocalizedName("vardo");
			LanguageRegistry.addName(vardo, "Vardo");
			GameRegistry.addRecipe(new ItemStack(vardo, 1), new Object [] {"WWW", "WBW", "GYG", Character.valueOf('W'), Block.cloth, Character.valueOf('B'), Item.bed, Character.valueOf('G'), Item.ingotGold, Character.valueOf('Y'), wagon});
		harness = (new ItemSH(itemIDs[9])).setName("harness").setUnlocalizedName("harness");
			LanguageRegistry.addName(harness, "Draught Harness");
			GameRegistry.addRecipe(new ItemStack(harness, 1), new Object[]{"L L", "ILI", "L L", 'L', Item.leather, 'I', Item.ingotIron});
		prairieSeeds = (new ItemPrairieSeedsSH(itemIDs[10], prairieGrass.blockID, 5)).setUnlocalizedName("prairieseeds");
			LanguageRegistry.addName(prairieSeeds, "Prairie Grass Seeds");
			
		//Register Vanilla Item Recipes
    	ModLoader.addRecipe(new ItemStack(Item.saddle, 1), new Object [] {"LLL", "L L", "I I", 'L', Item.leather, 'I', Item.ingotIron});
    	ModLoader.addRecipe(new ItemStack(Item.sugar, 1), new Object [] {"SS", "SS", 'S', SimplyHorses.sCubes});
	}
	
	//Thanks to Wuppy's vid tuts for this!
	public static int getUniqueID(){
		do {eggIDs++;}
		while (EntityList.getStringFromID(eggIDs) != null);
		
		return eggIDs;
	}
	
	public static void addMobEgg(Class <? extends Entity> entity, int primary, int secondary){
		int id = getUniqueID();
		
		EntityList.IDtoClassMapping.put(id, entity);
		EntityList.entityEggs.put(id, new EntityEggInfo(id, primary, secondary));
	}
	
    public static void tickStart(EnumSet<TickType> type, Object... tickData)
    {
		synchronized(safeSet1){
			Iterator herdIt = Herds.iterator();
			HorseHerdSH newHerd = null;
			while(herdIt.hasNext()){
				newHerd = ((HorseHerdSH) herdIt.next());
				if (newHerd.getHerdSize() <= 0){
					herdIt.remove();
				}
				else{
					newHerd.onHerdUpdate();
				}
			}
		}
		
		synchronized(safeSet2){
			Iterator draughtIt = DraughtHelpers.iterator();
			DraughtHelper dHelper = null;
			while(draughtIt.hasNext()){
				dHelper = (DraughtHelper) draughtIt.next();
				if (dHelper.getAssembly().size() <= 0){
					draughtIt.remove();
				}
				else if (dHelper.getAssembly().size() > 1 || dHelper.getLoadList() != null){
					//dHelper.onDraughtUpdate();
				}
			}
		}
    }
	
	public static void registerNewHerd(EntityHorseWildSH horse, HorseHerdSH newHerd){
		if (newHerd.getHerdSize() > 0 && safeSet1.add(newHerd)){}
		else {
			horse.setNewHerd(true);
		}
    }
	
	public static void registerNewDraughtHelper(DraughtHelper draughthelper){
		if(safeSet2.add(draughthelper)){}
	}
	
	/**Finds an entity based on its UUID passed in*/
	public static Entity findEntity(World worldObj, Entity entity, UUID id)
    {
        for (int i = 0; i < worldObj.loadedEntityList.size(); i++)
        {
            Entity entity2 = (Entity)worldObj.loadedEntityList.get(i);

            if (entity2.getDistanceToEntity(entity) <= 50F && entity.getPersistentID().equals(id)){
                return entity2;
            }
        }
        
        return null;
    }
}
