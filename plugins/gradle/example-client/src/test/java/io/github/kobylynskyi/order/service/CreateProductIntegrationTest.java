package io.github.kobylynskyi.order.service;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import io.github.kobylynskyi.product.graphql.model.CreateMutationRequest;
import io.github.kobylynskyi.product.graphql.model.ProductInputTO;
import io.github.kobylynskyi.product.graphql.model.ProductResponseProjection;
import io.github.kobylynskyi.product.graphql.model.ProductTO;
import io.github.kobylynskyi.product.graphql.model.StockStatusTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateProductIntegrationTest {

    @Disabled("The test is for reference only")
    @Test
    void createProductUsingRestAssured() {
        CreateMutationRequest createProductMutation = new CreateMutationRequest();
        createProductMutation.setProductInput(getProductInput());
        ProductResponseProjection responseProjection = new ProductResponseProjection()
                .id().title().description().sku().price().stockStatus();

        GraphQLRequest request = new GraphQLRequest(createProductMutation, responseProjection);

        GraphQLResult<Map<String, ProductTO>> result = given()
                .baseUri("http://localhost:8081")
                .contentType(ContentType.JSON)
                .body(request.toString())
                .post("/graphql")
                .then()
                .statusCode(200)
                .extract().response().as(new TypeRef<GraphQLResult<Map<String, ProductTO>>>() {
                });

        assertFalse(result.hasErrors());
        ProductTO createdProduct = result.getData().get(createProductMutation.getOperationName());
        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertEquals("GT FORCE ALUMINUM ELITE 27.5\" 2019", createdProduct.getTitle());
        assertEquals("BI001604", createdProduct.getSku());
        assertEquals(StockStatusTO.IN_STOCK, createdProduct.getStockStatus());
        assertEquals(BigDecimal.valueOf(2047.5).toString(), createdProduct.getPrice());
    }

    private static ProductInputTO getProductInput() {
        return new ProductInputTO.Builder()
                .setTitle("GT FORCE ALUMINUM ELITE 27.5\" 2019")
                .setSku("BI001604")
                .setStockStatus(StockStatusTO.IN_STOCK)
                .setPrice(BigDecimal.valueOf(2047.5).toString())
                .build();
    }

}