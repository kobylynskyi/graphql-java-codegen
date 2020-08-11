package io.github.dreamylost.scalar;

import graphql.language.StringValue;
import graphql.schema.*;

import java.util.regex.Pattern;

/**
 * graphql scalar type
 * <p>
 * use in graphql schema like : scalar Email
 *
 * @author 梦境迷离
 * @time 2020年04月03日
 */
public class EmailScalar {

    public static final GraphQLScalarType Email = GraphQLScalarType.newScalar().name("Email").description("A custom scalar that handles emails").coercing(
            new Coercing() {
                @Override
                public Object serialize(Object dataFetcherResult) {
                    return serializeEmail(dataFetcherResult);
                }

                @Override
                public Object parseValue(Object input) {
                    return parseEmailFromVariable(input);
                }

                @Override
                public Object parseLiteral(Object input) {
                    return parseEmailFromAstLiteral(input);
                }
            }).build();


    private static boolean looksLikeAnEmailAddress(String possibleEmailValue) {
        return Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", possibleEmailValue);
    }

    private static Object serializeEmail(Object dataFetcherResult) {
        String possibleEmailValue = String.valueOf(dataFetcherResult);
        if (looksLikeAnEmailAddress(possibleEmailValue)) {
            return possibleEmailValue;
        } else {
            throw new CoercingSerializeException("Unable to serialize " + possibleEmailValue + " as an email address");
        }
    }

    private static Object parseEmailFromVariable(Object input) {
        if (input instanceof String) {
            String possibleEmailValue = input.toString();
            if (looksLikeAnEmailAddress(possibleEmailValue)) {
                return possibleEmailValue;
            }
        }
        throw new CoercingParseValueException("Unable to parse variable value " + input + " as an email address");
    }

    private static Object parseEmailFromAstLiteral(Object input) {
        if (input instanceof StringValue) {
            String possibleEmailValue = ((StringValue) input).getValue();
            if (looksLikeAnEmailAddress(possibleEmailValue)) {
                return possibleEmailValue;
            }
        }
        throw new CoercingParseLiteralException(
                "Value is not any email address : '" + input + "'"
        );
    }
}