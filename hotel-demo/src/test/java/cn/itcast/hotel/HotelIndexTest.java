package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

public class HotelIndexTest {
    private RestHighLevelClient client;





    @BeforeEach     // 在每个test方法之前执行该注解
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://114.212.245.239:9200")
        ));
    }

    @AfterEach      // 在每个test方法之后执行该注解
    void tearDown() throws IOException {
        this.client.close();
    }


    @Test
    void createHotelIndex() throws IOException {

        CreateIndexRequest request = new CreateIndexRequest("hotel");


        request.source(MAPPING_TEMPLATE, XContentType.JSON);

        client.indices().create(request, RequestOptions.DEFAULT);

    }



    @Test
    void testDeleteHotelIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");

        client.indices().delete(request, RequestOptions.DEFAULT);
    }



    @Test
    void testExistsHotelIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("hotel");

        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists ? "索引库已经存在!" : "索引库不存在!");

    }

}
