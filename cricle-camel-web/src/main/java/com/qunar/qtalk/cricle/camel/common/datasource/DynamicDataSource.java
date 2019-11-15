package com.qunar.qtalk.cricle.camel.common.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoling.wang on 2018/12/28.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    public static final String MASTER = "master";
    public static final String SLAVE  = "slave";

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>(){
        @Override
        protected String initialValue() {
            return MASTER;
        }
    };

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(new HashMap<Object, Object>(targetDataSources));
        super.afterPropertiesSet();
    }


    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    public static void setDataSource(String dataSource) {
        contextHolder.set(dataSource);
    }

    public static String getDataSource() {
        return contextHolder.get();
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }
}
