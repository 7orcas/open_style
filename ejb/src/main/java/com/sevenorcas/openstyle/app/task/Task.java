package com.sevenorcas.openstyle.app.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sevenorcas.openstyle.app.application.ApplicationI;
import com.sevenorcas.openstyle.app.entity.BaseEntity;
import com.sevenorcas.openstyle.app.entity.Field;
import com.sevenorcas.openstyle.app.user.UserParam;


/**
 * The <code>Task</code> class is a control record for a running (or completed) system task.<p>  
 * 
 * Tasks are jobs that can be triggered by user requests, interceptor events (eg work flow action) or timer events.<br>
 * Note also that tasks may be run from a batch job or in-line with a call. 
 *     
 * 
 * [License] 
 * @author John Stewart
 */
@SuppressWarnings("serial")
@Entity
@Table(name="task", schema="cntrl")
@SequenceGenerator(name="ID_SEQUENCE",sequenceName="seq_id_entity",allocationSize=1)
public class Task extends BaseEntity implements Serializable {

	/**
	 * ID Field.<p>
	 * @see ApplicationI
	 */
	@Id
	@Field(appType=FIELD_TYPE_ID)
	@GeneratedValue (strategy=GenerationType.SEQUENCE, generator="ID_SEQUENCE")
	private Long id;
	
	@Field(appType=FIELD_TYPE_KEY, readonly=true)
	private String code;
	
	@Field(appType=FIELD_TYPE_LOOKUP_REF, values="1=TaskSN,2=TaskSR,3=TaskSF,4=TaskSFE,5=TaskSFW", readonly=true)
	private Integer status;
	
	/**
	 * Incremental number within <b>this</b> task
	 */
	@Column(name="run_nr")
	private Integer runNr;
	
	/**
	 * Actual task runs.
	 */
	@Field
	@OneToMany(cascade={CascadeType.ALL},mappedBy="task")
	@OrderBy("created ASC")
	private List <TaskRun> taskruns = new ArrayList<TaskRun>(); 
	
	@Transient
	private long scansImported = 0;
	
	/**
	 * Company configured message (e.g. expected load time)
	 */
	@Transient
	private String message;
	
	////////////////////// Main class methods  //////////////////////////////////
	    
	/**
	* Create role object
	*/
	public Task() {
		super();
	}
	
	/**
	 * Is <b>this<b> task running
	 * @param int minute limit, i.e. if started over the passed in minute limit then task is assumed to have died.<br> 
	 *        Note if minute limit <= 0 then task is never assumed to have died. 
	 * @return true = is running, false = not running
	 */
	public boolean isRunning(int minutes){
		if (status.intValue() != TASK_STATUS_RUNNING){
			return false;
		}
		if (minutes <= 0){
			return true;
		}
		
		long t = created.getTime();
		t = new Date().getTime() - t;
		t = t / 60000;
		
		return t <= minutes;
	}
	
	/**
     * Add a task run
     * @param String run message
     * @param String programmers message
     */
    public TaskRun addRunTask(String message, String programmer){
        return addRunTask(message, programmer, null);
    }
	
	/**
	 * Add a task run
	 * @param String run message
	 * @param String programmers message
	 * @param Number of records processed
	 */
	public TaskRun addRunTask(String message, String programmer, Integer records){
		TaskRun run = new TaskRun();
		run.setStatus(TASK_STATUS_RUNNING);
		run.setMessage(message);
		run.setRecords(records);
		run.setProgrammer(programmer);
		run.setTask(this);
		run.setRunNr(runNr);
		run.setCreated(new Date());
		run.setCreatedId(createdId);
		taskruns.add(run);
		return run;
	}
	
	/**
	 * Set <b>this</b> task as running status
	 * @param UserParam parameters
	 */
	public void run (UserParam params){
		setStatus(TASK_STATUS_RUNNING);
		setCreatedId(params.getUser_id());
		setCreated(new Date());
		setUpdated(null);
		setRunNr(runNr != null? ++runNr : 1);
	}
	
	/**
	 * Set <b>this</b> task as finished status
	 * @param UserParam parameters
	 */
	public void finished (UserParam params){
	    finished (params, TASK_STATUS_FINISHED);
	}
	
	/**
	 * Set <b>this</b> task as finished with a error status
	 * @param UserParam parameters
	 */
	public void finishedWithError (UserParam params){
	    finished (params, TASK_STATUS_FINISHED_ERROR);
	}
	
	/**
     * Set <b>this</b> task as finished with warning status
     * @param UserParam parameters
     */
    public void finishedWithWarn (UserParam params){
        finished (params, TASK_STATUS_FINISHED_WARN);
    }
    
    
    /**
     * Set <b>this</b> task as finished with passed in status
     * @param UserParam parameters
     * @param status
     */
    private void finished (UserParam params, int status){
        setStatus(status);
        setUpdated(new Date());
    }
	
	/**
     * Did <b>this</b> task finish
     */
	@Transient
	public boolean isFinished (){
        return status != null && status == TASK_STATUS_FINISHED;
    }
	
	/**
     * Did <b>this</b> task finish
     */
    @Transient
    public boolean isFinishedWithError (){
        return status != null && status == TASK_STATUS_FINISHED_ERROR;
    }
	
    /**
     * Did <b>this</b> task finish
     */
    @Transient
    public boolean isFinishedWithWarn (){
        return status != null && status == TASK_STATUS_FINISHED_WARN;
    }
    
	////////////////////// Getters / Setters //////////////////////////////////    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<TaskRun> getTaskruns() {
		return taskruns;
	}
	public void setTaskruns(List<TaskRun> taskruns) {
		this.taskruns = taskruns;
	}

	public Integer getRunNr() {
		return runNr;
	}
	public void setRunNr(Integer runNr) {
		this.runNr = runNr;
	}

	public long getScansImported() {
		return scansImported;
	}
	public void setScansImported(long scansImported) {
		this.scansImported = scansImported;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
	
}
