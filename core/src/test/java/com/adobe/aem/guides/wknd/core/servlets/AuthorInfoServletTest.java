package com.adobe.aem.guides.wknd.core.servlets;



import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import org.apache.sling.api.resource.ValueMap;

class AuthorInfoServletTest {

    private AuthorInfoServlet servlet;
    private SlingHttpServletRequest request;
    private SlingHttpServletResponse response;
    private Resource resource;
    private Page page;
    private RequestPathInfo pathInfo;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AuthorInfoServlet();

        request = mock(SlingHttpServletRequest.class);
        response = mock(SlingHttpServletResponse.class);
        resource = mock(Resource.class);
        page = mock(Page.class);
        pathInfo = mock(RequestPathInfo.class);

        when(request.getResource()).thenReturn(resource);
        when(request.getRequestPathInfo()).thenReturn(pathInfo);
        when(resource.adaptTo(Page.class)).thenReturn(page);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    
	/*
	 * @Test void testValidJsonResponse() throws Exception {
	 * when(pathInfo.getExtension()).thenReturn("json");
	 * 
	 * ValueMap valueMap = mock(ValueMap.class);
	 * when(valueMap.get("jcr:lastModifiedBy", String.class)).thenReturn("author1");
	 * when(valueMap.get("firstName", String.class)).thenReturn("Jane");
	 * when(valueMap.get("lastName", String.class)).thenReturn("Doe");
	 * when(page.getProperties()).thenReturn(valueMap);
	 * 
	 * // ✅ Mock listChildren to return an empty iterator (not null!)
	 * when(page.listChildren(any(PageFilter.class)))
	 * .thenReturn(Collections.emptyIterator());
	 * 
	 * servlet.doGet(request, response);
	 * 
	 * String output = responseWriter.toString();
	 * assertTrue(output.contains("Jane")); assertTrue(output.contains("author1"));
	 * 
	 * verify(response).setContentType("application/json"); verify(response,
	 * never()).setStatus(SlingHttpServletResponse.SC_BAD_REQUEST); }
	 */


    @Test
    void testMissingAuthorId() throws Exception {
        when(pathInfo.getExtension()).thenReturn("json");

        ValueMap valueMap = mock(ValueMap.class);
        when(valueMap.get("jcr:lastModifiedBy", String.class)).thenReturn(null);

        when(page.getProperties()).thenReturn(valueMap);

        servlet.doGet(request, response);

        verify(response).setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseWriter.toString().contains("No lastModifiedBy"));
    }


    @Test
    void testInvalidResource() throws Exception {
        when(resource.adaptTo(Page.class)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Resource is not a page."));
    }

	/*
	 * @Test void testUnsupportedExtension() throws Exception {
	 * when(pathInfo.getExtension()).thenReturn("html");
	 * 
	 * ValueMap valueMap = mock(ValueMap.class);
	 * when(valueMap.get("jcr:lastModifiedBy", String.class)).thenReturn("someone");
	 * 
	 * when(page.getProperties()).thenReturn(valueMap);
	 * 
	 * servlet.doGet(request, response);
	 * 
	 * verify(response).setStatus(SlingHttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE
	 * ); assertTrue(responseWriter.toString().contains("Unsupported extension")); }
	 */

}
