---
#Likeaboss
---
##Index
* [Description](#description)
* [Configuration files](#configuration-files)
	* [Config.yml](#configyml)
	* [Bosses.yml](#bossesyml)
	* [Worldname.yml](#worldnameyml)
	* [Abilities.yml](#abilitiesyml)
		* [Potion type names](#potion-type-names)
* [Converting your old config files](#converting-your-old-config-files)
* [Commands and permissions](#commands-and-permissions)


---
##Description
This mod occasionally turns some monsters into a stronger version, boss-like. Its main purpose is to add a little diversity and randomness to the game. It can be used as a difficulty enhancer, or even as a reward system for your players, depending on your configuration.
Technically every monster spawning naturally in your world has a chance to become a boss, which hits harder, has more hit points, possess some immunities and drops better. Of course all of this is configurable.
Moreover, they can notify you of their presence by sending you a message when you hit them, when they hit you, or after a certain amount of time when you're close enough. They can also have a flaming effect around them (the one used by mob spawners).
Almost everything is configurable, and if it isn't yet, it soon will be! 

##Configuration files

###Config.yml
The basic settings

	Command:
	  Ignore:
	#Delay (in second) after which the command is effective
	    Delay: 120
	  Spawn:
	#Maximum number of bosses per command use.
	    Max: 50
	
	#You can use color codes. {PLAYER}, {BOSSNAME} and {DAMAGE} are the possible keywords.
	#{Player} can only be used in the ToOthers messages, {BOSSNAME} only in the ToPlayer, ToOthers and the Viewer messages and {DAMAGE} only in the ViewerMessage.
	Message:
	  PlayerFoundBoss:
	    ToPlayer: "&cOh noes, that's a {BOSSNAME} boss!"
	    ToOthers: "&c{PLAYER} found a {BOSSNAME} boss!"
	  BossFoundPlayer:
	    ToPlayer: "&cSneaky {BOSSNAME} boss."
	    ToOthers: "&cA {BOSSNAME} boss found {PLAYER}!"
	  Proximity: "&4You feel an evil presence..."
	  ViewerMessage: '{BOSSNAME} Boss Health: {HEALT} (-{DAMAGE})'
	  ViewerDefeated: '{BOSSNAME} boss has been defeated.'	
	
	#Each value represents an interval in second.
	Task:
	  #Frequency at which the boss list used by other tasks is updated.
	  RetrieveBossList: 0.5
	  #Effect around bosses, 0 to disable.
	  VisualEffect: 1.0
	  #Used to send a message to players when they're near a boss for a certain amount of time. 0 to disable.
	  CheckBossProximity: 0.5
	  #Used to keep the entity alive even if other plugins damage it. 0 to disable (but you shouldn't).
	  CheckEntityHealth: 2.0
	  #Used to remove bosses whose entity no longer exist in the world. Don't turn it off.
	  CheckEntityExistence: 5.0
	  #Frequency at which the player stats are saved to the hard drive.
	  SavePlayerData: 600.0
		
	#Global boss immunities and settings.
	Boss:
	  OverwriteDrops: false
	  Immunity:
	  [...]
---

###Bosses.yml
Which is the configuration file to put all your bosses in

	#Name of the boss. When showing this name to a player, it will become: Cave Spider
	#(It will put spaces on a new capitol letter and skip the part behind the '#')
	CaveSpider#base:
	  #Probability of boss occurrence ; same but from mob spawners ; maximal height.
      Spawn: 3.0 1.0 128
	  #Health ; damage ; experience. Base values are multiplied by this.
      Stats: 3.75 2.5 10.5
	  #You can add as many rolls as you want, the name doesn't matter (same for items).
      Drop:
        FirstRoll:
	      #Material ID and meta data ; probability ; minimal amount ; maximal amount.
	      #Note that the sum of probability should not exceed 100 per roll.
          FirstItem: 46:0 20.0 1 2
          [...]
---

###Worldname.yml
These are is the configuration files where you choose what monster will spawn in each world, choose which abilities they will get and what loot they will drop.  
These configuration files are located in the Worlds directory and named \<worldname\>.yml

	# This is a list of all the bosses that wil spawn in this world.
	Boss:
	# These names have to match names in the bosses.yml file
	- Blaze
	# As you see, there are two CaveSpider entries, which both point to a different entry in bosses.yml
	# although a player won't see the difference when they get a message (Output: Cave Spider)
	# You can use this to distinguish between bosses in different worlds.
	# (e.g.: use the first three letters of the worldname behind the '#' character)
	- CaveSpider
	- CaveSpider#base
	- Creeper
	- Enderman
	- Ghast
	- LavaSlime
	- PigZombie
	- Silverfish
	- Skeleton
	- Slime
	- Spider
	- Zombie
	# This is a list of the abilities all the bosses in this world will have
	Ability:
	# These names have to match names in the abilities.yml file
	- PO3
	- FP2
	#Global drops used by each boss in this world.
	Loot:
	  #You can add as many rolls as you want, the name doesn't matter (same for items).
	  FirstRoll:
	    #Material ID and meta data ; probability ; minimal amount ; maximal amount.
	    #Note that the sum of probability should not exceed 100 per roll.
	    FirstItem: 264:0 10.0 1 2
	    [...]
		    

---
		
###Abilities.yml
This is a basic abilities config file 

	AP25:
	  #The type of ability
	  Type: ArmorPierce
	  #The message to be displayed when a boss uses the ability
	  #(not defining a message is possible as well, then it just won't notify people that a boss used the ability)
	  Message: "&4{BOSSNAME} Boss used Armor Pierce."
	  #The possibility of this ability being used by a boss (ArmorPierce will be triggered on an attack by the boss, but then only 10 percent of the times)
	  Probability: 10.0
	  #This is the amount of seconds the boss will wait before trying this ability again
	  Cooldown: 5.0
	  Value: 25.0
	FP2:
	  Type: FirePunch
	  Message: "&4{BOSSNAME} Boss used Fire Punch."
	  Probability: 15.0
	  #The amount of fireticks the player will be on fire (if already on fire, this will add to the current fireticks)
	  Ticks: 2
	KB2:
	  Type: Knockback
	  Message: "&4{BOSSNAME} Boss used Knockback."
	  Probability: 25.0
	  VerticalCoef: 2.0
	  HorizontalCoef: 3.0
	PO3:
	  Type: Potion
	  Message: "&4{BOSSNAME} Boss used slowness."
	  #The type of potioneffect to use (see below for a full list of options)
	  Effect: slow
	  #The target for the potioneffect, options: 'Self' (the boss itself) or 'Other' (the player attacking the boss) (case-insensitive)
	  Target: other
	  Probability: 25.0
	  #The potion amplifier (2 in this example will give Slowness III (so 1+amplifier)) Can be omitted for potioneffects like invisibility.
	  Amplifier: 2
	  #The duration in seconds
	  Duration: 3.0
	B2:
	  Type: Bomb
	  Message: "&4{BOSSNAME} Boss planted a bomb."
	  Probability: 15.0
	  Cooldown: 7.5
	  #The fusetime in ticks (20 ticks per second is the average)
	  Fuse: 80
	  #The explosion radius
	  Radius: 3
---

####Potion type names
(Case-insensitive)
* Speed  -  Increasees movement speed
* Slow  - Decreases movement speed
* Fast_digging - Increases digging speed
* Slow_digging - Decreases digging speed
* Increase_damage - Increases damage dealt to an entity(Strength)
* Weakness - Decreases damage dealt to an entity
* Heal - Adds hp
* Harm - Subtracts hp
* Jump - Increases jumping height
* Confusion - Warps vision
* Regeneration - Regenerates health
* Damage_resistance - Decreases damage dealt to the entity
* Fire_resistance - Stops fire damage
* Water_breathing - Allows breathing under water
* Invisibility - Grants invisibility
* Blindness - blinds an entity
* Night_vision - Allows you to see in the dark
* Hunger - Increases hunger
* Poison - Deals damage to the entity over time

---

##Converting your old config files
To update to this version of Likeaboss, you **will** have to convert your config files manually (for the time being).  
Below I will try to explain you how I would do it myself.  
*If you want, you can run Likeaboss without your config files first to let it create the default ones.*

1. The first thing you'll notice, is that all the world config files are no longer stored as '\<worldname\>\\config.yml', but as 'Worlds\\\<worldname\>.yml', which unclutters your Likeaboss plugin folder. So first you will want to move all these files and rename them.  
	For people running windows, I have made a batch script which will, if placed in the 'plugins\\Likeaboss' directory, do this for you. You can get the batch script [here](https://github.com/Erackron/Likeaboss/downloads) ([direct download](https://github.com/downloads/Erackron/Likeaboss/moveWorldConfigs.bat)).

2. The next step is to move all the bosses you have in each world config to the bosses.yml file. If you still want to distinguish between bosses with the same name from multiple worlds, it is advised to use the first three letters of the world name as a suffix to the bosses name (e.g.: in a world name AdventureWorld and a boss named Creeper use: Creeper#adv)  
	This can take pretty long if you many bosses and/or worlds, unfortunately I don't have an automated method for this.

3. You will have to replace the 'Drop' node to 'Loot' in your world configs

4. Edit the config file for your worlds to comply to the world config format ([what a world config file should look like](#worldnameyml))

5. Replace 'CheckBossProximity' to 'CheckEntityProximity' and 'BossVisualEffect' to 'DrawBossEffect' in your config.yml

6. Add the 'ViewerMessage' and 'ViewerDefeated' nodes ([what config.yml should look like](#configyml))


---

##Commands and permissions
*\<argument\> is required and [argument] is optional.*

(lab.*) - All the permissions  

(lab.help) - /lab **help** - Display these commands  
(lab.info) - /lab **info** - Display some global and non-lasting stats.  
(lab.reload) - /lab **reload** - Reload configuration files.  
(lab.clear) - /lab **clear** - Clear informations given by /lab info.  
(lab.viewer) - /lab **viewer** - Toggle viewer state, which allows to see boss healths.  
(lab.ignore.*) - All the ignore permissions  
(lab.ignore) - /lab **ignore** - Toggle ignore state, which allows to not be affected by bosses.  
(lab.ignore.immediate) - Allow to bypass /lab ignore delay.  
(lab.list) - /lab **list** - Display the location of active bosses.  
(lab.stats) - /lab **stats** *[player]* - Display the leaderboard, or player stats.  
(lab.spawn) - /lab **spawn** *\<type\> [amount]*- Spawn one or multiple bosses on the targeted block.  