package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.adobe.aem.guides.wknd.core.models.Author;
import com.day.cq.wcm.api.Page;

/*<main>*/
/*<h2>Main Content</h2>*/
/*        <div class="content">*/
/*            <!-- This can be a dynamic component, such as a rich text component -->*/
/*            <sly data-sly-resource="${'wknd/components/content/main' @ page='${page.id}'}" />*/
/*        </div>*/
/*    </main>*/

// OSGi Component Servlet
@Component(service = Servlet.class, property = {
        "sling.servlet.resourceTypes=wknd/components/servlets/pageinfo",
        "sling.servlet.methods=GET"
})
public class PageInfoServlet extends SlingSafeMethodsServlet {

    // Dummy implementation to return an Author based on a page ID
    protected Author getAuthorForPage(String pageId) {
        return new Author("Default", "Author");

        /*ValueMap props = resource.getChild("jcr:content").adaptTo(ValueMap.class);
        String modifiedBy = props.get("cp:lastModifiedBy",String.class);*/
    }

    // Dummy implementation to return a list of child pages
    protected List<Page> getChildPagesModifiedByAuthor(String pageId, Author author) {
        return new ArrayList<>();

        /*Page page = pageManager.getPage(pagePath);
        Iterator<Page> children = page.listChidren(newPageFilter());
        while (children.hasNext()){
            Page child = children.next();
            String modifiedBy = child.getProperties.get("cq:LastModifiedBy", String.class);
            if (authorId.equals(modifiedBy)) {
                //add to list
            }
        }*/
    }

    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse resp)
            throws ServletException, IOException {
        String pageId = req.getParameter("page");
        if (pageId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'page' parameter");
            return;
        }

        Author author = getAuthorForPage(pageId);
        List<Page> childPages = getChildPagesModifiedByAuthor(pageId, author);

        // Determine file format (default to JSON)
        String format = req.getParameter("format");
        if (format == null) {
            String uri = req.getRequestURI();
            if (uri != null && uri.endsWith(".xml")) {
                format = "xml";
            } else {
                format = "json";
            }
        }

        String output;
        if ("xml".equalsIgnoreCase(format)) {
            resp.setContentType("application/xml");
            output = buildXmlOutput(author, childPages);
        } else {
            resp.setContentType("application/json");
            output = buildJsonOutput(author, childPages);
        }

        PrintWriter out = resp.getWriter();
        out.write(output);
    }

    private String buildXmlOutput(Author author, List<Page> childPages) {
        StringBuilder xml = new StringBuilder();
        xml.append("<pageInfo>");
        xml.append("<author>");
        xml.append("<firstName>").append(author.getFirstName()).append("</firstName>");
        xml.append("<lastName>").append(author.getLastName()).append("</lastName>");
        xml.append("</author>");
        xml.append("<childPages>");
        for (Page child : childPages) {
            xml.append("<page>").append(child.getTitle()).append("</page>");
        }
        xml.append("</childPages>");
        xml.append("</pageInfo>");
        return xml.toString();
    }

    private String buildJsonOutput(Author author, List<Page> childPages) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"author\":{");
        json.append("\"firstName\":\"").append(author.getFirstName()).append("\",");
        json.append("\"lastName\":\"").append(author.getLastName()).append("\"");
        json.append("},");
        json.append("\"childPages\":[");
        for (int i = 0; i < childPages.size(); i++) {
            json.append("\"").append(childPages.get(i).getTitle()).append("\"");
            if (i < childPages.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }
}
