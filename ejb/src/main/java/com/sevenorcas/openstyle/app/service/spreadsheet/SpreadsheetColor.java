package com.sevenorcas.openstyle.app.service.spreadsheet;

/**
 * Spreadsheet Color definition
 * 
 * [License] 
 * @author John Stewart
 */
public class SpreadsheetColor {
    
    protected boolean background = true;
    protected int r;
    protected int g;
    protected int b;
    
    /**
     * Default constructor, background flag is set
     * @param r
     * @param g
     * @param b
     */
    public SpreadsheetColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    /**
     * String constructor, background flag is set
     * @param rgb
     */
    public SpreadsheetColor(String rgb) {
        try{
            String[] s = rgb.split(",");
            this.r = Integer.parseInt(s[0]);
            this.g = Integer.parseInt(s[1]);
            this.b = Integer.parseInt(s[2]);
        }
        catch (Exception e){
            this.r = 0;
            this.g = 0;
            this.b = 0;
        }
    }
    
    public int total(){
        return r + g + b;
    }
    
    public String toString(){
        return "" + r + "," + g + "," + b + "," + (background?"b":"f");
    }
    
    public boolean isBackground(){
        return background;
    }
    public void setForeground(boolean fg) {
        this.background = !fg;
    }
    
}
