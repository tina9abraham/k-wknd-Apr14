package com.adobe.aem.guides.wknd.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.Session;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FormSearchServletTest {

    private FormSearchServlet servlet;
    private QueryBuilder queryBuilderMock;
    private SlingHttpServletRequest requestMock;
    private SlingHttpServletResponse responseMock;
    private ResourceResolver resourceResolverMock;
    private Session sessionMock;
    private Query queryMock;
    private SearchResult searchResultMock;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize servlet and mocks
        servlet = new FormSearchServlet();
        queryBuilderMock = mock(QueryBuilder.class);
        requestMock = mock(SlingHttpServletRequest.class);
        responseMock = mock(SlingHttpServletResponse.class);
        resourceResolverMock = mock(ResourceResolver.class);
        sessionMock = mock(Session.class);
        queryMock = mock(Query.class);
        searchResultMock = mock(SearchResult.class);

        // Inject queryBuilderMock into the servlet via reflection
        Field qbField = FormSearchServlet.class.getDeclaredField("queryBuilder");
        qbField.setAccessible(true);
        qbField.set(servlet, queryBuilderMock);

        // Common stubbing for resource resolution and query building
        when(requestMock.getResourceResolver()).thenReturn(resourceResolverMock);
        when(resourceResolverMock.adaptTo(Session.class)).thenReturn(sessionMock);
        when(queryBuilderMock.createQuery(any(PredicateGroup.class), eq(sessionMock))).thenReturn(queryMock);
    }

    @Test
    public void testDoGetWithResults() throws Exception {
        // Setup: simulate a search hit with valid page data
        String searchTerm = "test";
        when(requestMock.getParameter("q")).thenReturn(searchTerm);

        // Create a mock hit along with a fake page resource structure (jcr:content child and value map)
        Hit hitMock = mock(Hit.class);
        Resource pageResourceMock = mock(Resource.class);
        Resource contentResourceMock = mock(Resource.class);
        when(hitMock.getResource()).thenReturn(pageResourceMock);
        when(pageResourceMock.getChild("jcr:content")).thenReturn(contentResourceMock);

        // Create a ValueMap holding the page properties
        ValueMap valueMapMock = mock(ValueMap.class);
        String title = "Example Title";
        String description = "Example Description";
        String image = "example.jpg";
        Calendar lastModifiedCalendar = Calendar.getInstance();
        String lastModifiedStr = lastModifiedCalendar.getTime().toString();

        when(valueMapMock.get("jcr:title", "")).thenReturn(title);
        when(valueMapMock.get("jcr:description", "")).thenReturn(description);
        when(valueMapMock.get("image", "")).thenReturn(image);
        when(valueMapMock.get("cq:lastModified", Calendar.class)).thenReturn(lastModifiedCalendar);
        when(contentResourceMock.getValueMap()).thenReturn(valueMapMock);

        // Simulate one hit in the search result
        List<Hit> hits = new ArrayList<>();
        hits.add(hitMock);
        when(searchResultMock.getHits()).thenReturn(hits);
        when(queryMock.getResult()).thenReturn(searchResultMock);

        // Capture the servlet's output by writing to a StringWriter
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(responseMock.getWriter()).thenReturn(writer);

        // Execute the servlet's doGet() method
        servlet.doGet(requestMock, responseMock);
        writer.flush();
        String jsonOutput = stringWriter.toString();

        // Parse and verify the JSON output returned by the servlet
        JsonObject jsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertTrue(jsonObject.has("results"), "JSON should contain the key 'results'");
        JsonArray resultsArray = jsonObject.getAsJsonArray("results");
        assertEquals(1, resultsArray.size(), "There should be one result returned");

        JsonObject resultObj = resultsArray.get(0).getAsJsonObject();
        assertEquals(title, resultObj.get("title").getAsString());
        assertEquals(description, resultObj.get("description").getAsString());
        assertEquals(image, resultObj.get("image").getAsString());
        assertEquals(lastModifiedStr, resultObj.get("lastModified").getAsString());

        // Verify that the response content type is correctly set
        verify(responseMock).setContentType("application/json");
    }

    @Test
    public void testDoGetWithNoResults() throws Exception {
        // Setup: simulate no search hits by returning an empty list
        String searchTerm = "nonexistent";
        when(requestMock.getParameter("q")).thenReturn(searchTerm);

        List<Hit> hits = new ArrayList<>();
        when(searchResultMock.getHits()).thenReturn(hits);
        when(queryMock.getResult()).thenReturn(searchResultMock);

        // Prepare the response writer to capture servlet output
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(responseMock.getWriter()).thenReturn(writer);

        // Execute the doGet() method
        servlet.doGet(requestMock, responseMock);
        writer.flush();
        String jsonOutput = stringWriter.toString();

        // Parse and verify that the returned JSON contains an empty "results" array
        JsonObject jsonObject = JsonParser.parseString(jsonOutput).getAsJsonObject();
        assertTrue(jsonObject.has("results"), "JSON should contain the key 'results'");
        JsonArray resultsArray = jsonObject.getAsJsonArray("results");
        assertEquals(0, resultsArray.size(), "There should be no results returned");

        verify(responseMock).setContentType("application/json");
    }
}
