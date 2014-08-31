package com.axisrooms.db.dao;

import com.axisrooms.db.dao.impl.ARDaoImpl;
import com.axisrooms.db.dao.impl.ArcDao;
import com.axisrooms.db.dao.impl.ArcDaoImpl;

public class ARDaoManager {
    public static ARDao getInstance() {
        return new ARDaoImpl();
    }

    public static ArcDao getArcInstance() {
        return new ArcDaoImpl();
    }

//    public static QueueInfoDAO getQueueInfoInstance() {
//        Config config = ArcRedisConfig.getConfig();
//        JedisPool redisPool = new JedisPool(new JedisPoolConfig(), RedisDatabaseManager.getHost(),
//                RedisDatabaseManager.getPort(), RedisDatabaseManager.getTimeout(), RedisDatabaseManager.getPassword(),
//                RedisDatabaseManager.getDatabase());
//        return new QueueInfoDAORedisImpl(config, redisPool);
//    }
}
