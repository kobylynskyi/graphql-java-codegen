package io.github.kobylynskyi.order.external.starwars;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import io.github.kobylynskyi.starwars.graphql.Droid;
import io.github.kobylynskyi.starwars.graphql.Human;

public class CharacterTypeResolver extends TypeIdResolverBase {

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        switch (id) {
            case "Human":
                return context.constructSpecializedType(superType, Human.class);
            case "Droid":
                return context.constructSpecializedType(superType, Droid.class);
            default:
                return null;
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        return subType.getSimpleName();
    }

}