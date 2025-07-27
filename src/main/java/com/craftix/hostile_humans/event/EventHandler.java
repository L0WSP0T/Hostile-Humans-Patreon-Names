package com.craftix.hostile_humans.event;

import com.craftix.hostile_humans.compat.CollectiveVillagerNames;
import com.craftix.hostile_humans.compat.FarmersDelight;
import com.craftix.hostile_humans.compat.TravelersBackpack;
import com.craftix.hostile_humans.entity.entities.Human;
//import com.natamus.villagernames_common_forge.util.Names;
import com.natamus.villagernames_common_forge.util.Names;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import static com.craftix.hostile_humans.entity.entities.HumanInventoryGenerator.generateInventory;

import com.craftix.hostile_humans.HostileHumans;

public class EventHandler {

    static boolean addedFarmerItems;
    // random number is used for when the gear generates during the development
    String randomTag = "gave_gear" + (int) (Math.random() * 10000);
    
    @SubscribeEvent
    public void explode(ExplosionEvent event) {
    	if (!event.getWorld().isClientSide) {
			for (Human human : event.getWorld().getEntitiesOfClass(Human.class, event.getExplosion().getExploder().getBoundingBox().inflate(16.0))) {
				human.setInvestigateSound(new BlockPos(event.getExplosion().getPosition()));
			}
		}
    }
    
    @SubscribeEvent
    public void damage(LivingDamageEvent event) {
    	if (!event.getEntity().level.isClientSide) {
			for (Human human : event.getEntity().level.getEntitiesOfClass(Human.class, event.getEntity().getBoundingBox().inflate(16.0))) {
				human.setInvestigateSound(event.getEntity().blockPosition());
			}
		}
    }

    @SubscribeEvent
    public void serverStart(ServerStartedEvent event) {
    	HostileHumans.patreonNames.forEach(name -> {
        	if (Names.customnames != null) {
        		if (!Names.customnames.contains(name)) {
        			Names.customnames.add(name);
        		}
        	}
    	});
    	
        if (!addedFarmerItems && ModList.get().isLoaded("farmersdelight")) {
            FarmersDelight.addFoodItems();
            addedFarmerItems = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void spawn(LivingSpawnEvent.SpecialSpawn event) {
        if (ModList.get().isLoaded("travelersbackpack") && event.getEntity() instanceof Human human) {
            TravelersBackpack.apply(human);
        }
        if (ModList.get().isLoaded("villagernames") && event.getEntity() instanceof Human human) {
            CollectiveVillagerNames.nameEntity(human);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (ModList.get().isLoaded("travelersbackpack") && event.getEntity() instanceof Human) {
            TravelersBackpack.applyDeath(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlace(BlockEvent.EntityPlaceEvent event) {
        var placer = event.getEntity();
        if (placer != null) {
            var humans = placer.level.getEntities(placer, placer.getBoundingBox().inflate(10), entity -> entity instanceof Human otherHuman);
            for (Entity otherHuman : humans) {
                ((Human) otherHuman).isAlert = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(LivingDamageEvent event) {
        var damaged = event.getEntity();
        if (damaged instanceof Player) {
            var humans = damaged.level.getEntities(damaged, damaged.getBoundingBox().inflate(10), entity -> entity instanceof Human otherHuman);
            for (Entity otherHuman : humans) {
                ((Human) otherHuman).isAlert = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void joinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity.hasCustomName()) {
            String tag = entity.getCustomName().getString();
            if (tag.contains("give_random_gear") && !entity.getTags().contains(randomTag)) {

                if (entity instanceof Human human) {
                    human.setHomePos(human.blockPosition());
                    for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                        human.setItemSlot(equipmentslot, ItemStack.EMPTY);
                    }

                    if (ModList.get().isLoaded("travelersbackpack")) {
                        TravelersBackpack.apply(human);
                    }

                    generateInventory(human, tag.contains("ranged"));
                    entity.setCustomName(null);
                }
                if (entity instanceof ArmorStand stand && ((ArmorStand) entity).getRandom().nextFloat() < 0.2f) {
                    int[] armorstandArmor = new int[]{0, 1, 2, 0, 1, 2, 3, 3, 3};
                    int staticPick = armorstandArmor[stand.getRandom().nextInt(armorstandArmor.length)];
                    for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                        Item item = Mob.getEquipmentForSlot(equipmentslot, staticPick);

                        if (item != null) {
                            ItemStack is = item.getDefaultInstance();
                            stand.setItemSlot(equipmentslot, is);
                        }
                    }
                    entity.setCustomName(null);
                }

                entity.addTag(randomTag);
            }
        }
        if (entity instanceof Human human) {
            if (ModList.get().isLoaded("villagernames")) {
                CollectiveVillagerNames.nameEntity(human);
            }
        }
    }
}


