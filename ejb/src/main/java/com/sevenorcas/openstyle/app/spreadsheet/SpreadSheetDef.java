package com.sevenorcas.openstyle.app.spreadsheet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation to indicate the order of fields and methods (ie to be used as columns) in the generation of a spreadsheet.<p>
 * 
 * Note: expected return type is <code>List {@literal <}Object{@literal >}</code>.<p>   
 * 
 * eg: The below example will export the following columns in the spreadsheet:
 * <ul>- (super) field value "i"</ul>
 * <ul>- method value getEnglish()</ul>
 * <ul>- method value getDeutsch()</ul>
 * 
 * <pre><code>
 * {@literal @}SpreadSheetDef
 * public List {@literal <}Object{@literal >} spreadSheetDef() throws Exception{
 *
 *    List {@literal <}Object{@literal >} columns = new ArrayList<>();
 *    columns.add(super.getClass().getField("i"));
 *    columns.add(getClass().getMethod("getEnglish"));
 *    columns.add(getClass().getMethod("getDeutsch"));
 *
 *    return columns;
 * }
 * </code></pre>
 * 
 * [License]
 * @author John Stewart
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SpreadSheetDef {
	
}
