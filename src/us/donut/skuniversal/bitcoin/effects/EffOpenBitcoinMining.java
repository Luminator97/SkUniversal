package us.donut.skuniversal.bitcoin.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import us._donut_.bitcoin.BitcoinAPI;

import javax.annotation.Nullable;

@Name("Bitcoin - Open Mining Menu")
@Description("Opens the bitcoin mining menu to a player.")
@Examples({"open the bitcoin mining menu to player"})
public class EffOpenBitcoinMining extends Effect {

    static {
        Skript.registerEffect(EffOpenBitcoinMining.class, "open [the] bitcoin min(e|ing) [(menu|interface)] to %player%");
    }

    private Expression<Player> player;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] e, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult pr) {
        player = (Expression<Player>) e[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean b) {
        return "open bitcoin mining menu to " + player.toString(e, b);
    }

    @Override
    protected void execute(Event e) {
        if (player.getSingle(e) == null) return;
        BitcoinAPI.openMiningInterface(player.getSingle(e));
    }
}