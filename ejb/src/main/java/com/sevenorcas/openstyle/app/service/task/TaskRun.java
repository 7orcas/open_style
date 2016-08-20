package com.sevenorcas.openstyle.app.service.task;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sevenorcas.openstyle.app.service.entity.BaseEntity;
import com.sevenorcas.openstyle.app.service.entity.Field;

/**
 * Record for a each run (completed or terminated) system task.<p>
 *  
 * [License] 
 * @author John Stewart
 */

@SuppressWarnings("serial")
@Entity
@Table(name="task_run", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE_GEN",sequenceName="seq_id_entity",allocationSize=1)
public class TaskRun extends BaseEntity implements Serializable {

	/** ID Field. */   
	@Id  
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE_GEN")
	@Field(appType=FIELD_TYPE_ID)
	protected Long id;
	
	/** Header foreign key */
	@ManyToOne
	@JoinColumn(name="task_id")
	private Task task;

	@Column(insertable=false,updatable=false)
	private Long task_id;

	
	@Field(appType=FIELD_TYPE_LOOKUP_REF, values="1=TaskSN,2=TaskSR,3=TaskSF,4=TaskSFE", readonly=true)
	private Integer status;
	
	@Field(lang=true, label="TaskMess")
	private String message;
	
	@Field(label="RecordCount")
    private Integer records;
	
	/**
	 * Incremental number from parent task
	 */
	@Column(name="run_nr")
	private Integer runNr;
	
	private String programmer;
	
    ////////////////////// Methods //////////////////////////////////	
	
	/**
	 * Constructor
	 */
	public TaskRun() {
	}

	/**
	 * Set <b>this</b> task as finished status
	 */
	public void finished (){
		finished (null, null, null);
	}
	
	/**
     * Set <b>this</b> task as finished status
     * @param String run message
     */
    public void finished (String message){
        finished (message, null, null);
    }
	
	/**
	 * Set <b>this</b> task as finished status
	 * @param String run message
	 * @param String programmers message
	 * @param Number of records processed
	 */
	public void finished (String message, String programmer, Integer records){
		setStatus(TASK_STATUS_FINISHED);
		updated = new Date();
		if (message != null){
			setMessage(message);
		}
		if (programmer != null){
			setProgrammer(programmer);
		}
		if (records != null){
            setRecords(records);
        }
	}
	

	/**
	 * Prevent default update of <b>this</b> field
	 */
	@Override
	public void setUpdated(Date updated) {
		if (this.updated == null){
			this.updated = updated;
		}
	}
	
	
    ////////////////////// Getters / Setters //////////////////////////////////    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getRecords() {
        return records;
    }
    public void setRecords(Integer records) {
        this.records = records;
    }
    public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public String getProgrammer() {
		return programmer;
	}
	public void setProgrammer(String programmer) {
		this.programmer = programmer;
	}
	public Integer getRunNr() {
		return runNr;
	}
	public void setRunNr(Integer runNr) {
		this.runNr = runNr;
	}
	
	
	
}
