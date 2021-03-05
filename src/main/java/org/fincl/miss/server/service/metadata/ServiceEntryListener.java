package org.fincl.miss.server.service.metadata;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.service.Service;
import org.fincl.miss.server.logging.logger.service.ServiceLogLevelUpdater;
import org.fincl.miss.server.service.metadata.impl.ServiceMetadataResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ServiceEntryListener implements EntryListener<String, Service> {

	@Autowired
	ServiceLogLevelUpdater serviceLogLevelUpdater;
	
	/**
	 * 
	 * @return
	 */
	private ServiceMetadataResolverImpl getServiceMetadataResolver() {

		return ApplicationContextSupport.getBean(ServiceMetadataResolverImpl.class);
	}

	/**
	 * 
	 */
	@Override
	public void entryAdded(final EntryEvent<String, Service> event) {
		getServiceMetadataResolver().registerService(event.getValue());
		//ServiceLogLevelService logLevelService = ApplicationContextSupport.getBean(ServiceLogLevelServiceImpl.class);
		serviceLogLevelUpdater.modifyServiceLogLevelFilter(event.getValue());
	}

	/**
	 * 
	 */
	@Override
	public void entryRemoved(final EntryEvent<String, Service> event) {
		getServiceMetadataResolver().unregisterService(event.getOldValue().getServiceId());
	}

	/**
	 * 
	 */
	@Override
	public void entryUpdated(final EntryEvent<String, Service> event) {
		final ServiceMetadataResolverImpl metadataResolver = getServiceMetadataResolver();
		final Service service = event.getValue();
		metadataResolver.unregisterService(service.getServiceId());
		metadataResolver.registerService(service);
		serviceLogLevelUpdater.modifyServiceLogLevelFilter(event.getValue());
	}

	/**
	 * 
	 */
	@Override
	public void entryEvicted(final EntryEvent<String, Service> event) {

		// Do nothing.
	}

	/**
	 * 
	 */
	@Override
	public void mapEvicted(final MapEvent event) {

		// Do nothing.
	}

	/**
	 * 
	 */
	@Override
	public void mapCleared(final MapEvent event) {

		// Do nothing.
	}
}
