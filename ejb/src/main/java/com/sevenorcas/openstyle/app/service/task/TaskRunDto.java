package com.sevenorcas.openstyle.app.service.task;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenorcas.openstyle.app.service.dto.BaseDto;
import com.sevenorcas.openstyle.app.service.dto.Dto;
import com.sevenorcas.openstyle.app.service.entity.Field;

/**
 * Task run data transfer object<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Dto(entity="app.task.TaskRun")
public class TaskRunDto extends BaseDto implements Serializable {

	@JsonProperty(value="s")  public Integer status;
	@JsonProperty(value="m")  public String message;
	@JsonProperty(value="x")  public Integer records;
	
	private Date created;
	private Date updated;
	
	
	/**
	 * Default Constructor
	 */
	public TaskRunDto() {
		super(null);
	}
	
	
	/**
	 * Entity Constructor
	 * @param ImportFile entity
	 */
	public TaskRunDto(TaskRun entity) {
		super(entity);
	}
	
	
	/**
	 * Run time in seconds
	 * @return
	 */
	@Field (label="RunTimeMS")
	@JsonProperty(value="r")
	public Long runTime(){
		if (created == null || updated == null){
			return null;
		}
		if (status != TASK_STATUS_FINISHED){
			return null;
		}
		
		long x = created.getTime();
		long y = updated.getTime();
		
		return y - x;
	}
	
	
}
