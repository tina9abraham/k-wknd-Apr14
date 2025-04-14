package com.adobe.aem.guides.wknd.core.servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.aem.guides.wknd.core.models.Author;
import com.day.cq.commons.Filter;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PageInfoServletTest {

    private PageInfoServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    private StringWriter responseWriter;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Create a subclass of the servlet overriding the data retrieval methods.
        servlet = new PageInfoServlet() {
            @Override
            protected Author getAuthorForPage(String pageId) {
                return new Author("John", "Doe");
            }
            
            @Override
            protected List<Page> getChildPagesModifiedByAuthor(String pageId, Author author) {
                List<Page> pages = new ArrayList<>();
                pages.add(new FakePageImpl("Child1"));
                pages.add(new FakePageImpl("Child2"));
                return pages;
            }
        };
        
        responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }
    
    @Test
    public void testJsonOutput() throws Exception {
        Mockito.when(request.getParameter("page")).thenReturn("testPage");
        Mockito.when(request.getParameter("format")).thenReturn("json");
        Mockito.when(request.getRequestURI()).thenReturn("/pageinfo.json");
        
        servlet.doGet(request, response);
        
        // Verify that the content type is set to application/json.
        Mockito.verify(response, Mockito.atLeastOnce()).setContentType("application/json");
        response.getWriter().flush();
        String result = responseWriter.toString();
        assertTrue(result.contains("\"firstName\":\"John\""), "JSON should contain firstName");
        assertTrue(result.contains("\"lastName\":\"Doe\""), "JSON should contain lastName");
        assertTrue(result.contains("\"Child1\""), "JSON should contain first child page");
        assertTrue(result.contains("\"Child2\""), "JSON should contain second child page");
    }
    
    @Test
    public void testXmlOutput() throws Exception {
        Mockito.when(request.getParameter("page")).thenReturn("testPage");
        Mockito.when(request.getParameter("format")).thenReturn("xml");
        Mockito.when(request.getRequestURI()).thenReturn("/pageinfo.xml");
        
        servlet.doGet(request, response);
        
        Mockito.verify(response, Mockito.atLeastOnce()).setContentType("application/xml");
        response.getWriter().flush();
        String result = responseWriter.toString();
        assertTrue(result.contains("<firstName>John</firstName>"), "XML should contain firstName element");
        assertTrue(result.contains("<lastName>Doe</lastName>"), "XML should contain lastName element");
        assertTrue(result.contains("<page>Child1</page>"), "XML should contain first child page");
        assertTrue(result.contains("<page>Child2</page>"), "XML should contain second child page");
    }
    
    @Test
    public void testOutputDefaultFormatWhenNoFormatParam() throws Exception {
        // Test case when URI ends with ".xml"
        Mockito.when(request.getParameter("page")).thenReturn("testPage");
        Mockito.when(request.getParameter("format")).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/pageinfo.xml");
        
        servlet.doGet(request, response);
        Mockito.verify(response, Mockito.atLeastOnce()).setContentType("application/xml");
        response.getWriter().flush();
        String result = responseWriter.toString();
        assertTrue(result.contains("<firstName>John</firstName>"), "Default (inferred) XML should contain firstName element");
        
        // Reset the writer for the next check.
        responseWriter.getBuffer().setLength(0);
        PrintWriter newWriter = new PrintWriter(responseWriter);
        Mockito.when(response.getWriter()).thenReturn(newWriter);
        
        // Test case when URI does not end with ".xml" (default to JSON).
        Mockito.when(request.getParameter("format")).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/pageinfo.html");
        
        servlet.doGet(request, response);
        Mockito.verify(response, Mockito.atLeastOnce()).setContentType("application/json");
        newWriter.flush();
        result = responseWriter.toString();
        assertTrue(result.contains("\"firstName\":\"John\""), "Default JSON should contain firstName");
    }
    
    @Test
    public void testMissingPageParameter() throws Exception {
        Mockito.when(request.getParameter("page")).thenReturn(null);
        servlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'page' parameter");
    }
    
    // Abstract FakePage implementation to work with com.day.cq.wcm.api.Page.
    private static abstract class FakePage implements Page {
        private final String title;
        
        public FakePage(String title) {
            this.title = title;
        }
        
        @Override
        public String getTitle() {
            return title;
        }
        
        @Override
        public String getPath() {
            return null;
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public String getNavigationTitle() {
            return null;
        }
        
        // Provide a concrete implementation for hasChildren without using @Override,
        // as required.
        public boolean hasChildren() {
            return false;
        }
    }
    
    // Concrete subclass of FakePage to allow instantiation.
    private static class FakePageImpl extends FakePage {
        public FakePageImpl(String title) {
            super(title);
        }

		@Override
		public PageManager getPageManager() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Resource getContentResource() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Resource getContentResource(String relPath) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator<Page> listChildren() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator<Page> listChildren(Filter<Page> filter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterator<Page> listChildren(Filter<Page> filter, boolean deep) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChild(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getDepth() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Page getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Page getParent(int level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Page getAbsoluteParent(int level) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ValueMap getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ValueMap getProperties(String relPath) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPageTitle() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isHideInNav() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean hasContent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isValid() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public long timeUntilValid() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Calendar getOnTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Calendar getOffTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Calendar getDeleted() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDeletedBy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLastModifiedBy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Calendar getLastModified() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getVanityUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Tag[] getTags() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void lock() throws WCMException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLocked() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getLockOwner() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canUnlock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void unlock() throws WCMException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Template getTemplate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLanguage(boolean ignoreContent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLanguage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
