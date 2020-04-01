package io.github.kobylynskyi.bikeshop.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author bogdankobylinsky
 */
@Slf4j
@RestController
public class GraphQLController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GraphQL graphQL;

    @RequestMapping(
            value = "/graphql",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ExecutionResult execute(@RequestBody ExecutionInput executionInput) throws IOException {
        return graphQL.execute(executionInput);
    }

}