package org.fincl.miss.server.message.parser.telegram.factory;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;
import org.springframework.stereotype.Component;

@Component
public class TelegramHazelInstance {
    
//    private static HazelcastInstance instance ;
//    
//    @PostConstruct
//    public static void initialize() { 
//        Config cfg = null;
//        try {
//            String xmlPath =TelegramHazelInstance.class.getResource("").getPath() + "hazelcast.xml";
//            cfg = new XmlConfigBuilder(xmlPath).build();
//        }
//        catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } 
//         
//        instance = Hazelcast.newHazelcastInstance(cfg);
//    }
 
	private static TelegramHazelInstance instance;

	@Resource(name="telegramHeaders")
	@SuppressWarnings("rawtypes")
	private transient Map headers;

	@Resource(name="telegramBodies")
	@SuppressWarnings("rawtypes")
	private transient Map bodies;
	
	@Resource(name="telegramInterfaces")
    @SuppressWarnings("rawtypes")
    private transient Map interfaces;
	
	@Resource(name="telegramInterfacesInfo")
    @SuppressWarnings("rawtypes")
    private transient Map interfacesInfo;
	
 
	    
	@PostConstruct
	public void initialize() {

		instance = this; 
	}
	
	private static TelegramHazelInstance getInstance() {

		// Bean이 생성되기 전일 경우 명시적으로 생성한다.
		if (instance == null) {
			ApplicationContextSupport.getBean(TelegramHazelInstance.class);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static <V, K> Map<String, TelegramHeaderFactory> getTelegramHeaderMap(){ 

        return getInstance().headers;
    }
    
    @SuppressWarnings("unchecked")
	public static <V, K> Map<String, TelegramBodyFactory> getTelegramBodyMap(){ 

        return getInstance().bodies;
    }
    
    @SuppressWarnings("unchecked")
    public static <V, K> Map<String, InterfaceIdVo> getTelegramInterfaceMap(){  
        return getInstance().interfaces;
    }
    
    @SuppressWarnings("unchecked")
    public static <V, K> Map<String,TB_IFS_TLGM_IFVo> getTelegramInterfaceInfoMap(){ 

        return getInstance().interfacesInfo;
    }
   
    
//    private static LinkedHashMap instance ;
//    
//    public static void initialize() { 
//        instance = new LinkedHashMap();  
//        instance.put("HEADER_MAP", new LinkedHashMap<String, TelegramHeaderFactory>());
//        instance.put("BODY_MAP", new LinkedHashMap<String, TelegramBodyFactory>()); 
//            
//    }
//   
//    public static <V, K> Map<String, TelegramHeaderFactory> getTelegramHeaderMap(String mapName){ 
//        if(instance==null || instance.get(mapName)==null)initialize();
//        
//        Map<String, TelegramHeaderFactory> mapCustomers = (Map<String, TelegramHeaderFactory>) instance.get(mapName);
//        return mapCustomers;
//    }
//    
//    public static <V, K> Map<String, TelegramBodyFactory> getTelegramBodyMap(String mapName){
//        if(instance==null || instance.get(mapName)==null)initialize();
//        Map<String, TelegramBodyFactory> mapCustomers = (Map<String, TelegramBodyFactory>) instance.get(mapName);
//        return mapCustomers;
//    }
    
}
