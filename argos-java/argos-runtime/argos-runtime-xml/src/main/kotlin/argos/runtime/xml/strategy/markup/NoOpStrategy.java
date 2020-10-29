package argos.runtime.xml.strategy.markup;

import canon.api.IRenderable;
import canon.parser.xml.strategy.AbstractParseStrategy;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.List;

public class NoOpStrategy extends AbstractParseStrategy<IRenderable> {
    @Override
    public IRenderable parse(@NotNull Node node, @NotNull Function1<? super Node, ? extends List<? extends IRenderable>> function1) {
        return null;
    }
}
