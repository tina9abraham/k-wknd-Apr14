package com.adobe.aem.guides.wknd.core.servlets;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.adobe.aem.guides.wknd.core.models.AuthorInfo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Author Info Servlet",
                "sling.servlet.methods=GET",
                "sling.servlet.resourceTypes=example/components/page",
                "sling.servlet.extensions=json",
                "sling.servlet.extensions=xml"
        })
public class AuthorInfoServlet extends SlingSafeMethodsServlet {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        Resource resource = request.getResource();
        Page page = resource.adaptTo(Page.class);

        if (page == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Resource is not a page.");
            return;
        }

        String authorId = page.getProperties().get("jcr:lastModifiedBy", String.class);
        String firstName = page.getProperties().get("firstName", "");
        String lastName = page.getProperties().get("lastName", "");

        if (StringUtils.isBlank(authorId)) {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("No lastModifiedBy found for page.");
            return;
        }

        AuthorInfo authorInfo = new AuthorInfo();
        authorInfo.setAuthorId(authorId);
        authorInfo.setAuthorFirstName(firstName);
        authorInfo.setAuthorLastName(lastName);
        authorInfo.setModifiedChildPages(getModifiedChildPagePaths(page, authorId));

        String ext = request.getRequestPathInfo().getExtension();

        if ("json".equals(ext)) {
            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.writeValueAsString(authorInfo));
        } else if ("xml".equals(ext)) {
            response.setContentType("application/xml");
            response.getWriter().write(xmlMapper.writeValueAsString(authorInfo));
        } else {
            response.setStatus(SlingHttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.getWriter().write("Unsupported extension. Use '.json' or '.xml'.");
        }
    }

    private List<String> getModifiedChildPagePaths(Page parentPage, String authorId) {
        List<String> modifiedPaths = new ArrayList<>();
        Iterator<Page> children = parentPage.listChildren(new PageFilter(true, true));
        while (children.hasNext()) {
            Page child = children.next();
            String modifiedBy = child.getProperties().get("jcr:lastModifiedBy", String.class);
            if (authorId.equals(modifiedBy)) {
                modifiedPaths.add(child.getPath());
            }
        }
        return modifiedPaths;
    }

    // Simple POJO for response mapping
    
}
