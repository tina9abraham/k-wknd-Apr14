package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.search.*;
import com.day.cq.search.result.*;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.*;
import org.osgi.service.component.annotations.*;
import javax.jcr.*;
import javax.servlet.Servlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@Component(service = Servlet.class,
           property = {
               "sling.servlet.paths=/bin/formsearch",
               "sling.servlet.methods=GET"
           })
public class FormSearchServlet extends SlingAllMethodsServlet {

 
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String term = request.getParameter("q");

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("type", "cq:Page");
        queryMap.put("group.p.or", "true");
        queryMap.put("group.1_fulltext", term);
        queryMap.put("group.2_fulltext", term);
        queryMap.put("p.limit", "20");

        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();

        JsonObject responseJson = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        for (Hit hit : result.getHits()) {
            try {
            	Resource pageResource = hit.getResource();
            	Resource contentRes = pageResource.getChild("jcr:content");

            	if (contentRes != null) {
            	    ValueMap vm = contentRes.getValueMap();

            	    JsonObject obj = new JsonObject();
            	    obj.addProperty("title", vm.get("jcr:title", ""));
            	    obj.addProperty("description", vm.get("jcr:description", ""));
            	    obj.addProperty("lastModified", vm.get("cq:lastModified", Calendar.class) != null
            	        ? vm.get("cq:lastModified", Calendar.class).getTime().toString()
            	        : "N/A");
            	    obj.addProperty("image", vm.get("image", ""));

            	    resultsArray.add(obj);
            	}
            } catch (Exception ignored) {}
        }

        responseJson.add("results", resultsArray);
        response.setContentType("application/json");
        response.getWriter().write(responseJson.toString());
    }
}