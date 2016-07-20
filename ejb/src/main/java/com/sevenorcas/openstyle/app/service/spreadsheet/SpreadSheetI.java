package com.sevenorcas.openstyle.app.service.spreadsheet;

/**
 * Spreadsheet constants.<p>
 * 
 * [License] 
 * @author John Stewart
 */
public interface SpreadSheetI {
    
    final static public int CLASS_INTEGER             = 1;
    final static public int CLASS_DOUBLE              = 2;
    final static public int CLASS_STRING              = 3;
    final static public int CLASS_DATE                = 4;
    final static public int CLASS_LONG                = 5;
    final static public int CLASS_BOOLEAN             = 6;
    final static public int CLASS_CHARACTER           = 7;
    final static public int CLASS_PERCENTAGE          = 8;
    final static public int CLASS_UKURS               = 9;
    final static public int CLASS_INTEGER_LEFT        = 10;
        
    final static public int FORMAT_NORMAL             = 100;
    final static public int FORMAT_BOLD               = 102;
    final static public int FORMAT_HEADER             = 103;

    final static public int ALIGN_UNDEFINED           = 200;
    final static public int ALIGN_LEFT                = 201;
    final static public int ALIGN_RIGHT               = 202;
    final static public int ALIGN_CENTER              = 203;
    final static public int VALIGN_UNDEFINED          = 210;
    final static public int VALIGN_TOP                = 211;
    final static public int VALIGN_CENTER             = 212;
    final static public int VALIGN_BOTTOM             = 213;
    
    final static public int TEXT_WARP_OFF             = 900;
    final static public int TEXT_WARP_ON              = 901;
    final static public int BORDER_OFF                = 910;
    final static public int BORDER_ON                 = 911;
}
