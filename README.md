## Anti Coarse Dirt

A Minecraft mod that prevents coarse dirt from being replaced with other blocks.

### Commands

/anticoarsedirt
/anticoarsedirt help
/anticoarsedirt on
/anticoarsedirt off
/anticoarsedirt setblock <block>
/anticoarsedirt setrange <radius>
/anticoarsedirt status
/anticoarsedirt true
/anticoarsedirt false

### Config

anticoarsedirt-server.toml
```
[general]
	#Enable or disable the feature
	enabled = true
	#Radius around the player to convert coarse dirt
	#Range: 1 ~ 6
	radius = 1
	#The block to replace coarse dirt with (use minecraft:block_name format)
	#Examples: minecraft:dirt, minecraft:grass_block, minecraft:stone
	targetBlock = "minecraft:dirt"
```