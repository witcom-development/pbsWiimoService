package org.fincl.miss.server.service.metadata;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public interface ServiceMetadataResolver {

	/**
	 * 서비스 아이디로 서비스 메타정보를 조회한다. 클라이언트 아이피 및 사용시간 인증은 하지 않는다.
	 * @param serviceId
	 * @return
	 */
	ServiceMetadata resolve(String serviceId);

	/**
	 * 서비스 아이디로 서비스 메타정보를 조회한다. 요청 정보를 이용하여 인증을 수행하고 인증 실패 시 예외를 발생시킨다.
	 * @param serviceId
	 * @param serviceRequest
	 * @return
	 */
	ServiceMetadata resolve(String serviceId, ServiceRequest serviceRequest);
}
