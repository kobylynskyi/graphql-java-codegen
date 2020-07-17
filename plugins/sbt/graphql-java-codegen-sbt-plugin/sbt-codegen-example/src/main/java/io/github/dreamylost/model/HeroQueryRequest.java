package io.github.dreamylost.model;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@javax.annotation.Generated(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "2020-07-17T11:17:19+0800"
)
public class HeroQueryRequest implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.QUERY;
    private static final String OPERATION_NAME = "hero";

    private Map<String, Object> input = new LinkedHashMap<>();

    public HeroQueryRequest() {
    }

    public void setEpisode(EpisodeEntity episode) {
        this.input.put("episode", episode);
    }

    @Override
    public GraphQLOperation getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperationName() {
        return OPERATION_NAME;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

    @Override
    public String toString() {
        return Objects.toString(input);
    }

    public static class Builder {

        private EpisodeEntity episode;

        public Builder() {
        }

        public Builder setEpisode(EpisodeEntity episode) {
            this.episode = episode;
            return this;
        }


        public HeroQueryRequest build() {
            HeroQueryRequest obj = new HeroQueryRequest();
            obj.setEpisode(episode);
            return obj;
        }

    }
}
