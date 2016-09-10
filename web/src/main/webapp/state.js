'use strict';


/**
 * Page Configurations
 *    u = client url
 *    t = templateUrl
 *    c = controller
 *    m = model
 *    r = remote
 *    p = remote parameters
 */
var pgConfig = {};

//System standard pages
pgConfig.home               = {u:'/home',               t:'main_monitor.html',                         c:'mainCtrl'};
pgConfig.relogin            = {u:'/relogin',            t:'template/app/relogin.html',                 c:'reloginCtrl'};
pgConfig.remoteException    = {u:'/exception',          t:'template/app/exception.html',               c:'exceptionCtrl'};
pgConfig.clearPage          = {u:'/clearpage',          t:'template/app/clear_page.html',              c:'clearPageCtrl'};
pgConfig.userChangePassword = {u:'/changepassword',     t:'app/mod/user/view/change_password.html',    c:'userChangePasswordCtrl',  m:'userModel.changePassword', r:'userChangePasswordRemote'};


pgConfig.showDoc            = {u:'/doc',                t:'mod/doc/view/doc.html',                     c:'docCtrl',                 m:'docModel.doc',                r:'docRemote'};








//service:start
pgConfig.langAdmin          = {u:'/lang/all',           t:'app/mod/langadmin/view/lang_admin.html',    c:'langAdminCtrl',           m:'langadminModel.list',         r:'langadminRemote'};
pgConfig.userAdmin          = {u:'/userAdmin',          t:'app/mod/useradmin/view/user_admin.html',    c:'userAdminCtrl',           m:'useradminModel.userAdmin',    r:'useradminUsersRemote'};
pgConfig.companyAdmin       = {u:'/companyAdmin',       t:'app/mod/company/view/company.html',         c:'companyAdminCtrl',        m:'companyModel.list',           r:'companyadminRemote'};
pgConfig.patches            = {u:'/patches',            t:'app/mod/patches/view/patches.html',         c:'patchesCtrl',             m:'patchesModel.list',           r:'patchesRemote'};
pgConfig.dataImport         = {u:'/configImport',       t:'app/mod/configimport/view/import.html',     c:'configImportCtrl',        m:'configImportModel.import',    r:'configImportRemote'};
//service:end

//Application pages
pgConfig.mouldGroup         = {u:'/mouldgroup',         t:'mod/mdata/view/mould_group_list.html',      c:'mdataMouldGroupCtrl',     m:'mdataModel.mouldGroup',       r:'mdataMouldGroupRemote'};
pgConfig.mouldMap           = {u:'/mouldmap',           t:'mod/mdata/view/mould_map.html',             c:'mdataMouldMapCtrl',       m:'mdataModel.mouldMap',         r:'mdataMouldMapRemote'};
pgConfig.mouldCodeList      = {u:'/mouldcodelist',      t:'mod/mdata/view/mouldcode_list.html',        c:'mdataMouldCodeCtrl',      m:'mdataModel.mouldCode',        r:'mdataMouldCodeRemote',   p:null};
pgConfig.lastGroup          = {u:'/lastgroup',          t:'mod/mdata/view/last_group_list.html',       c:'mdataLastGroupCtrl',      m:'mdataModel.lastGroup',        r:'mdataLastGroupRemote',   p:{mg:true}};
pgConfig.lastMap            = {u:'/lastmap',            t:'mod/mdata/view/last_map.html',              c:'mdataLastMapCtrl',        m:'mdataModel.lastMap',          r:'mdataLastMapRemote'};
pgConfig.lastCodeList       = {u:'/lastcodelist',       t:'mod/mdata/view/lastcode_list.html',         c:'mdataLastCodeCtrl',       m:'mdataModel.lastCode',         r:'mdataLastCodeRemote',    p:null};
pgConfig.machineList        = {u:'/machineList',        t:'mod/mdata/view/machine_list.html',          c:'mdataMachineCtrl',        m:'mdataModel.machine',          r:'mdataMachineRemote'};
pgConfig.puConfig           = {u:'/puconfigList',       t:'mod/mdata/view/puconfig_list.html',         c:'mdataPUConfigCtrl',       m:'mdataModel.puconfig',         r:'mdataPUConfigRemote'};

pgConfig.plantList          = {u:'/plantList',          t:'mod/mdata/view/plant_list.html',            c:'mdataPlantCtrl',          m:'mdataModel.plant',            r:'mdataPlantRemote'};
pgConfig.categoryList       = {u:'/categoryList',       t:'mod/mdata/view/category_list.html',         c:'mdataCategoryCtrl',       m:'mdataModel.category',         r:'mdataCategoryRemote'};
pgConfig.shiftList          = {u:'/shiftList',          t:'mod/mdata/view/shift_list.html',            c:'mdataShiftCtrl',          m:'mdataModel.shift',            r:'mdataShiftRemote'};
pgConfig.styleList          = {u:'/styleList',          t:'mod/mdata/view/style_list.html',            c:'mdataStyleCtrl',          m:'mdataModel.style',            r:'mdataStyleRemote'};
pgConfig.styleLast          = {u:'/styleLast',          t:'mod/mdata/view/style_last.html',            c:'mdataStyleCtrl',          m:'mdataModel.style',            r:'mdataStyleRemote'};
pgConfig.styleMachineList   = {u:'/styleMachineList',   t:'mod/mdata/view/style_machine_list.html',    c:'mdataStyleMachineCtrl',   m:'mdataModel.styleMachine',     r:'mdataStyleMachineRemote'};
pgConfig.styleMaterialList  = {u:'/styleMaterialList',  t:'mod/mdata/view/style_material_list.html',   c:'mdataStyleMaterialCtrl',  m:'mdataModel.styleMaterial',    r:'mdataStyleMaterialRemote'};
pgConfig.styleAttrList      = {u:'/styleAttrList',      t:'mod/mdata/view/style_attr_list.html',       c:'mdataStyleAttrCtrl',      m:'mdataModel.styleAttr',        r:'mdataStyleAttrRemote'};
pgConfig.colorList          = {u:'/colorList',          t:'mod/mdata/view/color_list.html',            c:'mdataColorCtrl',          m:'mdataModel.color',            r:'mdataColorRemote'};
pgConfig.attributetypeList  = {u:'/attributetypeList',  t:'mod/mdata/view/attributetype_list.html',    c:'mdataAttributeTypeCtrl',  m:'mdataModel.attributeType',    r:'mdataAttributeTypeRemote'};
pgConfig.materialtypeList   = {u:'/materialtypeList',   t:'mod/mdata/view/materialtype_list.html',     c:'mdataMaterialTypeCtrl',   m:'mdataModel.materialType',     r:'mdataMaterialTypeRemote'};
pgConfig.sizeList           = {u:'/sizeList',           t:'mod/mdata/view/size_list.html',             c:'mdataSizeCtrl',           m:'mdataModel.size',             r:'mdataSizeRemote',        p:null};
pgConfig.reqtypeList        = {u:'/reqtypeList',        t:'mod/mdata/view/reqtype_list.html',          c:'mdataReqtypeCtrl',        m:'mdataModel.reqtype',          r:'mdataReqtypeRemote'};
pgConfig.putypeList         = {u:'/putypeList',         t:'mod/mdata/view/putype_list.html',           c:'mdataPUTypeCtrl',         m:'mdataModel.putype',           r:'mdataPUTypeRemote'};

pgConfig.importLogi         = {u:'/importLogi',         t:'mod/import/view/logi_import.html',          c:'importLogiCtrl',          m:'importModel.importlogi',      r:'importLogiRemote'};
pgConfig.importFile         = {u:'/importFile',         t:'mod/import/view/file_import.html',          c:'importFileCtrl',          m:'importModel.importfile',      r:'importFileRemote'};
pgConfig.calendarList       = {u:'/calendarList',       t:'mod/calendar/view/calendar_map.html',       c:'calendarMapCtrl',         m:'calendarModel.calendarMap',   r:'calendarMapRemote'};
pgConfig.prep               = {u:'/prep',               t:'mod/prep/view/selection.html',              c:'prepCtrl',                m:'prepModel.prepselection',     r:'prepRemote'};
pgConfig.simu               = {u:'/simu',               t:'mod/simu/view/selection.html',              c:'simuCtrl',                m:'simuModel.simuselection',     r:'simuRemote'};
pgConfig.period             = {u:'/period',             t:'mod/period/view/selection.html',            c:'periodCtrl',              m:'periodModel.periodSelection', r:'periodRemote'};
pgConfig.simuEngine2        = {u:'/simuEngine2',        t:'mod/simu/view/engine2.html',                c:'simuEngine2Ctrl',         m:'simuModel.simuengine2prep',   r:'simuEngine2PrepRemote'};
pgConfig.prepMatRpt         = {u:'/prepmatrpt',         t:'mod/rpt/view/prepmat.html',                 c:'prepMatRptCtrl',          m:'rptModel.prepMat',            r:'rptPrepMatRemote'};
pgConfig.fixList            = {u:'/fixList',            t:'mod/fix/view/selection.html',               c:'fixListCtrl',             m:'fixModel.list',               r:'fixListRemote'};
pgConfig.startMoulds        = {u:'/startmoulds',        t:'mod/simu/view/startmould_list.html',        c:'simuStartMouldCtrl',      m:'simuModel.startMould',        r:'simuStartMouldRemote'};
pgConfig.simuconfig         = {u:'/simuconfig',         t:'mod/simu/view/config.html',                 c:'simuConfigCtrl',          m:'simuModel.config',            r:'simuConfigRemote'};
pgConfig.calendarMap        = {u:'/calendarmap',        t:'mod/calendar/view/calendar_map.html',       c:'calendarMapCtrl',         m:'calendarModel.calendarMap',   r:'calendarMapRemote',      p:null};
pgConfig.exportFix          = {u:'/exportFix',          t:'mod/export/view/export.html',               c:'exportCtrl',              m:'exportModel.exportselection', r:'exportRemote'};


/**
 * @doc model
 * @name appx
 * @description Application js
 * 
 */
var app = angular.module('app');


/**
 * Configuration of application routes 
 */
app.config(['$stateProvider', function ($stateProvider) {
    
    //Add pages to state provider
    for (var c in pgConfig){

        //Add in parameter attribute
        if (!angular.isDefined(pgConfig[c].p)){
            pgConfig[c].p = null;
        }

        //control to prevent multiple firing if dependencies can't be loaded
        pgConfig[c].fire = false;
        
        $stateProvider.state(c, {url: pgConfig[c].u, templateUrl: pgConfig[c].t, controller: pgConfig[c].c});
    }

}]);


/**
 * <b>This</b> module intercepts state changes to:<ul>
 *     <li>test if model / data is in cache</li>
 *     <li>manage errors and exceptions with redirections</li>
 * </ul>
 */
app.run(['$injector','$state', '$rootScope', '$interval', 'acGlobal', 'acModel', 'acCache', 'acDefinition', 
          function($injector, $state, $rootScope, $interval, acGlobal, acModel, acCache, acDefinition) {

    $rootScope.pgConfig = pgConfig;
    

    /**
     * Listener for state changes. Checks is model and/or objects need to be loaded prior to the controller/page load.
     */
    $rootScope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
        
        if ($rootScope.relogin){
            e.preventDefault();
            return;
        }
        
        //Get passed in parameters
        if (pgConfig[toState.name].p !== null){
            toParams = pgConfig[toState.name].p;
        }

        
        //Get model
        var model = null;
        if (angular.isDefined(toState.model)){
            model = toState.model;
        }
        else if (angular.isDefined(pgConfig[toState.name].m)){
            var js   = pgConfig[toState.name].m;
            var attr = null;

            var i = js.indexOf('.');
            if (i > -1){
                js   = pgConfig[toState.name].m.substring(0, i);
                attr = pgConfig[toState.name].m.substring(i + 1);
            }
            model = $injector.get(js);

            if (attr !== null){
                model = model[attr];
            }

            toState.model = model;
        }
        else {
            window.scrollTo(0, 0);
            return;
        }

        
        //Test if the model and / or the objects are in cache. If not then fire the REST method(s) to get them.
        //These objects MUST be in cache. Objects that are not to be cached will be removed via the controller.
        var fire = false;
        if (model.model !== null && (!acCache.isModel(model.model) || !acCache.isCached(model.model))){
            fire = true;
        }
        else if (angular.isDefined(model.modelSql) && !acCache.isModel(model.modelSql)){
            fire = true;   
        }

        if (fire && pgConfig[toState.name].fire === true){
            e.preventDefault();
            $state.go('remoteException');
            window.scrollTo(0, 0);
            return;
        }
        
        
        if (fire){
            
            pgConfig[toState.name].fire = true;
            
            var remote = null;
            if (angular.isDefined(toState.remote)){
                remote = toState.remote;
            }
            else{
                remote = $injector.get(pgConfig[toState.name].r);
                toState.remote = remote;
            }

            fireRest (e, toState, model, toParams, remote);
        }
        
        window.scrollTo(0, 0);
        
    });


    
    //Return status (as defined in Java ApplicationI)
    var RETURN_STATUS_MAX_ROWS = acGlobal.globals().RETURN_STATUS_MAX_ROWS;
    var RETURN_STATUS_INVALID  = acGlobal.globals().RETURN_STATUS_INVALID;
    var RETURN_STATUS_NO_PERM  = acGlobal.globals().RETURN_STATUS_NO_PERM;
    
    /**
     * Standard error callback
     */
    var error = function (result){
        switch(result.getStatusId()){
            case RETURN_STATUS_MAX_ROWS:
                $state.go("remoteException");
                break;
            case RETURN_STATUS_INVALID:
                if (angular.isFunction($rootScope.setValidationMessage)){
                    $rootScope.setValidationMessage(result.getErrorHtml());
                }
                $state.go("remoteException");
                break;
            case RETURN_STATUS_NO_PERM:
                $state.go("remoteException");
                break;
            default:
                $state.go("remoteException");
        }
    };


    /**
     * Fire the REST method
     */
    var fireRest = function(e, toState, model, params, remote){
        e.preventDefault();

        var load = [];

        //Load dependencies first (remotes for loading lists)
        if (angular.isDefined(model.dependencies)){
            for(var i=0; i<model.dependencies.length; i++){
                var rem = $injector.get(model.dependencies[i]);
                load[i] = rem.query();
            }
        }
        
        //Load model dependencies, ie other models that is state requires
        if (angular.isDefined(model.model_dependencies)){
            for(var i=0; i<model.model_dependencies.length; i++){
                load.push(acDefinition.loadModel(model.model_dependencies[i]));
            }
        }
        
        //Load sql model
        if (angular.isDefined(model.modelSql) && !acCache.isModel(model.modelSql)){
            load.push(acDefinition.loadSql(model.modelSql));
        }


        var stopTime = $interval(function() {
            //Test if dependencies are loaded
            var fire = true;
            for(var i=0; i<load.length; i++){
                if (!load[i]()){
                    fire = false;
                }
            }

            if (fire){
                $interval.cancel(stopTime);
                
                if (angular.isDefined(params)){
                    //do nothing
                }
                else if (angular.isDefined(model.params)){
                    params = model.params();
                }
                
                remote.query(params,
                    function(result){
                        if (result.isOk()){
                            pgConfig[toState.name].fire = false;
                            $state.go(toState);
                        }
                    }, error);
            }
        }, 100);
    };
    
 
    
}]);