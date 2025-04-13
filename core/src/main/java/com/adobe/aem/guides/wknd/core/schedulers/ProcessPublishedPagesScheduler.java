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

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ProcessPublishedPagesSchedulerConfig.class)
public class ProcessPublishedPagesScheduler implements Runnable {


	private Scheduler scheduler;

	
	private SlingSettingsService slingSettings;

	
	private ResourceResolverFactory resolverFactory;


    private String schedulerExpression;


	public ResourceResolverFactory resourceResolverFactory;


	public SlingSettingsService settingsService;

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
        if (!slingSettings.getRunModes().contains("author")) {
            return;
        }

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
            Resource content = resolver.getResource("/content");
            if (content != null) {
                for (Resource page : content.getChildren()) {
                    ModifiableValueMap properties = page.adaptTo(ModifiableValueMap.class);
                    if (properties != null && "Activate".equals(properties.get("cq:lastReplicationAction"))) {
                        properties.put("processedDate", Calendar.getInstance());
                    }
                }
                resolver.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
