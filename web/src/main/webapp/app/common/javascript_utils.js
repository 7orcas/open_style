/**
 * @doc module
 * @name javascript utilities
 * @description 
 * 
 * General (common) functions for <u>this</u> application.<p> 
 * 
 * [License]
 * @author various
 */


/**
 * Parse a string to a date.<p>
 * Format must bet day. month. year
 * @param string to parse
 */
function parseStringToDate(string){
    if (!angular.isDefined(string) || string === null){
        return null;
    }
    
    try{
        string = replaceAll(string, '.', ' ');
        string = replaceAll(string, '-', ' ');
        string = replaceAll(string, '/', ' ');
        var parts = string.split(' ');    

        var day   = parseInt(parts[0]);
        var month = parseInt(parts[1]) - 1; //Months are zero based
        var year  = parseInt(parts[2]);
        if (year < 100){
            year = 2000 + year;
        }

        if (testNaN(day)){return false;}
        if (testNaN(month)){return false;}
        if (testNaN(year)){return false;}

        // new Date(year, month [, day [, hours[, minutes[, seconds[, ms]]]]])
        var d = new Date(year, month, day); // Note: months are 0-based
        
        if (testNaN(d.getTime())){return false;}
        return d;
    }
    catch (err){
        return false;
    }

};

/**
 * Regular expressions contain special (meta) characters
 * thanks to http://stackoverflow.com/questions/1144783/replacing-all-occurrences-of-a-string-in-javascript
 */
function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
};

/**
 * Replace all occurances in string
 * thanks to http://stackoverflow.com/questions/1144783/replacing-all-occurrences-of-a-string-in-javascript
 */
function replaceAll(string, find, replace) {
  return string.replace(new RegExp(escapeRegExp(find), 'g'), replace);
};


/*
 * Datepicker labels
 */
var appLanguageCode   = 'en';
var appLanguageMonth  = ['January','February','March','April','May','June','July', 'August', 'September', 'October', 'November', 'December'];
var appLanguageMonthS = ['Jan','Feb','Mar','Apr','May','Jun','Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
var appLanguageDayS   = ['Mo','Tu','We','Th','Fr','Sa','Su'];


/**
 * Customized short days for date picker in ui-bootstrap-tpls-0.10.0.js. 
 * 
 * @param language code
 * @return list of short days
 */
function getCustomerizedShortDaysOfTheWeek(){
    return appLanguageDayS;
};


/**
 * Customized months for date picker in ui-bootstrap-tpls-0.10.0.js. 
 * 
 * @param language code
 * @param month title
 * @return list of months
 */
function getCustomerizedMonthsOfTheYear(title){
    var labels    = null;
    var labels_en = ['January','February','March','April','May','June','July', 'August', 'September', 'October', 'November', 'December'];  

    if (angular.isDefined(appLanguageMonth)){
        labels = appLanguageMonth;
    }
    else{
        labels = labels_en;
    }
    
    for (var i=0; i<labels_en.length; i++){
        if (title.indexOf(labels_en[i]) != -1){
            return labels[i] + title.substring(labels_en[i].length);
        }
    }
    return title;
}

/**
 * Customized language code for date picker in ui-bootstrap-tpls-0.10.0.js. 
 * 
 * @param language code
 * @return validated language code
 */
function getCustomerizedLanguageCode(lang){
    if (angular.isDefined(appLanguageCode)){
        lang = appLanguageCode;
    }
    else {
    	lang = 'de'; 
    }
    return lang;
}


/**
 * Customized language code for date picker in ui-bootstrap-tpls-0.10.0.js. 
 * 
 * @param language code
 * @return validated language code
 */
function getCustomerizedLanguageLabel(labelKey){
	var lang = getCustomerizedLanguageCode();
	
	var labels = ['Today','Weeks','Clear','Done'];
	var labels_de = ['Heute','Wochen','Löschen','Schließen'];
	
	var index = -1;
	for (var i=0; i<labels.length; i++){
		if (labels[i] === labelKey){
			index = i;
			break;
		}
	}
	
	if (index === -1){
		return labelKey;
	}
	
	
	if (lang === 'de'){
		return labels_de[index];
	}
	
    return labelKey;
}


 



/**
 * Test if the passed in value is not a number. 
 * 
 * @param value to test
 * @return true = value is not a number, false = value is a number.
 */
testNaN = function(value){
	try{
        return !(!isNaN(parseFloat(value)) && isFinite(value));    
    } catch (err){
        return true;
    }
};




/** 
* For a given date, get the ISO week number.<p>
*
* Based on information at:<p>
*
*    http://www.merlyn.demon.co.uk/weekcalc.htm#WNR
*
* Algorithm is to find nearest Thursday, it's year
* is the year of the week number. Then get weeks
* between that date and the first day of that year.<p>
*
* Note that dates in one year can be weeks of previous
* or next year, overlap is up to 3 days.<p>
*
* e.g.<ul> 
*     <li>2014/12/29 is Monday in week  1 of 2015</li>
*     <li>2012/1/1   is Sunday in week 52 of 2011</li>
* </ul><p>
*       
* refer: http://stackoverflow.com/questions/6117814/get-week-of-year-in-javascript-like-in-php<p>
* 
* @param Date to find week number for
* @return Array with element [0] = year, [1] = week number 
*/
function getWeekNumber(d) {
   // Copy date so don't modify original
   d = new Date(+d);
   d.setHours(0,0,0);
   // Set to nearest Thursday: current date + 4 - current day number
   // Make Sunday's day number 7
   d.setDate(d.getDate() + 4 - (d.getDay()||7));
   // Get first day of year
   var yearStart = new Date(d.getFullYear(),0,1);
   // Calculate full weeks to nearest Thursday
   var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
   // Return array of year and week number
   return [d.getFullYear(), weekNo];
};




/**
 * Get browser minimum update message (if client is outside defined standard).<p>
 * 
 * A tailored message is returned if:<ul>
 * <li>- client has a defined browser but below minimum version</li>
 * <li>- client has an undefined browser</li><p> 
 * </ul><p>
 * 
 * Thanks to http://stackoverflow.com/questions/5916900/detect-version-of-browser  (Hermann Ingjaldsson)<p>
 * 
 * @return tailored message (if client is outside defined standard) or null if client ok
 */

function getUpdateBrowserMessage(){
    
    //Defined browser standard
    //TODO: Move versions to an application definitions file (to be shared with this login application and actual application) 
    var versions =  [
        {browser:'Chrome', version:33, name:'Chrome'},
        {browser:'Firefox',version:27, name:'Firefox'},
        {browser:'Msie',   version:10, name:'MS Internet Explore'},
       ];   
    

    var get_browser = function(){
        var N=navigator.appName, ua=navigator.userAgent, tem;
        var M=ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
        if(M && (tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
        M=M? [M[1], M[2]]: [N, navigator.appVersion, '-?'];
        return M[0];
    };
    var browser = get_browser(); 


    var get_browser_version = function(){
        var N=navigator.appName, ua=navigator.userAgent, tem;
        var M=ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
        if(M && (tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
        M=M? [M[1], M[2]]: [N, navigator.appVersion, '-?'];
        return M[1];
    };
    var version = get_browser_version();
    
    try{
        version = parseInt(version);
    } catch (err){}


    var found   = false;
    var update  = false;
    var minimum = null;
    
    //Test client browser against standard
    for (var i=0; i<versions.length; i++){
        var b = versions[i];
    
        if (browser.toUpperCase() === b.browser.toUpperCase()){
            found = true;
            update  = version < b.version;
            if (update){
                minimum = b.name + ' v' + b.version;
            }
            break;
        }
    }
    
    if (!found){
        update = true;
        minimum = '';
        
        for (var i=0; i<versions.length; i++){
            var b = versions[i];
            minimum = minimum + (i>0?', ':'') + b.name + ' v' + b.version;  
        }
    }
    
    return minimum;
};
    