package com.adobe.aem.guides.wknd.core.schedulers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class ProcessPublishedPagesSchedulerTest {

    private final AemContext context = new AemContext();
    private ProcessPublishedPagesScheduler scheduler;
    private Scheduler mockScheduler;
    private ResourceResolverFactory resolverFactory;
    private SlingSettingsService slingSettingsService;

    @BeforeEach
    public void setUp() throws Exception {
        // Instantiate the scheduler under test
        scheduler = new ProcessPublishedPagesScheduler();

        // Create a SlingSettingsService mock that returns "author"
        slingSettingsService = mock(SlingSettingsService.class);
        Set<String> authorRunModes = Collections.singleton("author");
        when(slingSettingsService.getRunModes()).thenReturn(authorRunModes);

        // Create a mock Scheduler from the Sling commons scheduler
        mockScheduler = mock(Scheduler.class);

        // Create a mock for the ResourceResolverFactory
        resolverFactory = mock(ResourceResolverFactory.class);

        // Inject the mocks into the private fields of our scheduler.
        injectField(scheduler, "scheduler", mockScheduler);
        injectField(scheduler, "slingSettings", slingSettingsService);
        injectField(scheduler, "resolverFactory", resolverFactory);
    }

    /**
     * Test the scheduler's run() method when running in author mode.
     * It loads a sample content JSON which represents published pages,
     * runs the service, and then verifies that the pages have been updated with a processedDate.
     */
    @Test
    public void testRunProcessesPagesOnAuthor() throws Exception {
        // Load test content representing published pages.
        // Ensure that the JSON file placed at /mocks/content.json contains a page like:
        // { "sample": { "en": { "jcr:content": {} } } }
        context.load().json("/mocks/content.json", "/content");

        // Use the AEM context's resolver as the simulated service resource resolver.
        ResourceResolver resolver = context.resourceResolver();
        Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "publishedPageProcessorServiceUser");
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resolver);

        // Run the scheduler job (which simulates the scheduled run)
        scheduler.run();

        // Retrieve the page that should have been processed.
        Resource page = resolver.getResource("/content/sample/en");
        assertNotNull(page, "The page /content/sample/en should exist");

        // Check that the 'jcr:content' node has the 'processedDate' property set.
        Resource jcrContent = page.getChild("jcr:content");
        assertNotNull(jcrContent, "The jcr:content node should exist");
        ModifiableValueMap valueMap = jcrContent.adaptTo(ModifiableValueMap.class);
        assertNotNull(valueMap.get("processedDate"), "processedDate property should be set on jcr:content");
    }

    /**
     * Test the scheduler's run() method when running in publish mode.
     * The service should skip processing and not attempt to retrieve a resource resolver.
     */
    @Test
    public void testRunSkipsOnPublish() throws Exception {
        // Set the run modes to publish only
        Set<String> publishRunModes = Collections.singleton("publish");
        when(slingSettingsService.getRunModes()).thenReturn(publishRunModes);

        // Run the scheduler; since we're not on author, nothing should occur.
        scheduler.run();

        // Verify that getServiceResourceResolver was not called
        verify(resolverFactory, never()).getServiceResourceResolver(any());
    }

    /**
     * Utility method to inject a value into a private field of an object.
     *
     * @param target    The object whose field should be modified.
     * @param fieldName The name of the field.
     * @param value     The value to set.
     */
    private void injectField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field " + fieldName, e);
        }
    }
}
