package com.adobe.aem.guides.wknd.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.aem.guides.wknd.core.models.Author;
import com.adobe.aem.guides.wknd.core.models.Page;


@ExtendWith(MockitoExtension.class)
public class MinimalMockitoTest {

    @Mock
    private HttpServletRequest request;

    @Test
    public void setUp() {
        // Even though the runner should initialize request, manually check:
        if(request == null) {
            request = Mockito.mock(HttpServletRequest.class);
        }
    }

    @Test
    public void testRequestMock() {
        Mockito.when(request.getParameter("page")).thenReturn("testPage");
        String param = request.getParameter("page");
        // If this assert fails or throws, then the mock isn’t set up properly.
        assertEquals("testPage", param);
    }
}