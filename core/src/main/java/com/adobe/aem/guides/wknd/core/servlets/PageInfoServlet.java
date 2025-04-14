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

import com.adobe.aem.guides.wknd.core.models.Author;
import com.adobe.aem.guides.wknd.core.models.AuthorInfo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


// PageInfoServlet.java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/pageinfo")
public class PageInfoServlet extends HttpServlet {

    // Instead of JCR repo
    protected Author getAuthorForPage(String pageId) {
        // Dummy implementation
        return new Author("Default", "Author");
    }

    protected List<Page> getChildPagesModifiedByAuthor(String pageId, Author author) {
        // Dummy implementation returns an empty list.
        return new ArrayList<>();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pageId = req.getParameter("page");
        if (pageId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'page' parameter");
            return;
        }

        Author author = getAuthorForPage(pageId);
        List<Page> childPages = getChildPagesModifiedByAuthor(pageId, author);

        // Find file format
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
