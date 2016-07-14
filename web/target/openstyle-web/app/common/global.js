'use strict';

/**
 * @doc module
 * @name app.common.global
 * @description 
 * 
 * Central repository for global parameters along with common functions.<p> 
 * 
 * The design goal of <b>this</b> module is to provide a central repository (and hence easier management) for storing 
 * application configurations. I.e. application wide parameters and methods are defined within <b>this</b> module.<p>
 * 
 * Design notes:<ul>
 *     <li>objects within this module are changeable, e.g. at login initialization according to user predefined configuration.</li><br>
 *     <li><b>this</b> module is referenced by almost all other modules.</li><br>
 *     <li><b>this</b> module controls the visual loading animation (i.e. a animation that locks out user input, necessary for
 *         actions that are awaiting a server response).</li><br>
 * </ul>
 * 
 * [License]
 * @author John Stewart
 */
angular.module('app.common.global', [])

    .service('acGlobal', function($rootScope) {
        var global = {};
        
        global.company         = 0; //DEFAULT_COMPANY (0 === not used)
        global.companyCode     = null;
        global.testCompany     = false;
        
        global.site            = null;
        global.language        = null;
        global.decimalSymbol   = '.';

        //default date formatting
        global.timezoneOffset     = 0;
        global.dateFormat         = 'dd.MMM.yy';
        global.dateFormatDto      = 'dd.MM.yy';
        global.dateFormatMonth    = 3;
        global.daysShortDefault   = ['Mo','Tu','We','Th','Fr','Sa','Su',];
        global.daysShort          = global.daysShortDefault; 
        global.monthsShortDefault = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        global.monthsShort        = global.monthsShortDefault;
        global.months             = ['January','February','March','April','May','June','July','August','September','October','November','December'];

        //Default id field 'dto' name (as defined in BaseDto)
        global.idFieldname     = 'i';
        
        global.loading         = false;
        global.loadingCount    = 0;
        global.loadingErrorMessageDisplayed = false;
        
        global.indexPage       = null;
        global.remoteUrlPrefix = 'rest/';
        global.helpUrlRoot     = '';
        
        global.systemErr           = null;
        global.missingDescription  = '?';
        global.isChangedFn         = null;
        global.heartbeatTimer      = null;
        global.sessionTimeoutTimer = null;
        
        global.temp_prefix = 'init_tempfield_';
        
        //Allow control of enter key (eg a dialog can prevent default enter key submit action via this method)
        //Note: don't forget to reset on $scope.destroy
        global.preventEnterKeyDefault = false;
        var preventEnterKey = function(event){
             if (event.which == '13' && global.preventEnterKeyDefault) {
                 event.preventDefault();
             }
        };
        $(document).keypress(preventEnterKey);
        
        
        //Current login
        global.userid = "??";

        
        //ReturnDto status (as defined in ApplicationI)
        global.RETURN_STATUS_UNKNOWN  = -1; //return object is not a ReturnDto
        global.RETURN_STATUS_OK       = 0;
        global.RETURN_STATUS_WARNING  = 1;
        global.RETURN_STATUS_ERROR    = 2;
        global.RETURN_STATUS_MAX_ROWS = 3;
        global.RETURN_STATUS_INVALID  = 4;
        global.RETURN_STATUS_EXISTS   = 5;      
        global.RETURN_STATUS_NO_PERM  = 6;
            
            
        //Application specific

        
        
        
        
        
        //Centralize id sequence number 
        var nextId = 0;
        
        var self = {
                
            /** 
             * Return <b>this</b> objects application configurations 
             */ 
            globals: function (){return global;},
                
            /**
             * Is the current user logged into the server application (if so they will have
             * a valid <code>acUserObj</code>
             */
            testLoginStatus: function (userObj){
                    if (!angular.isDefined(userObj) || !userObj.isLogin()) {
                        $rootScope.$broadcast('event:loginRequired');
                        return false; 
                    }  
                    return true;
                },
              
            /**
             * Return if the visual loading animation is active (i.e. client is waiting an outstanding(s) call)
             */    
            isLoading: function(){ return global.loading;},

            /**
             * Remote calls (by default) will make visible a visual loading animation to indicate to the
             * user the client is busy. Multiple remote calls may be active, hence a cumulative 
             * <code>loadingCount</code> variable is used to control the animation pending a response
             * for all active calls.
             */    
            addLoading: function(){
                    global.loadingCount = global.loadingCount + 1;
                    if (!global.loading){
                        global.loading = true;
                        global.loadingErrorMessageDisplayed = false;
                        var el = document.getElementById("processing");
                        if (el !== null){
                            el.style.visibility = "visible";
                        }
                    }
                },
            
            /**
             * Remote calls (by default) will make visible a visual loading animation to indicate to the
             * user the client is busy. Note that <code>loadingCount</code> variable is not used.
             */    
            addLoadingNoReg: function(){
                    var el = document.getElementById("processing");
                    if (el !== null){
                        el.style.visibility = "visible";
                    }
                },                
                
            /**
             * Clear the <code>loadingCount</code> variable and remove the visual loading animation. This
             * may be necessary after a time-out is reached for a remote server call.
             * TODO: Implement a default server timeout in remote module (needs to be a graceful as possible) 
             */ 
            removeLoadingForce: function(){
                    global.loadingCount = 0;
                    self.removeLoading();
            },
            
            /**
             * Once the server response arrives from a call, <b>this</b> method is called to decrement the 
             * <code>loadingCount</code> variable. Once <code>loadingCount</code> is 0 it is assumed that
             * all outstanding remote server calls are complete and the visual loading animation can be 
             * removed (allowing normal user activity). 
             */
            removeLoading: function(){
                    global.loadingCount = global.loadingCount - 1;
                    if (global.loadingCount < 0){
                        global.loadingCount = 0;
                    }
                    if (global.loadingCount === 0){
                        global.loading = false;
                        
                        var el = document.getElementById("processing");
                        if (el !== null){
                            el.style.visibility = "hidden";
                        }
                    }
                },
                
             /**
              * Register a function to test if unsaved changes exist. Used to control if user can 
              * navigate away from page. 
              */
             addIsChangedFn: function(l){
                    global.isChangedFn = l;
                },
                
            /**
              * De-register the unsaved changes exist function (i.e. it is no longer relevant). 
              */   
             removeIsChangedFn: function(){
                    global.isChangedFn = null;
                },
                
             /**
              * Return if there are unsaved changes. Used to control if user can navigate away from page. 
              */   
             isChanged: function(){
                    if (global.isChangedFn !== null && angular.isFunction(global.isChangedFn)){
                        return global.isChangedFn();
                    }
                    return false;
                },
             
            /**
              * Show message to user that there are unsaved changes.  
              */   
             showIsChanged: function(message){   
                    alert(message);
                },
               
             /**
              * If passed in value = true, then prevent enter key from submitting a form
              * (e.g. used in dialog that requires multiple field entries)
              */   
             preventEnterKeyDefault: function (value){
                    global.preventEnterKeyDefault = value;
                },
                
             /**
              * Get the next centralize id sequence number
              */   
             getNextId: function(){
                    nextId = nextId + 1;
                    return nextId; 
                },
                
           
        };
        
        return self;
    })
    

;         