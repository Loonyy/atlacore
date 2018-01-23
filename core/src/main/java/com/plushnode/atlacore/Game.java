package com.plushnode.atlacore;

import com.google.common.reflect.ClassPath;
import com.plushnode.atlacore.ability.*;
import com.plushnode.atlacore.ability.air.AirBlast;
import com.plushnode.atlacore.ability.air.AirScooter;
import com.plushnode.atlacore.ability.air.AirSwipe;
import com.plushnode.atlacore.ability.earth.Shockwave;
import com.plushnode.atlacore.ability.fire.Blaze;
import com.plushnode.atlacore.collision.CollisionSystem;
import com.plushnode.atlacore.element.BasicElement;
import com.plushnode.atlacore.element.ElementRegistry;
import com.plushnode.atlacore.protection.ProtectionSystem;
import com.plushnode.atlacore.util.TempBlockManager;

import java.io.IOException;
import java.util.Arrays;

public class Game {
    public static CorePlugin plugin;

    private static ProtectionSystem protectionSystem;
    private static CollisionSystem collisionSystem;

    private static AbilityRegistry abilityRegistry;
    private static ElementRegistry elementRegistry;
    private static AbilityInstanceManager instanceManager;
    private static TempBlockManager tempBlockManager;

    public Game(CorePlugin plugin, CollisionSystem collisionSystem) {
        Game.plugin = plugin;
        Game.collisionSystem = collisionSystem;

        instanceManager = new AbilityInstanceManager();
        abilityRegistry = new AbilityRegistry();
        protectionSystem = new ProtectionSystem();
        tempBlockManager = new TempBlockManager();
        elementRegistry = new ElementRegistry();

        elementRegistry.registerElement(new BasicElement("Air"));
        elementRegistry.registerElement(new BasicElement("Earth"));
        elementRegistry.registerElement(new BasicElement("Fire"));
        elementRegistry.registerElement(new BasicElement("Water"));

        AbilityDescription blazeDesc = new GenericAbilityDescription<>("Blaze", "Blaze it 420",
                elementRegistry.getElementByName("Fire"), 3000,
                Arrays.asList(ActivationMethod.Sneak), Blaze.class, false);

        AbilityDescription scooterDesc = new GenericAbilityDescription<>("AirScooter", "scoot scoot",
                elementRegistry.getElementByName("Air"), 3000,
                Arrays.asList(ActivationMethod.Punch), AirScooter.class, true);

        AbilityDescription shockwaveDesc = new GenericAbilityDescription<>("Shockwave", "wave wave",
                elementRegistry.getElementByName("Earth"), 6000,
                Arrays.asList(ActivationMethod.Punch, ActivationMethod.Sneak, ActivationMethod.Fall), Shockwave.class, false);

        AbilityDescription airSwipeDesc = new GenericAbilityDescription<>("AirSwipe", "swipe swipe",
                elementRegistry.getElementByName("Air"), 1500,
                Arrays.asList(ActivationMethod.Punch, ActivationMethod.Sneak), AirSwipe.class, false);

        AbilityDescription airBlastDesc = new GenericAbilityDescription<>("AirBlast", "blast blast",
                elementRegistry.getElementByName("Air"), 500,
                Arrays.asList(ActivationMethod.Punch, ActivationMethod.Sneak), AirBlast.class, false);

        abilityRegistry.registerAbility(blazeDesc);
        abilityRegistry.registerAbility(scooterDesc);
        abilityRegistry.registerAbility(shockwaveDesc);
        abilityRegistry.registerAbility(airSwipeDesc);
        abilityRegistry.registerAbility(airBlastDesc);

        initializeAbilities();
    }

    public AbilityDescription getAbilityDescription(String abilityName) {
        return abilityRegistry.getAbilityByName(abilityName);
    }

    public void addAbility(User user, Ability instance) {
        instanceManager.addAbility(user, instance);
    }

    public void update() {
        instanceManager.update();
    }

    public static ProtectionSystem getProtectionSystem() {
        return protectionSystem;
    }

    public static void setProtectionSystem(ProtectionSystem protectionSystem) {
        Game.protectionSystem = protectionSystem;
    }

    public static CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public static AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }

    public static ElementRegistry getElementRegistry() {
        return elementRegistry;
    }

    public static AbilityInstanceManager getAbilityInstanceManager() {
        return instanceManager;
    }

    public static TempBlockManager getTempBlockManager() {
        return tempBlockManager;
    }

    // Forces all abilities to be loaded. This ensures all of them create their static Config objects.
    private void initializeAbilities() {
        try {
            ClassPath cp = ClassPath.from(Game.class.getClassLoader());

            for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive("com.plushnode.atlacore.ability")) {
                try {
                    Class.forName(info.getName());
                } catch (ClassNotFoundException e) {

                }
            }
        } catch (IOException e) {

        }
    }
}