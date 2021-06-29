package us.donut.skuniversal.plotsquared.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.plotsquared.core.plot.Plot;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import static us.donut.skuniversal.plotsquared.PlotSquaredHook.getPlot;

@Name("PlotSquared - Plot Rating")
@Description("Returns the rating of a plot.")
@Examples({"send \"%the rating of the plot with id (id of plot at player)%\""})
public class ExprPlotRating extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprPlotRating.class, Number.class, ExpressionType.COMBINED, "[the] [av(erage|g)] rating of [the] [PlotSquared] plot [with ID] %string%");
    }

    private Expression<String> id;

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] e, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult pr) {
        id = (Expression<String>) e[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean b) {
        return "rating of plot with id " + id.toString(e, b);
    }

    @Override
    @Nullable
    protected Number[] get(Event e) {
        Plot plot;
        if (id.getSingle(e) == null || (plot = getPlot(id.getSingle(e))) == null) return null;
        return Double.isNaN(plot.getAverageRating()) ? null : new Number[]{plot.getAverageRating()};
    }
}
