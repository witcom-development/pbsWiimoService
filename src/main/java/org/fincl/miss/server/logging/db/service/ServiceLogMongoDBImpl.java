package org.fincl.miss.server.logging.db.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

//@Repository
public class ServiceLogMongoDBImpl implements ServiceLogMapper {
    
    private final String LOG_COLLECTION_NAME = "COLL_SERVICE_LOG";
    
    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;
    
    @PostConstruct
    public void initCollection() {
        if (!mongoTemplate.collectionExists(LOG_COLLECTION_NAME)) {
            CollectionOptions collectionOptions = new CollectionOptions(1024 * 1024 * 1024, 1024 * 1024 * 1024, true); // bytes, count, capped
            mongoTemplate.createCollection(LOG_COLLECTION_NAME, collectionOptions);
            
            // mongoTemplate.indexOps(LOG_COLLECTION_NAME).ensureIndex(new Index().on("field", Direction.ASC));
        }
    }
    
    @Override
    public void addServiceLog(ServiceLog serviceLog) {
        
        mongoTemplate.save(serviceLog, LOG_COLLECTION_NAME);
    }
    
    @Override
    public void modifyServiceLog(ServiceLog serviceLog) {
        // TODO Auto-generated method stub
        
    }
    
}
