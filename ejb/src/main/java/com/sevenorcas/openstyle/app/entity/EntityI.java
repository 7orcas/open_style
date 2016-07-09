package com.sevenorcas.openstyle.app.entity;

/**
 * Entity Interface<p>
 * 
 * Objects implement <b>this</b> interface to identify themselves as Application Entities.<br>
 * Application entities contain the following attributes:<ul>
	 * <li>A <code>Long primary id</code> field</li>
	 * <li>The <code>permissionKey</code> method to return the entities permission key (used to determine user rights for the entity)</li>
 * </ul><p>
 * 
 * [License]
 * @author John Stewart
 */
public interface EntityI extends IdI {
	
	final static public String DEFAULT_PERM = "Default";
	public String permissionKey();
}
