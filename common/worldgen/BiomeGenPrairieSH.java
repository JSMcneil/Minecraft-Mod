package simplyhorses.common.worldgen;

import java.util.Random;

import simplyhorses.common.entities.horses.EntityHorseWildSH;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenPrairieSH extends BiomeGenPlains {

	public BiomeGenPrairieSH(int par1) {
		super(par1);
		
		topBlock = (byte)Block.grass.blockID;
        this.theBiomeDecorator.grassPerChunk = 14;
		this.spawnableCreatureList.clear();
        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorseWildSH.class, 12, 1, 1));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8, 4, 4));
	}
	
	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random par1Random)
    {
        return new WorldGenPrairieGrassSH();
    }

}
