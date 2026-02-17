package uk.acronical.script;

import org.apache.commons.jexl3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.LoggerUtils;

import java.util.Map;

/**
 * A service for evaluating dynamic expressions and scripts at runtime.
 * <p>
 * This utility utilises the Apache Commons JEXL engine to process string-based
 * logic, allowing for highly flexible configuration-driven behaviour.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class ScriptService {

    private final JexlEngine engine;

    /**
     * Initialises the {@link ScriptService} with a cached and strict JEXL engine.
     */
    public ScriptService() {
        this.engine = new JexlBuilder().cache(512).strict(true).silent(true).create();
    }

    /**
     * Evaluates a raw JEXL expression within a provided variable context.
     *
     * @param expression       The string expression to evaluate (e.g., {@code "player.level > 10"}).
     * @param contextVariables A map of objects to be injected into the script context.
     * @return The resulting object from the evaluation, or {@code null}.
     * @throws IllegalArgumentException If the expression is malformed or invalid.
     */
    @Nullable
    public Object evaluate(@NotNull String expression, @NotNull Map<String, Object> contextVariables) {
        try  {
            JexlExpression jexlExpression = engine.createExpression(expression);
            JexlContext jexlContext = new MapContext();

            for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
                jexlContext.set(entry.getKey(), entry.getValue());
            }

            return jexlExpression.evaluate(jexlContext);
        } catch (JexlException exception) {
            LoggerUtils.severe("Failed to evaluate script expression: " + expression);
            throw new IllegalArgumentException("Invalid script expression: " + expression, exception);
        }
    }

    /**
     * Evaluates an expression and returns a boolean result.
     *
     * @param expression       The expression to evaluate.
     * @param contextVariables The variables available to the script.
     * @return {@code true} if the result is a Boolean and evaluates to true; otherwise {@code false}.
     */
    public boolean evaluateBoolean(@NotNull String expression, @NotNull Map<String, Object> contextVariables) {
        Object result = evaluate(expression, contextVariables);
        return result instanceof Boolean && (Boolean) result;
    }

    /**
     * Evaluates an expression and returns a mathematical double result.
     *
     * @param expression       The expression to evaluate.
     * @param contextVariables The variables available to the script.
     * @return The numeric value of the result, or {@code 0.0} if the result is not a number.
     */
    public double evaluateMath(@NotNull String expression, @NotNull Map<String, Object> contextVariables) {
        Object result = evaluate(expression, contextVariables);
        if (result instanceof Number) return ((Number) result).doubleValue();
        return 0.0;
    }
}
