package com.sevenorcas.openstyle.app.spreadsheet;

import javax.ejb.Local;
import javax.ws.rs.core.Response;

import com.sevenorcas.openstyle.app.user.UserParam;

/**
 * Local <code>SpreadSheetService</code> bean interface.
 * @author John Stewart
 */
@Local
public interface SpreadSheetService {
	public String generateFile(UserParam userParam, Object exportObject, String outFilename, String language, String password) throws Exception;
	public Response returnFile(UserParam userParam, String filename, boolean delete) throws Exception;
}
