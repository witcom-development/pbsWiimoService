package org.fincl.miss.server.service.metadata.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fincl.miss.core.service.Service;
import org.fincl.miss.core.service.ServiceGroup;
import org.fincl.miss.core.service.ServiceRepositoryService;
import org.fincl.miss.core.service.impl.WritableServiceGroupImpl;
import org.fincl.miss.core.service.impl.WritableServiceImpl;
import org.fincl.miss.core.service.impl.WritableServiceTimeImpl;
import org.fincl.miss.core.service.util.ServiceUtils;
import org.fincl.miss.server.service.metadata.NotRegisteredServiceException;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.fincl.miss.server.service.metadata.ServiceMetadataResolver;
import org.fincl.miss.server.service.metadata.ServiceRequest;
import org.fincl.miss.server.service.metadata.UnserviceableTimeException;
import org.fincl.miss.server.service.metadata.UnusableServiceException;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ServiceMetadataResolverImpl implements ServiceMetadataResolver {

	/**
	 * 
	 */
	private static final Log LOG = LogFactory.getLog(ServiceMetadataResolverImpl.class);

	/**
	 * 
	 */
	private transient final Map<String, ServiceMetadata> metadatas;

	/**
	 * 등록된 서비스 정보를 저장하는 저장소.
	 */
	private transient ServiceRepositoryService repositoryService;

	/**
	 * 어노테이션으로 설정된 서비스 정보를 저장하는 저장소.
	 */
	private transient ConfiguredServiceRepositoryServiceImpl candidateRepositoryService;

	/**
	 * 
	 */
	private transient boolean autoRegister;

	/**
	 * 
	 */
	public ServiceMetadataResolverImpl() {

		this.metadatas = new ConcurrentHashMap<String, ServiceMetadata>();
	}

	/**
	 * 
	 * @param repositoryService
	 */
	public void setServiceRepositoryService(final ServiceRepositoryService repositoryService) {

		if (repositoryService == null) {
			throw new IllegalArgumentException("ServiceRepositoryService null");
		}

		this.repositoryService = repositoryService;
	}

	/**
	 * 
	 * @param candidateRepositoryService
	 */
	public void setCandidateServiceRepositoryService(
			final ConfiguredServiceRepositoryServiceImpl candidateRepositoryService) {

		if (candidateRepositoryService == null) {
			throw new IllegalArgumentException("CandidateServiceRepositoryService null");
		}

		this.candidateRepositoryService = candidateRepositoryService;
	}

	/**
	 * 
	 * @param autoRegister
	 */
	public void setAutoRegister(final boolean autoRegister) {

		this.autoRegister = autoRegister;
	}

	/**
	 * 
	 */
	public void initialize() {

		if (candidateRepositoryService != null) {
			for (final Service service: repositoryService.getAllServices()) {
				registerService(service);
			}

			if (autoRegister) {
				for (final ServiceGroup serviceGroup :
						candidateRepositoryService.getAllServiceGroups()) {
					final String serviceGroupName = serviceGroup.getServiceGroupName();
					if (repositoryService.getServiceGroup(serviceGroupName) != null) {
						continue;
					}

					final WritableServiceGroupImpl tempServiceGroup =
							new WritableServiceGroupImpl(serviceGroup);
					tempServiceGroup.setUserId("AutoRegisterer");

					repositoryService.addServiceGroup(tempServiceGroup);
				}

				for (final Service service : candidateRepositoryService.getAllServices()) {
					final String serviceId = service.getServiceId();
					if (repositoryService.getService(serviceId) != null) {
						continue;
					}

					final WritableServiceImpl tempService = new WritableServiceImpl(service);
					tempService.setUsable(true);
					tempService.setUserId("AutoRegisterer");
					repositoryService.addService(tempService);

					final WritableServiceTimeImpl serviceTime = new WritableServiceTimeImpl();
					serviceTime.setServiceId(serviceId);
					serviceTime.setWeekCode("1234567");
					repositoryService.addServiceTime(serviceTime);
				}
			}
		}
	}

	/**
	 * 
	 * @param service
	 */
	public void registerService(final Service service) {

		final String serviceId = service.getServiceId();
		final Service candidateService = candidateRepositoryService.getService(serviceId);
		if (candidateService == null) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Service registered but not configured: " + serviceId);
			}
			return;
		}

		final String serviceGroupName = service.getServiceGroupName();
		final String candidateServiceGroupName = candidateService.getServiceGroupName();
		if (!serviceGroupName.equals(candidateServiceGroupName)) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Registered serviceGroupName of " + serviceId +
						" differ from configured: " + serviceGroupName + ", " +
						candidateServiceGroupName);
			}
			return;
		}
		final String handlerMethodName = service.getHandlerMethodName();
		final String candidateHandlerMethodName = candidateService.getHandlerMethodName();
		if (!handlerMethodName.equals(candidateHandlerMethodName)) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Registered handlerMethodName of " + serviceId +
						" differ from configured: " + handlerMethodName + ", " +
						candidateHandlerMethodName);
			}
			return;
		}

		final ServiceGroup serviceGroup = repositoryService.getServiceGroup(serviceGroupName);
		final ServiceGroup candidateServiceGroup =
				candidateRepositoryService.getServiceGroup(candidateServiceGroupName);
		final String handlerClassName = serviceGroup.getHandlerClassName();
		final String candidateHandlerClassName = candidateServiceGroup.getHandlerClassName();
		if (!handlerClassName.equals(candidateHandlerClassName)) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Registered handlerClassName of " + serviceId +
						" differ from configured: " + handlerClassName + ", " +
						candidateHandlerClassName);
			}
			return;
		}

		final ServiceMetadata metadata = new ServiceMetadataImpl(service,
				candidateRepositoryService.getHandlerMethod(serviceId));
		
		
		metadatas.put(serviceId, metadata);
	}

	/**
	 * 
	 * @param serviceId
	 */
	public void unregisterService(final String serviceId) {

		metadatas.remove(serviceId);
	}

	/**
	 * 
	 */
	@Override
	public ServiceMetadata resolve(final String serviceId) {

		return resolve(serviceId, null);
	}

	/**
	 * 
	 */
	@Override
	public ServiceMetadata resolve(final String serviceId, final ServiceRequest serviceRequest) {

		if (serviceId == null) {
			throw new IllegalArgumentException("ServiceId null");
		}

		final ServiceMetadata metadata = metadatas.get(serviceId);
		if (metadata == null) {
			throw new NotRegisteredServiceException("Service not registered: " + serviceId, null);
		}

		if (serviceRequest != null) {
			final String clientIp = serviceRequest.getClientIp();
			if (clientIp == null) {
				throw new IllegalArgumentException("ClientIp null");
			}

			// 테스터 등록이 되어 있지 않을 경우에만 서비스 사용유무 및 실행시간 인증
			if (repositoryService.getServiceTester(serviceId, clientIp) == null) {
				if (!metadata.isUsable()) {
					throw new UnusableServiceException("Unusable service: " + serviceId, metadata);
				}
				 
			}
		}

		return metadata;
	}
}
