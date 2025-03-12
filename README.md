# familiar magic

### made during [ModFest 1.21](https://modfest.net/1.21) and showcased in [BlanketCon '25](https://modfest.net/vanity/bc25)

---

Craft a Summoning Table and some Enchanted Candles, and with a bit of setup,
you can summon any entity in the world - or even from another dimension!

If you'd like to plunge deeper into magic, there are also some strange items
you can craft...

## blocks:
* ### Summoning Table! 
  The centerpiece of any summoning ritual, along with two rings of Enchanted Candles around it.
  There are multiple ways to use the Summoning Table: 
  * You can place a True Name (described later) in the summoning table and then ignite the Summoning Table.
    This will cause Wisps of Smoke to appear around the table, which will indicate where to place
    Enchanted Candles to summon the named entity.
    * If the Enchanted Candles are already in position, doing this again will set them alight!
      You can use this to save time, and also to verify that you've placed everything correctly.
  * Once you have a full set of Enchanted Candles placed around, you can Focus [keybind] while igniting the Summoning Table
    to activate the summoning! If all goes well, a request will be sent and the top of the table will turn into a portal.
  * If you set up a full summoning ritual and have the target sit on top of the Summoning Table, you can place any pattern
    of certain blocks on the ground in a 13x13 area around the Summoning Table, you can add that pattern of blocks as an additional summoning requirement.
    To do this, Focus [keybind] while sneaking and using the Summoning Table with an empty hand, then repeat without sneaking to confirm.
    * Current valid blocks: fire, flowers, mushrooms, water plants, trapdoors, pressure plates, buttons, wires, crystals, and skulls.
      See the full list in the [tag](https://github.com/afamiliarquiet/familiar-magic/blob/main/src/main/resources/data/familiar_magic/tags/block/familiar_things.json).
  * crafted like an enchanting table, with a true name instead of a book and an amethyst shard and copper ingot instead of diamonds
* ### Enchanted Candles! 
  They're bigger than normal candles and floaty, made to be used in summoning rituals. 
  They can also burn underwater, and will change color to match.
  * crafted like normal candles, but with blaze powder instead of string
* ### Wisps of Smoke
  Created from burning a true name in a summoning table to help you arrange the candles.
  If you Focus [keybind], you'll start to see how it should come together!
  * crafted from a single fire charge

## items:
* ### True Name
  Right click on any entity to imprint their true name, which can then be used as reference in a Summoning Table.
  Can be used in a dispenser.
  * crafted from a single name tag
* ### Big Hat!
  Any practitioner of witchery needs to at least have the option to put on a big hat.
  * As a bonus, you can sneak up on a few mobs and right-click the hat onto them!
  * Sneak up on em again with an empty hand to take back the hat - 
    fear not, it'll be just as good as when you gave it to them :)
    * (supported critters: pets, slimes, silverfish, friendly sea creatures, and especially foxes!)
      See the full list in the [tag](https://github.com/afamiliarquiet/familiar-magic/blob/main/src/main/resources/data/familiar_magic/tags/entity_type/hattable.json) -
      also, if you'd like to add other mobs to the tag, please note that additional code is currently required for hat wearing.
      This tag exists as a tag instead of code primarily for easy reference.
  * crafted with 4 black wool in the shape you'd figure - like the base of an enchanting table
* ### Odd Trinket?
  * It seems to be a usable item, but does it do anything? No baseline player has seen any effect from it.
  * crafted with a piece of obsidian and raw copper
* ### Curious Vial..?
  * Something's not right with whatever is in this vial. Drink at your own risk.
  * crafted with dragon's breath and blaze powder (temporary recipe, will change in a later version)

## summoning!
* If someone sets up a summoning table with *your* true name, you'll receive a summoning request!
  It gives you the info on accepting/rejecting in the request,
  as well as the summoning position and the offerings in the table.
  * Focus + Jump to accept, Focus + Sneak to reject
* If you give a fox something that can start fires, it can also activate summoning tables! Clever little critters.

## curses..?
 * Have you found yourself suffering strange digestion, or sneezing flames? Do you want it to stop?
   Try having some beetroot soup - and make sure you're protected by fire resistance too, for good measure.

## other:
* Your focus keybind is not bound by default - neither is its twin, a toggle bind that can do the same thing while saving you from holding a button.

---

## etc.

mrp. i want to be summonable, so this mod must exist :)

and hey now it absorbed my first modfest mod! it'll happen again.
i've got a scattering of plans for other things. this mod will slowly bloat.
if you'd like i might make a version that just has the summoning,
for anyone that doesn't want all my strange creature features.