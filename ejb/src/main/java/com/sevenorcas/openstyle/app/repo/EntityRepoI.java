
package com.sevenorcas.openstyle.app.repo;

import javax.persistence.EntityManager;

import com.sevenorcas.openstyle.app.entity.BaseEntity;
import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Interface for <code>BaseEntity</code> Repositories 
 * 
 * [License] 
 * @author John Stewart
 */
public interface EntityRepoI extends BaseDaoI {
	public <T extends BaseEntity> T  save (UserParam params, T entity, EntityManager em) throws Exception;
	public <T extends BaseEntity>T  findById (Class <T> clazz, Long id, EntityManager em) throws Exception;
}
