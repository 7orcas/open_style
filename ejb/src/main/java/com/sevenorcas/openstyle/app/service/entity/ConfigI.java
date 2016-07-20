package com.sevenorcas.openstyle.app.service.entity;

/**
 * Configuration Interface<p>
 * 
 * Interface for classes that use and persist an encoded string of configurations.  
 *  
 * [License] 
 * @author John Stewart
 */

public interface ConfigI {
    public String getConfig();
    public void setConfig(String config);
    public String encode();
    public void decode(String config) throws Exception;
}
