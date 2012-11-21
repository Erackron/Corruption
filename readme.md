#Likeaboss
---
##Description
This mod occasionally turns some monsters into a stronger version, boss-like. Its main purpose is to add a little diversity and randomness to the game. It can be used as a difficulty enhancer, or even as a reward system for your players, depending on your configuration.
Technically every monster spawning naturally in your world has a chance to become a boss, which hits harder, has more hit points, possess some immunities and drops better. Of course all of this is configurable.
Moreover, they can notify you of their presence by sending you a message when you hit them, when they hit you, or after a certain amount of time when you're close enough. They can also have a flaming effect around them (the one used by mob spawners).
Almost everything is configurable, and if it isn't yet, it soon will be! 

---
##Configuration files
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
	
	#Global drops used by each boss.
	Drop:
	  FirstRoll:
	    FirstItem: 264:0 10.0 1 2
	    [...]

---
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
###Abilities.yml
This is a basic abilities config file 
	##The name of the ability, which you will have to use in other config files
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

####Potion type names
(Case-insensitive)
Speed  -  Increasees movement speed
Slow  - Decreases movement speed
Fast_digging - Increases digging speed
Slow_digging - Decreases digging speed
Increase_damage - Increases damage dealt to an entity(Strength)
Weakness - Decreases damage dealt to an entity
Heal - Adds hp
Harm - Subtracts hp
Jump - Increases jumping height
Confusion - Warps vision
Regeneration - Regenerates health
Damage_resistance - Decreases damage dealt to the entity
Fire_resistance - Stops fire damage
Water_breathing - Allows breathing under water
Invisibility - Grants invisibility
Blindness - blinds an entity
Night_vision - Allows you to see in the dark
Hunger - Increases hunger
Poison - Deals damage to the entity over time


---
##Commands and permissions

(lab.*) - All the permissions

(lab.help) - /lab help - Display this page :)
(lab.info) - /lab info - Display some global and non-lasting stats.
(lab.reload) - /lab reload - Reload configuration files.
(lab.clear) - /lab clear - Clear informations given by /lab info.
(lab.viewer) - /lab viewer - Toggle viewer state, which allows to see boss healths.
(lab.ignore.*) -
(lab.ignore) - /lab ignore - Toggle ignore state, which allows to not be affected by bosses.
(lab.ignore.immediate) - Allow to bypass /lab ignore delay.
(lab.list) - /lab list - Display the location of active bosses.
(lab.stats) - /lab stats [player] - Display the leaderboard, or player stats.
(lab.spawn) - /lab spawn <type> [amount]- Spawn one or multiple bosses on the targeted block. 