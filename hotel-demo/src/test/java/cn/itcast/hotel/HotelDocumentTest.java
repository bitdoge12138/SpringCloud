package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.assertj.core.data.Index;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;


import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class HotelDocumentTest {
    private RestHighLevelClient client;



    @Autowired
    private IHotelService hotelService;



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
    void testAddDocument() throws IOException {
        Hotel hotel = hotelService.getById(36934L);

        HotelDoc hotelDoc = new HotelDoc(hotel);


        String json = JSON.toJSONString(hotelDoc);


        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());


        request.source(json, XContentType.JSON);

        client.index(request, RequestOptions.DEFAULT);
    }



    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("hotel", "36934");

        client.delete(request, RequestOptions.DEFAULT);
    }



    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("hotel", "36934");

        request.doc(
                "price", "952",
                "starName", "四钻"
        );

        client.update(request, RequestOptions.DEFAULT);
    }


    @Test
    void testBulkRequest() throws IOException {
        List<Hotel> hotels = hotelService.list();


        BulkRequest request = new BulkRequest();

        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);


            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON));



            client.bulk(request, RequestOptions.DEFAULT);
        }
    }



    @Test
    void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source()
                .query(QueryBuilders.matchAllQuery());

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);


        handleResponse(response);
    }

    private void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();

        long total = searchHits.getTotalHits().value;

        System.out.println("共搜索到" + total + "条数据");

        SearchHit[] hits = searchHits.getHits();


        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();

            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("name");

                if (highlightField != null) {
                    String name = highlightField.getFragments()[0].string();  // Fragments 片段的意思

                    hotelDoc.setName(name);
                }
            }

            System.out.println("hotelDoc = " + hotelDoc);
        }
    }



    @Test
    void testMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source()
                .query(QueryBuilders.matchQuery("all", "如家"));

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);


        handleResponse(response);
    }




    @Test
    void testHighlight() throws IOException {
        SearchRequest request = new SearchRequest("hotel");


        request.source().query(QueryBuilders.matchQuery("all", "如家"));

        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));


        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
    }




}
