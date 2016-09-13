package com.sevenorcas.openstyle.mod.docu;

import java.util.List;

import javax.ejb.Local;

import com.sevenorcas.openstyle.app.mod.user.UserParam;
import com.sevenorcas.openstyle.mod.docu.ent.DocumentEnt;
import com.sevenorcas.openstyle.mod.docu.html.DocumentHtml;


/**
 * Local interface to Document Service
 *
 * [License]
 * @author John Stewart
 */

@Local
public interface DocumentService {
	public List<DocumentEnt> list (UserParam userParam, DocumentCtl sql) throws Exception;
	
	public DocumentHtml html (UserParam params, DocumentCtl ctl) throws Exception;
}
