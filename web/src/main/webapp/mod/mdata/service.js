'use strict';

angular.module('mod.mdata.service', [])

    

     /**
      * Mould Map
      */
    .factory('mdataMouldMapRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.mouldMap);
    })
    
    /**
      * Mould Codes
      */
    .factory('mdataMouldCodeRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.mouldCode);
    })
    
    .factory('mdataMouldCodesByGroupRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.mouldCodesByGroup);
    })
    
     /**
      * Moulds export
      */
    .factory('mdataMouldCodesExportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.mouldCodeExport);
    })
    
    
    /**
      * Mould Groups
      */
    .factory('mdataMouldGroupRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.mouldGroup);
    })
    /**
      * Moulds Group export
      */
    .factory('mdataMouldGroupExportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.mouldGroupExport);
    })
    
    
     /**
      * Last Map
      */
    .factory('mdataLastMapRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.lastMap);
    })
    
    /**
      * Last Codes
      */
    .factory('mdataLastCodeRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.lastCode);
    })
    
    .factory('mdataLastCodesByGroupRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.lastCodesByGroup);
    })
    
    
    /**
      * Last Groups
      */
    .factory('mdataLastGroupRemote', function(acRemote, mdataModel) {
    	return acRemote.createRemote (mdataModel.lastGroup);
    })
    
    
    
    /**
      * Machines
      */
    .factory('mdataMachineRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.machine);
    })
    
    
     /**
      * Plants
      */
    .factory('mdataPlantRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.plant);
    })
    
     /**
      * Shifts
      */
    .factory('mdataShiftRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.shift);
    })
    
     /**
      * Categories
      */
    .factory('mdataCategoryRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.category);
    })
    
    /**
      * PU Types
      */
    .factory('mdataPUTypeRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.putype);
    })
    
    
    /**
      * PU Configurations
      */
    .factory('mdataPUConfigRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.puconfig);
    })
   
    /**
      * Production Order Requirement Types
      */
    .factory('mdataReqtypeRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.reqtype);
    })
    
    
     /**
      * Styles
      */
    .factory('mdataStyleRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.style);
    })
    
    
    /**
      * Styles export
      */
    .factory('mdataStyleExportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleExport);
    })
    
    /**
      * Styles
      * Results are not cached
      */
    .factory('mdataStyleLookupRemote', function(acRemote, mdataModel) {
        var remote = acRemote.createRemote (mdataModel.style);
        remote.cacheList = false;
        return remote;
    })
    
    /**
      * Style Machines
      */
    .factory('mdataStyleMachineRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleMachine);
    })
    
    /**
      * Style Materials
      */
    .factory('mdataStyleMaterialRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleMaterial);
    })
    
    /**
      * Styles Materials export
      */
    .factory('mdataStyleMaterialExportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleMaterialExport);
    })
    
    /**
      * Styles BOM import
      */
    .factory('mdataStyleBomImportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleBomImport);
    })
    
    /**
      * Style Attributes
      */
    .factory('mdataStyleAttrRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleAttr);
    })
    
    /**
      * Styles Attributes export
      */
    .factory('mdataStyleAttrExportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleAttrExport);
    })
    
    /**
      * Styles Attributes import
      */
    .factory('mdataStyleAttrImportRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.styleAttrImport);
    })
    
	/**
	  * Attribute process values
	  */
	.factory('mdataAttrProcessRemote', function(acRemote, mdataModel) {
	    return acRemote.createRemote (mdataModel.attributeProcess);
	})
    

     /**
      * Color
      */
    .factory('mdataColorRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.color);
    })    

    
    /**
      * Style (/Color) Attribute Type
      */
    .factory('mdataAttributeTypeRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.attributeType);
    })
    
    
    /**
      * Material Type
      */
    .factory('mdataMaterialTypeRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.materialType);
    })
    
    /**
      * Sizes
      */
    .factory('mdataSizeRemote', function(acRemote, mdataModel) {
        return acRemote.createRemote (mdataModel.size);
    })
    
;


