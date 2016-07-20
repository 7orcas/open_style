package com.sevenorcas.openstyle.app.service.spreadsheet;

import org.apache.poi.hssf.util.HSSFColor;

/**
 * Spreadsheet customer color index control object.<p>
 *  
 * [License] 
 * @author John Stewart
 */
public class Colors {

    //TODO: control this better!
    //Unused colors
    private short [] subColors = new short[] {
            HSSFColor.LAVENDER.index,
            HSSFColor.PALE_BLUE.index,
            HSSFColor.LIGHT_TURQUOISE.index,
            HSSFColor.LIGHT_GREEN.index,
            HSSFColor.LIGHT_YELLOW.index,
            HSSFColor.ROSE.index,
            HSSFColor.TAN.index,
            HSSFColor.PLUM.index,
            HSSFColor.SKY_BLUE.index,
            HSSFColor.BRIGHT_GREEN.index,
    };
    private int subColorIndex = 0;
    
    public int getNextIndex(){
        if (subColorIndex < subColors.length -1){
            ++subColorIndex;
        }
        else{
            subColorIndex = 0;
        }
        
        return subColorIndex;
    }
    
    public short getColorIndex(int index){
        return subColors[index];
    }
    
}
