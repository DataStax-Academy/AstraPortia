package com.datastax.astra.web.model;

import java.io.Serializable;

/**
 * Web bean to be displayed on home page
 * @author Cedrick LUNVEN (@clunven)
 */
public class HomeBean implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -4329564412692576275L;

    private String dbid;
    
    private String regionId;
    
    private String keyspace;

    /**
     * Getter accessor for attribute 'dbid'.
     *
     * @return
     *       current value of 'dbid'
     */
    public String getDbid() {
        return dbid;
    }

    /**
     * Setter accessor for attribute 'dbid'.
     * @param dbid
     * 		new value for 'dbid '
     */
    public void setDbid(String dbid) {
        this.dbid = dbid;
    }

    /**
     * Getter accessor for attribute 'regionId'.
     *
     * @return
     *       current value of 'regionId'
     */
    public String getRegionId() {
        return regionId;
    }

    /**
     * Setter accessor for attribute 'regionId'.
     * @param regionId
     * 		new value for 'regionId '
     */
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Setter accessor for attribute 'keyspace'.
     * @param keyspace
     * 		new value for 'keyspace '
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }
    
    

}
