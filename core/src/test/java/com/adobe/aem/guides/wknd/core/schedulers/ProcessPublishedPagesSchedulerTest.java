package com.adobe.aem.guides.wknd.core.schedulers;

import com.day.cq.replication.ReplicationStatus;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.api.resource.ModifiableValueMap;

import java.time.Instant;
import com.day.cq.replication.ReplicationStatus;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.*;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.api.resource.*;
import java.util.Calendar;

@ExtendWith(AemContextExtension.class)
class ProcessPublishedPagesSchedulerTest {

    private final AemContext context = new AemContext();

    @Mock
    ResourceResolverFactory resolverFactory;

    @Mock
    SlingSettingsService slingSettings;

    @Mock
    ResourceResolver resolver;

    @InjectMocks
    ProcessPublishedPagesScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new ProcessPublishedPagesScheduler();

        // Mock SlingSettingsService with author runmode
        SlingSettingsService slingSettingsService = mock(SlingSettingsService.class);
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);

        // Mock Scheduler
        Scheduler mockScheduler = mock(Scheduler.class);

        // Mock ResourceResolverFactory
        resolverFactory = mock(ResourceResolverFactory.class);

        // Inject mocks using reflection or a small helper if fields are private
        injectField(scheduler, "scheduler", mockScheduler);
        injectField(scheduler, "slingSettings", slingSettingsService);
        injectField(scheduler, "resolverFactory", resolverFactory);
    }
    
 // Utility method for setting private fields (since AEM doesn't inject @Reference in tests)
    private void injectField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	/*
	 * void setUp() throws LoginException { MockitoAnnotations.openMocks(this);
	 * 
	 * when(slingSettings.getRunModes()).thenReturn(Set.of("author"));
	 * 
	 * Map<String, Object> authMap =
	 * Collections.singletonMap(ResourceResolverFactory.SUBSERVICE,
	 * "publishedPageProcessorServiceUser");
	 * when(resolverFactory.getServiceResourceResolver(authMap)).thenReturn(resolver
	 * );
	 * 
	 * context.load().json("/mocks/content.json", "/content"); Resource root =
	 * context.resourceResolver().getResource("/content");
	 * when(resolver.getResource("/content")).thenReturn(root);
	 * when(resolver.hasChanges()).thenReturn(true); }
	 */
	/*
	 * @Test void testRunProcessesPublishedPages() throws Exception {
	 * scheduler.resourceResolverFactory = resolverFactory;
	 * scheduler.settingsService = slingSettings;
	 * 
	 * scheduler.run();
	 * 
	 * Resource page = context.resourceResolver().getResource("/content/sample/en");
	 * assertNotNull(page);
	 * 
	 * Resource jcrContent = page.getChild("jcr:content");
	 * assertNotNull(jcrContent.getValueMap().get("processedDate")); }
	 */
	/*
	 * @Test void testRunSkipsOnPublish() {
	 * when(slingSettings.getRunModes()).thenReturn(Set.of("publish"));
	 * scheduler.settingsService = slingSettings;
	 * 
	 * scheduler.run();
	 * 
	 * // Should not throw, do nothing }
	 */
}