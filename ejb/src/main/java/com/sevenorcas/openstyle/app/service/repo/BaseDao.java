package com.sevenorcas.openstyle.app.service.repo;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.sound.midi.Patch;
import javax.sql.DataSource;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.application.ApplicationParameters;
import com.sevenorcas.openstyle.app.application.Utilities;
import com.sevenorcas.openstyle.app.application.exception.AppException;
import com.sevenorcas.openstyle.app.mod.company.Company;
import com.sevenorcas.openstyle.app.mod.user.User;
import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.app.mod.user.UserRole;
import com.sevenorcas.openstyle.app.service.cache.CacheServiceImp;
import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.ConfigI;
import com.sevenorcas.openstyle.app.service.entity.EntityRef;
import com.sevenorcas.openstyle.app.service.entity.ValidationReferenceException;
import com.sevenorcas.openstyle.app.service.log.ApplicationLog;
import com.sevenorcas.openstyle.app.service.perm.NoPermissionException;
import com.sevenorcas.openstyle.app.service.sql.ResultSetX;
import com.sevenorcas.openstyle.app.service.sql.StatementX;
import com.sevenorcas.openstyle.app.service.task.Task;
import com.sevenorcas.openstyle.app.service.task.TaskRun;



/**
 * Base Repository<p>
 * 
 * Contains common methods for implementing classes. Also annotated with <code>@AroundInvoke</code>. This is particularly important for extraneous methods
 * such as caching, task management and work-flow.<p> 
 * 
 * [License] 
 * @author John Stewart
 */

@MappedSuperclass
public abstract class BaseDao implements ApplicationI {

	protected ApplicationParameters appParam = ApplicationParameters.getInstance();
	protected CacheServiceImp cache = CacheServiceImp.getInstance();

	//Table name definitions
	public static String T_COMPANY                   = tableName(Company.class);
	public static String T_USER                      = tableName(User.class);
	public static String T_USER_ROLE                 = tableName(UserRole.class);
	public static String T_PATCH                     = tableName(Patch.class);
	public static String T_TASK                      = tableName(Task.class);
	public static String T_TASK_RUN                  = tableName(TaskRun.class);
	
	
//	/**
//	 * Persistence context corresponds to the persistence-unit in 
//	 * ejb/src/main/resources/META-INF/persistence.xml
//	 * 
//	 * For multiple contexts see http://www.hostettler.net/blog/2012/11/20/multi-tenancy/ (this in not yet implemented)
//	 */
//	@PersistenceContext (unitName = "openstyleDS")
//	protected static EntityManager em;
	
	
	/**
	 * Default Constructor
	 */
	public BaseDao(){}
	
	
	/**
	 * Validate if the user has service permission to change the passed in entity (i.e. can perform updates or deletes).
	 * @param UserParam object
	 * @param BaseEntity object
	 * @throws NoPermissionException if user is not service
	 */
	public void validateServiceUser (UserParam params, Object obj) throws NoPermissionException{

		if (!Utilities.validateServiceUser(params, obj)){
			StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
			throw new NoPermissionException(stElements[2].getMethodName());
		}
	}
	
	/**
	 * Save / update / delete an entity along with its child entities.<p>
	 * 
	 * TODO: validation
	 * 
	 * @param UserParam object
	 * @param Entity T extends BaseEntity
	 * @return
	 */
	public <T extends BaseEntity> T  save (UserParam params, T entity, EntityManager em) throws Exception{
		
    	if (entity.isDelete()){
    		remove (params, entity, em);
    		return null;
    	}
    	
    	//Keep transient field
    	Long id_dto = entity.getId_dto();

    	//Must be a new record
    	if (entity.getId() != null && entity.getId() < 0){
    		entity.setId(null);
    	}
    	
    	deleteChild (params, entity, em);
		
		setStandardFields(params, entity);

		if (entity instanceof ConfigI){
            ConfigI c = (ConfigI)entity;
            c.setConfig(c.encode());
        }
		
		entity = em.merge(entity);
		entity.setId_dto(id_dto);
		
		if (entity instanceof ConfigI){
            ConfigI c = (ConfigI)entity;
            c.decode(c.getConfig());
        }
		
		return entity;
	}
	
	
	/**
	 * Delete child entities.<p>
	 * 
	 * This is a recursive method, working along the object scope
	 * 
	 * @param UserParam object
	 * @param Entity T extends BaseEntity
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private void deleteChild (UserParam params, Object entity, EntityManager em) throws Exception{
		
    	List<java.lang.reflect.Field> fields = cache.getFieldList(entity.getClass(), true);
    	java.lang.reflect.Field parent = null;
    	
		//Iterate fields looking for child lists. Once found then check for deletes
		for (java.lang.reflect.Field field: fields) {
			try {
				Object obj = field.get(entity);
				if (obj instanceof List){
					OneToMany a = field.getAnnotation(OneToMany.class);
					if (a == null){
						continue;
					}
					
					
					//Iterate child list and look for deletes
					parent = null;
					List<java.lang.reflect.Field> childFields = null;
					boolean deleteChild = false;
					
					List list = (List)obj;
					ArrayList<BaseEntity> deletes = new ArrayList<>();
					for (Object ent: list){
						
						
						//Look in child list for more children
						if (childFields == null){
							childFields = cache.getFieldList(ent.getClass(), true);
							Object x = Utilities.findFieldAnnontation(OneToMany.class, childFields);
							deleteChild = x != null;
						}
						
						if (deleteChild){
							deleteChild (params, ent, em);
						}
						
						if (ent instanceof BaseEntity){
							BaseEntity child = (BaseEntity)ent;
							if (child.isDelete()){
								deletes.add(child);
								
								//find parent field
								if (parent == null){
									String parentName = entity.getClass().getName();
									parentName = parentName.substring(parentName.lastIndexOf(".") + 1);
									parent = Utilities.findFieldAnnontation(ManyToOne.class, childFields); 
								}
							}
						}
					}
					
					for (BaseEntity child: deletes){
						parent.set(child, null);
						list.remove(child);
			    		remove(params, child, em);
			    	}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
	}
	
	
	/**
	 * Iterate through the passed in entity graph and set standard fields, i.e.<ul>
	 *     <li><code>comp_nr</code> if new record</li>
	 *     <li><code>active</code> = true if new record</li>
	 *     <li><code>create</code> and <code>create_id</code> if new record</li> 
	 *     <li><code>update</code> and <code>update_id</code> if editing record</li>
	 * </ul><p>
	 *  
	 * @param UserParam object
	 * @param Entity T extends BaseEntity
	 */
	public void setStandardFields(UserParam params, Object ent){
		Utilities.setStandardFields(params, ent);
	}
	
	
	/**
	 * Remove an entity
	 * 
	 * TODO: Log
	 * 
	 * @param UserParam object
	 * @param Entity T extends BaseEntity
	 * @throws NoPermissionException if service user is required (but user don't have status)
	 * @return
	 */
	public void  remove (UserParam params, Object entity, EntityManager em)throws Exception{
		validateServiceUser(params, entity);
		validateReferences(params, entity);
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}
	

	/**
	 * Validate if the passed in entity is referenced by another entity (i.e. database table).<p>
	 * 
	 * Note this method assumes the referencing table column is called <code>[passed in entity name]_id</code>.<p>
	 * 
	 * @param UserParam object
	 * @param BaseEntity object
	 * @throws AppException if referenced
	 */
	@SuppressWarnings("rawtypes")
	public void validateReferences (UserParam params, Object obj) throws Exception{

		BaseEntity entity = null;
		if (obj instanceof BaseEntity){
			entity = (BaseEntity)obj;
		}
		else{
			return;
		}
		
		EntityRef a = entity.getClass().getAnnotation(EntityRef.class);
		if (a == null || a.entities().length() == 0){
			return;
		}
		
		String entityTable = tableName(entity.getClass());
		
		StringBuffer sb = new StringBuffer();
		int count = 0;
		
		String [] names = a.entities().split(",");
		for (String name: names){
			Class clazz = Utilities.findClass(name);
			String table = tableName(clazz);
			String langK = tableLangKey(clazz);
			
			ResultSetX rs = StatementX
					.create("SELECT t." + entityTable + "_id FROM " + table + " t " 
						  + "WHERE t." + entityTable + "_id = " + entity.getId())
					.executeQuery(null);
			
			if (rs.count() > 0){
				count++;
				sb.append((count > 1?",":"") + langK);
			}
		}
		
		if (count > 0){
			throw new ValidationReferenceException(sb.toString());
		}
	}

	
	/**
	 * Find an entity by its ID
	 * @param Long id
	 * @return
	 */
	public <T extends BaseEntity>T  findById (Class <T> clazz, Long id, EntityManager em)  throws Exception{
	    T t = em.find(clazz, id);
	    if (t != null && t instanceof ConfigI){
	        ConfigI c = (ConfigI)t;
            c.decode(c.getConfig());
        }
		return t;
	}
	
	
	/**
     * Retrieve connection to datasource as defined in <code>ApplicationParameters</code>.<p>
     * 
     * NOTE: Close the connection !!<p>
     * 
     * @return Connection
     */
    public Connection getJDBCConnection() throws Exception{
    	return getJDBCConnection(appParam.getPostgresDatasource());
    }

    /**
     * Retrieve connection to datasource as defined in passed in value.
     * 
     * @see http://www.mastertheboss.com/jboss-datasource/how-to-configure-a-datasource-with-jboss-7
     * @param String datasource
     * @return Connection
     */
    static public Connection getJDBCConnection(String datasource) throws Exception{
		try {
			DataSource ds = (DataSource) new InitialContext().lookup(datasource);
			return ds.getConnection();
		} catch (Exception e) {
			ApplicationLog.error(e);
			throw e;
		}
    }
    
    
    
    /**
     * Close the passed in sql objects.
     * 
     * @see http://www.mastertheboss.com/jboss-datasource/how-to-configure-a-datasource-with-jboss-7
     * @param ResultSet
     * @param Statement
     * @param Connection
     */
    public void close(ResultSet resultSet, Statement statement, Connection connection) {
    	if (resultSet != null){
	    	try {
	    		resultSet.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    	if (statement != null){
	    	try {
	    		statement.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    	if (connection != null){
	    	try {
	    		connection.close();
			} catch (Exception e) {
				ApplicationLog.error(e);
			}
	    }
    }
    
    
    /**
	 * Return the annotation <code>@javax.persistence.Table</code> element <code>name</code> if it exists (and is not empty). 
	 * Otherwise return the simple class name.<p>
	 * 
	 * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
	 * 
	 * @param Class of java object
	 * @return Full table name with schema
	 */
	@SuppressWarnings("rawtypes")
	static public String tableName(Class clazz){
		return Utilities.tableName(clazz);
	}
	
	/**
     * Return the annotation <code>@EntityAttributes</code> element <code>langKey</code> if it exists (and is not empty). 
     * Otherwise return the table name.<p>
     * 
     * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
     * 
     * @param Class of java object
     * @return Full table name with schema
     */
    @SuppressWarnings("rawtypes")
    static public String tableLangKey(Class clazz){
        return Utilities.tableLangKey(clazz);
    }
	
	
	 /**
		 * Return the annotation <code>@javax.persistence.Column</code> element <code>name</code> if it exists (and is not empty). 
		 * Otherwise return the field name.<p>
		 * 
		 * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
		 * 
		 * @param Class of java object
		 * @param String field name
		 * @return Table-Field name
		 */
		@SuppressWarnings("rawtypes")
		public String columnName(Class clazz, String fieldname){
			
			String name = cache.getTableColumnName(clazz, fieldname);
			if (name != null){
				return name;
			}
			name = getTableColumnName(clazz, fieldname);
			if (name == null){
				name = fieldname;
			}
			cache.putTableColumnName(clazz, fieldname, name);
			
			return name;
		}
		
		/**
		 * Return the annotation <code>@javax.persistence.Column</code> element <code>name</code> if it exists (and is not empty). 
		 * Otherwise return the field name.<p>
		 * 
		 * This method uses reflection which is quite heavy. Therefore lookups are cached for performance.
		 * 
		 * @param Class of java object
		 * @param String field name
		 * @return Table-Field name
		 */
		@SuppressWarnings("rawtypes")
		static public String getTableColumnName(Class clazz, String fieldname){
		
			Field[] list = clazz.getDeclaredFields();
			for (Field f: list){
				f.setAccessible(true);
				if (f.getName().equals(fieldname)){
					Column c = f.getAnnotation(Column.class);
					if (c != null && c.name().length() > 0){
						return c.name();
					}		
					return fieldname;
				}
			}
			
			if (clazz.getSuperclass() != null){
				return getTableColumnName(clazz.getSuperclass(), fieldname);
			}
			
			return null;
		}
	
		
		/**
		 * Get the next temporary id number
		 * @return
		 * @throws Exception
		 */
		public long nextTempId () throws Exception{
			return nextId (SEQUENCE_TEMP);
		}
	    
		
		/**
		 * Get the next report id number
		 * @return
		 * @throws Exception
		 */
		public long nextReportId () throws Exception{
			return nextId (SEQUENCE_REPORT);
		}

		/**
		 * Get the next language id number
		 * @return
		 * @throws Exception
		 */
		public long nextLanguageId () throws Exception{
			return nextId (SEQUENCE_LANGUAGE);
		}

		/**
		 * Get the next entity id number
		 * @return
		 * @throws Exception
		 */
		public long nextEntityId () throws Exception{
			return nextId (SEQUENCE_ENTITY);
		}
		
		
		/**
		 * Get the number from passed in sequence
		 * @param String sequence name
		 * @return
		 * @throws Exception
		 */
		protected int nextNr (String sequence) throws Exception{
			return (int)nextId(sequence);
		}
		
		/**
		 * Get the id number
		 * @param String sequence name
		 * @return
		 * @throws Exception
		 */
		protected long nextId (String sequence) throws Exception{
			ResultSetX rs = StatementX.create("SELECT NEXTVAL('" + sequence + "')").executeQuery(null);
			while(rs.next()){
				return rs.getLong(1);
			}
			AppException ex = new AppException("Can't get NEXTVAL('" + sequence + "')");
			ex.logThisException();
			ex.emailThisException();
			throw ex;
		}

		
		/**
		 * Format the passed in date for SQL
		 * @param Date to format
		 * @return formatted date
		 */
		protected String formatSqlDate(Date date){
			return Utilities.formatSqlDate(date);
		}

		
		
}
