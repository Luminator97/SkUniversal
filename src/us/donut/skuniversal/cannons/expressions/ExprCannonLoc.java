package us.donut.skuniversal.cannons.expressions;

import at.pavlov.cannons.cannon.Cannon;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.UUID;

import static us.donut.skuniversal.cannons.CannonsHook.cannons;

@Name("Cannons - Cannon Location")
@Description("Returns the location of a cannon.")
@Examples({"send \"%the location of the cannon with id (id of cannon at player)%\""})
public class ExprCannonLoc extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprCannonLoc.class, Location.class, ExpressionType.COMBINED, "[the] loc[ation] of [the] cannon [with ID] %string%");
    }

    private Expression<String> id;

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] e, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult pr) {
        id = (Expression<String>) e[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean b) {
        return "the location of cannon with ID " + id.toString(e, b);
    }

    @Override
    @Nullable
    protected Location[] get(Event e) {
        Cannon cannon;
        if (id.getSingle(e) == null || (cannon = cannons.getCannon(UUID.fromString(id.getSingle(e)))) == null) return null;
        return new Location[]{cannon.getLocation()};
    }

}
