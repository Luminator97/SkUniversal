package us.donut.skuniversal.griefprevention;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.ryanhamshire.griefprevention.Claim;
import me.ryanhamshire.griefprevention.DataStore;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.events.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.donut.skuniversal.SkUniversalEvent;

import javax.annotation.Nullable;

public class GriefPreventionHook {

    public static DataStore dataStore;

    private static java.lang.reflect.Field dataStoreField;
    private static java.lang.reflect.Method dataStoreMethod;
    private static java.lang.reflect.Field parentField;
    private static java.lang.reflect.Method parentMethod;
    private static java.lang.reflect.Field childrenField;
    private static java.lang.reflect.Method childrenMethod;
    private static java.lang.reflect.Method legacyPermissionsMethod;
    private static java.lang.reflect.Method newPermsMethod;
    private static java.lang.reflect.Method buildersMethod;
    private static java.lang.reflect.Method containersMethod;
    private static java.lang.reflect.Method accessorsMethod;
    private static java.lang.reflect.Method managersMethod;

    public static Claim getClaim(@Nullable Long id) {
        if (id == null || dataStore == null) return null;
        Claim claim = dataStore.getClaim(id);
        if (claim != null) return claim;
        for (Claim aClaim : dataStore.getClaims()) {
            for (Claim childClaim : getChildren(aClaim)) {
                if (childClaim.getID().equals(id)) return childClaim;
            }
        }
        return null;
    }

    @Nullable
    private static Claim getParent(Claim claim) {
        try {
            if (parentMethod != null) {
                return (Claim) parentMethod.invoke(claim);
            }
        } catch (Exception ignored) {
        }
        try {
            if (parentField != null) {
                return (Claim) parentField.get(claim);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static java.util.Collection<Claim> getChildren(Claim claim) {
        try {
            if (childrenMethod != null) {
                return (java.util.Collection<Claim>) childrenMethod.invoke(claim);
            }
        } catch (Exception ignored) {
        }
        try {
            if (childrenField != null) {
                return (java.util.Collection<Claim>) childrenField.get(claim);
            }
        } catch (Exception ignored) {
        }
        return java.util.Collections.emptyList();
    }

    public static void getPermissions(Claim claim, java.util.List<String> builders, java.util.List<String> containers,
                                      java.util.List<String> accessors, java.util.List<String> managers) {
        if (claim == null) return;
        try {
            if (legacyPermissionsMethod != null) {
                legacyPermissionsMethod.invoke(claim, builders, containers, accessors, managers);
                return;
            }
        } catch (Exception ignored) {}
        try {
            if (newPermsMethod != null) {
                Object perms = newPermsMethod.invoke(claim);
                if (perms != null) {
                    if (buildersMethod == null) {
                        Class<?> c = perms.getClass();
                        buildersMethod = c.getMethod("builders");
                        containersMethod = c.getMethod("containers");
                        accessorsMethod = c.getMethod("accessors");
                        managersMethod = c.getMethod("managers");
                    }
                    builders.addAll((java.util.Collection<String>) buildersMethod.invoke(perms));
                    containers.addAll((java.util.Collection<String>) containersMethod.invoke(perms));
                    accessors.addAll((java.util.Collection<String>) accessorsMethod.invoke(perms));
                    managers.addAll((java.util.Collection<String>) managersMethod.invoke(perms));
                }
            }
        } catch (Exception ignored) {
        }
    }

    static {
        try {
            dataStoreMethod = GriefPrevention.class.getMethod("getDataStore");
        } catch (NoSuchMethodException ignored) {
        }
        try {
            dataStoreField = GriefPrevention.class.getField("dataStore");
        } catch (NoSuchFieldException ignored) {
        }
        try {
            if (dataStoreMethod != null) {
                dataStore = (DataStore) dataStoreMethod.invoke(GriefPrevention.instance);
            } else if (dataStoreField != null) {
                dataStore = (DataStore) dataStoreField.get(GriefPrevention.instance);
            }
        } catch (Exception ignored) {
        }
        try {
            parentMethod = Claim.class.getMethod("getParent");
        } catch (NoSuchMethodException ignored) {
        }
        try {
            parentField = Claim.class.getField("parent");
        } catch (NoSuchFieldException ignored) {
        }
        try {
            childrenMethod = Claim.class.getMethod("getChildren");
        } catch (NoSuchMethodException ignored) {
        }
        try {
            childrenField = Claim.class.getField("children");
        } catch (NoSuchFieldException ignored) {
        }
        try {
            legacyPermissionsMethod = Claim.class.getMethod("getPermissions", java.util.List.class, java.util.List.class, java.util.List.class, java.util.List.class);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            newPermsMethod = Claim.class.getMethod("getPermissions");
        } catch (NoSuchMethodException ignored) {
        }
        Skript.registerEvent("GriefPrevention - Claim Deletion", SkUniversalEvent.class, ClaimDeletedEvent.class, "[G[rief]P[revention]] claim delet(e|ion)")
                .description("Called when a claim is deleted.")
                .examples("on claim deletion:", "\tbroadcast \"Claim %event-number% was deleted!\"");
        EventValues.registerEventValue(ClaimDeletedEvent.class, Number.class, new Getter<Number, ClaimDeletedEvent>() {
            public Number get(ClaimDeletedEvent e) {
                return e.getClaim().getID();
            }
        }, 0);

        Skript.registerEvent("GriefPrevention - Claim Expiration", SkUniversalEvent.class, ClaimExpirationEvent.class, "[G[rief]P[revention]] claim expir(e|ation)")
                .description("Called when a claim expires.")
                .examples("on claim expiration:", "\tbroadcast \"Claim %event-number% has expired!\"");
        EventValues.registerEventValue(ClaimExpirationEvent.class, Number.class, new Getter<Number, ClaimExpirationEvent>() {
            public Number get(ClaimExpirationEvent e) {
                return e.getClaim().getID();
            }
        }, 0);

        Skript.registerEvent("GriefPrevention - Prevent Block Break", SkUniversalEvent.class, PreventBlockBreakEvent.class, "[G[rief]P[revention]] prevent block [from] break[ing]")
                .description("Called when a player tries to break a block.")
                .examples("on prevent block from breaking:", "\tbroadcast \"%player% tried to grief!\"");
        EventValues.registerEventValue(PreventBlockBreakEvent.class, Player.class, new Getter<Player, PreventBlockBreakEvent>() {
            public Player get(PreventBlockBreakEvent e) {
                return e.getInnerEvent().getPlayer();
            }
        }, 0);

        Skript.registerEvent("GriefPrevention - Prevent PvP", SkUniversalEvent.class, PreventPvPEvent.class, "[G[rief]P[revention]] prevent pvp")
                .description("Called when PvP is prevented.")
                .examples("on prevent pvp:", "\tbroadcast \"No PvP allowed!\"");

        Skript.registerEvent("GriefPrevention - Protect Drops", SkUniversalEvent.class, PreventPvPEvent.class, "[G[rief]P[revention]] protect [death] drops")
                .description("Called when death drops are protected.")
                .examples("on protect drops:", "\tbroadcast \"These drops are protected!\"");

        Skript.registerEvent("GriefPrevention - Save Trapped Player", SkUniversalEvent.class, SaveTrappedPlayerEvent.class, "[G[rief]P[revention]] save trapped player")
                .description("Called when a trapped player is saved.")
                .examples("on save trapped player:", "\tbroadcast \"A player was saved from claim %event-number%!\"");
        EventValues.registerEventValue(SaveTrappedPlayerEvent.class, Number.class, new Getter<Number, SaveTrappedPlayerEvent>() {
            public Number get(SaveTrappedPlayerEvent e) {
                return e.getClaim().getID();
            }
        }, 0);
        EventValues.registerEventValue(SaveTrappedPlayerEvent.class, Location.class, new Getter<Location, SaveTrappedPlayerEvent>() {
            public Location get(SaveTrappedPlayerEvent e) {
                return e.getDestination();
            }
        }, 0);
    }

}