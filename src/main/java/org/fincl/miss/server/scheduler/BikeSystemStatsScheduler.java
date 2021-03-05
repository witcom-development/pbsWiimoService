/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : BikeSystemStatsScheduler.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsManualMapper;
import org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper;
import org.fincl.miss.server.util.AES256anicar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @파일명          : BikeSystemStatsScheduler.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */
@Component
public class BikeSystemStatsScheduler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private BikeSystemStatsMapper bikeSystemStatsMapper;
	@Autowired
	private BikeSystemStatsManualMapper bikeSystemStatsManualMapper;
	//월별
	@ClusterSynchronized( jobToken="exeTB_STA_BIKE_USE_MM")
	public void exeTB_STA_BIKE_USE_MM() {
		logger.debug("**************통계 exeTB_STA_BIKE_USE_MM**************");
		bikeSystemStatsMapper.insertTB_STA_BIKE_USE_MM();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_BIKE_USE_HH")
	public void exeTB_STA_BIKE_USE_HH() {
		logger.debug("**************통계 exeTB_STA_BIKE_USE_HH**************");
		bikeSystemStatsMapper.insertTB_STA_BIKE_USE_HH();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_BIKE_USE_DD")
	public void exeTB_STA_BIKE_USE_DD() {
		logger.debug("**************통계 exeTB_STA_BIKE_USE_DD**************");
		bikeSystemStatsMapper.insertTB_STA_BIKE_USE_DD();
		
	}
	@ClusterSynchronized( jobToken="exeTB_STA_NEW_MB_MM")
	public void exeTB_STA_NEW_MB_MM() {
		logger.debug("**************통계 exeTB_STA_NEW_MB_MM**************");
		bikeSystemStatsMapper.insertTB_STA_NEW_MB_MM();
	}

	@ClusterSynchronized( jobToken="exeTB_STA_NEW_MB_DD")
	public void exeTB_STA_NEW_MB_DD() {
		logger.debug("**************통계 exeTB_STA_NEW_MB_DD**************");
		bikeSystemStatsMapper.insertTB_STA_NEW_MB_DD();
	}
	
	@ClusterSynchronized( jobToken="exeTB_STA_STATION_GRP_MOVE_MM")
	public void exeTB_STA_STATION_GRP_MOVE_MM() {
		logger.debug("**************통계 exeTB_STA_STATION_GRP_MOVE_MM**************");
		bikeSystemStatsMapper.insertTB_STA_STATION_GRP_MOVE_MM();
		
	}
	@ClusterSynchronized( jobToken="exeTB_STA_STATION_GRP_MOVE_DD")
	public void exeTB_STA_STATION_GRP_MOVE_DD() {
		logger.debug("**************통계 exeTB_STA_STATION_GRP_MOVE_DD**************");
		bikeSystemStatsMapper.insertTB_STA_STATION_GRP_MOVE_DD();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_STATION_RENT_DD")
	public void exeTB_STA_STATION_RENT_DD() {
		logger.debug("**************통계 exeTB_STA_STATION_RENT_DD**************");
		bikeSystemStatsMapper.insertTB_STA_STATION_RENT_DD();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_STATION_RENT_MM")
	public void exeTB_STA_STATION_RENT_MM() {
		logger.debug("**************통계 exeTB_STA_STATION_RENT_MM**************");
		bikeSystemStatsMapper.insertTB_STA_STATION_RENT_MM();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_PAYMENT_MM")
	public void exeTB_STA_PAYMENT_MM() {
		logger.debug("**************통계 exeTB_STA_PAYMENT_MM**************");
		bikeSystemStatsMapper.insertTB_STA_PAYMENT_MM();
		
	}
	@ClusterSynchronized( jobToken="exeTB_STA_PAYMENT_DD")
	public void exeTB_STA_PAYMENT_DD() {
		logger.debug("**************통계 exeTB_STA_PAYMENT_DD**************");
		bikeSystemStatsMapper.insertTB_STA_PAYMENT_DD();
		
	}
	@ClusterSynchronized( jobToken="exeTB_STA_FAULT_MM")
	public void exeTB_STA_FAULT_MM() {
		logger.debug("**************통계 exeTB_STA_FAULT_MM**************");
		bikeSystemStatsMapper.insertTB_STA_FAULT_MM();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_FAULT_DD")
	public void exeTB_STA_FAULT_DD() {
		bikeSystemStatsMapper.insertTB_STA_FAULT_DD();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_REPAIR_MM")
	public void exeTB_STA_REPAIR_MM() {
		logger.debug("**************통계 exeTB_STA_REPAIR_MM**************");
		bikeSystemStatsMapper.insertTB_STA_REPAIR_MM();
		
	}
	@ClusterSynchronized( jobToken="exeTB_STA_REPAIR_DD")
	public void exeTB_STA_REPAIR_DD() {
		logger.debug("**************통계 exeTB_STA_REPAIR_DD**************");
		bikeSystemStatsMapper.insertTB_STA_REPAIR_DD();
	}
	
	@ClusterSynchronized( jobToken="exeTB_STA_FOREIGN_RENT_MM")
	public void exeTB_STA_FOREIGN_RENT_MM() {
		logger.debug("**************통계 exeTB_STA_FOREIGN_RENT_MM**************");
		bikeSystemStatsMapper.insertTB_STA_FOREIGN_RENT_MM();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_FOREIGN_RENT_DD")
	public void exeTB_STA_FOREIGN_RENT_DD() {
		logger.debug("**************통계 exeTB_STA_FOREIGN_RENT_DD**************");
		bikeSystemStatsMapper.insertTB_STA_FOREIGN_RENT_DD();
	}
	@ClusterSynchronized( jobToken="exeTB_STA_RANK_MB_MM")
	public void exeTB_STA_RANK_MB_MM() {
		logger.debug("**************통계 exeTB_STA_RANK_MB_MM**************");
		bikeSystemStatsMapper.insertTB_STA_RANK_MB_MM();
	}
	
	@ClusterSynchronized( jobToken="exeOperationMaxBikeDD")
	public void exeOperationMaxBikeDD() {
		logger.debug("##### 최대 운영자전거 현황 통계 exeOperationMaxBikeDD #####");
		
		String useYn = bikeSystemStatsMapper.getOperationMaxBike();
		
		logger.debug("##### 최대 운영자전거 현황 통계 exeOperationMaxBikeDD ##### ==> " + useYn);
		
		if(useYn.equals("Y")){
			logger.debug("##### 최대 운영자전거 현황 통계 exeOperationMaxBikeDD ##### ==> 최대 값 변경으로 인한 insert, update 진행");
			bikeSystemStatsMapper.insertOperationMaxBikeDD();
		}else{
			logger.debug("##### 최대 운영자전거 현황 통계 exeOperationMaxBikeDD ##### ==> 진행되지 않음");
		}
		
	}
	
	/**
	 * @param args[0] batch 구분자
	 *        args[1] 파라미터. 
	 * @throws Exception 
	 * C:\workspace\miss-service\target\classes\org\fincl\miss\server\scheduler
	 * java -cp /svc/spb/src/spbServer/miss-service/WEB-INF/classes org.fincl.miss.server.scheduler.BikeSystemStatsScheduler STATS7 20151104
	 * java -cp C:\workspace\miss-service\target\classes  org.fincl.miss.server.scheduler.BikeSystemStatsScheduler STATS7 20151104
	 * */
	public static void main(String[] args) throws Exception {
		if(args == null) {
			return;
		}
		Connection conn = null;    
		/*String[] param = new String[2];
		param[0]= "STATS0";
		param[1]= "2015080619";*/
		//DB커넥션 생성
		try {
			System.out.println("+++연결!!!!1");
			String url = "jdbc:mariadb://125.133.65.235:3306/spb?interactiveClient=true&amp;autoReconnect=true&amp;autoReconnectForPools=true";
			String id = "spb";
			String pw ="spbdb1234!@#$";
			
			/*Driver driver = new org.mariadb.jdbc.Driver();
			DriverManager.registerDriver(driver);*/
			System.out.println("+++어쩌라고++++++++");
			Class.forName("org.mariadb.jdbc.Driver");
			
			conn = DriverManager.getConnection(url,id,pw);    
			//System.out.println("+++"+args[0]);
			System.out.println("+++연결!!!!");
			Map<String,String> pMap = new HashMap<String, String>();
			pMap.put("batchType", args[0]);
			pMap.put("batchParam", args[1]);
		    if(args.length > 1)	 {
		    	try {
		    	/*PreparedStatement pstm = conn.prepareStatement("");	
		    	
		    	ResultSet rs =  pstm.getResultSet();*/
		    		
				/*	session = factory.openSession();
					BikeSystemStatsManualMapper manualMapper = session.getMapper(BikeSystemStatsManualMapper.class);
					result = manualMapper.delManualStatsData(pMap); 
					if(result > 0) {
						result = manualMapper.addManualStatsData(pMap);
					}*/
				} catch (Exception e) {
					conn.rollback();
					e.printStackTrace();
					
				} finally {
					/*if(result > 0) {
						conn.commit();
					}*/
					conn.close();
				}
		    }
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		/*System.out.println("++++++++++++++++++++++++++start+++++++++++++++++++++++++++++++++++++++++");
		System.out.println("+++"+args[0]);
		int result = 0;
		DecodeDataSource dataSource = new DecodeDataSource();
		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
		dataSource.setUrl("jdbc:mariadb://125.133.65.235:3306/spb?interactiveClient=true&amp;autoReconnect=true&amp;autoReconnectForPools=true");
		dataSource.setUsername(AES256anicar.encrypt("spb"));//spb
		dataSource.setPassword(AES256anicar.encrypt("spbdb1234!@#$"));//spbdb1234!@#$"594b8174c76d38a0b7050837608025ed"
		System.out.println("++++++++++++++++++++++++++datasource+++++++++++++++++++++++++++++++++++++++++");
		String resource = "config/mybatis/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory factory = builder.build(inputStream);
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("", transactionFactory, dataSource);
		
		Configuration configuration = factory.getConfiguration();
		configuration.setEnvironment(environment);
		configuration.setLazyLoadingEnabled(true);*/
		/*
		
		
		Configuration configuration = new Configuration(environment);
		configuration.setLazyLoadingEnabled(true);
		configuration.addMapper(BikeSystemStatsManualMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	*/
		//SqlSession session = null;
		/*Map<String,String> pMap = new HashMap<String, String>();
		pMap.put("batchType", args[0]);
		pMap.put("batchParam", args[1]);
	    if(args.length > 1)	 {
	    	try {
				session = factory.openSession();
				BikeSystemStatsManualMapper manualMapper = session.getMapper(BikeSystemStatsManualMapper.class);
				result = manualMapper.delManualStatsData(pMap); 
				if(result > 0) {
					result = manualMapper.addManualStatsData(pMap);
				}
			} catch (Exception e) {
				session.rollback();
				e.printStackTrace();
				
			} finally {
				if(result > 0) {
					session.commit();
				}
	    		session.close();
			}
	    }*/
	}
}
