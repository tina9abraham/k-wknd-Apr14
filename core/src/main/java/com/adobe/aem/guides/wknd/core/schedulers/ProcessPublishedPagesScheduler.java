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



import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.api.resource.*;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ProcessPublishedPagesSchedulerConfig.class)
public class ProcessPublishedPagesScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ProcessPublishedPagesScheduler.class);

    private Scheduler scheduler;


    private SlingSettingsService slingSettings;


    private ResourceResolverFactory resolverFactory;


    private String schedulerExpression;


    @Activate
    protected void activate(ProcessPublishedPagesSchedulerConfig config) {
        this.schedulerExpression = config.scheduler_expression();

        if (!slingSettings.getRunModes().contains("author")) {
            return;
        }

        ScheduleOptions options = scheduler.EXPR(schedulerExpression);
        options.name("ProcessPublishedPagesScheduler").canRunConcurrently(false);
        scheduler.schedule(this, options);
    }

    @Override
    public void run() {
        // Only process in author mode
        if (!slingSettings.getRunModes().contains("author")) {
            return;
        }
        ResourceResolver resolver = null;
        try {
            Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "publishedPageProcessorServiceUser");
            resolver = resolverFactory.getServiceResourceResolver(authInfo);
            // Query for published pages; for each page, do:
            Resource page = resolver.getResource("/content/sample/en");
            if (page != null) {
                ReplicationStatus replicationStatus = page.adaptTo(ReplicationStatus.class);
                if (replicationStatus != null && replicationStatus.isActivated()) {
                    Resource jcrContent = page.getChild("jcr:content");
                    if (jcrContent != null) {
                        ModifiableValueMap values = jcrContent.adaptTo(ModifiableValueMap.class);
                        if (values != null) {
                            values.put("processedDate", Instant.now().toString());
                        }
                    }
                } else {
                    log.info("Page is not published. Skipping: {}", page.getPath());
                }
            }

            // Commit the changes so that in-memory changes are persisted.
            resolver.commit();
        } catch (Exception e) {
            log.error("Error processing published pages", e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
    }

}