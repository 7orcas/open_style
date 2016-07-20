
package com.sevenorcas.openstyle.app.service.repo;

import javax.persistence.EntityManager;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;

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
