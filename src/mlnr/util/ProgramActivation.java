/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mlnr.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.*;
import mlnr.gui.dlg.DialogTrialMode;

/**
 *
 * @author Robert Molnar
 */
public class ProgramActivation {
    /** Program key for Embroidery Draw version 2. */    
    private static final String EMBROIDERYDRAW_2_0 = "EmbroideryDraw20";
    /** Program Last Date opened. Used only when not activated, so that day count is done by the changing of dates. */
    private static final String EMBROIDERYDRAW_LAST_DATE = "EmbroideryDrawLastDate";
    /** Program days on trial mode count. */
    private static final String EMBROIDERYDRAW_COUNT = "EmbroideryDrawCount";
    
    /** Creates a new instance of ProgramActivation */
    public ProgramActivation() {
    }
    
    /** This will get the program key for the program.
     * @return the program key or if it does not exist then empty string.
     */
    public String getProgramKey() {
        return Preferences.userNodeForPackage(mlnr.embd.Version.getVersion()).get(EMBROIDERYDRAW_2_0, "");
    }
    
    /** DEBUG purposes. This will remove the program key.
     */
    public void removeProgramKey() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.put(EMBROIDERYDRAW_2_0, "");
    }
    
    /** This will check to see if the program can be used. It will check for a program key first, if it has then this 
     * will return without asking the user anything, if not then it will ask for trial mode or program key.
     * @param frame is the main frame of the program.
     * @return true if the program is to be used, else false shut program down.
     */
    public boolean isProgramUseable() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        String programKey = prefs.get(EMBROIDERYDRAW_2_0, "");
        
        // There is a key.
        if (programKey.equals("") == false)
            return true;
        
        // Get the number of days left on the trial version.
        int trialDaysLeft = getTrialDaysLeft(getLastdayProgramOpened());
               
        // Show Trial Mode or Program Activation dialog.
        DialogTrialMode dialog = new DialogTrialMode(null, true);
        dialog.setTrialDaysLeft(trialDaysLeft);
        dialog.setVisible(true);
        
        return dialog.isProgramRunable();
    }
    
    /** This will get the number of trial days left to use this program.
     * @param gLastDayOpened is the last day this program was opened.
     * @return the number of trial days left.
     */
    private int getTrialDaysLeft(GregorianCalendar gLastDayOpened) {        
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        
        // Get the number of days left for the trial mode.
        int trialDaysLeft = prefs.getInt(EMBROIDERYDRAW_COUNT, 15);
        
        // Decrease it by 1 if the days are different.
        GregorianCalendar gToday = new GregorianCalendar();
        if (gToday.get(Calendar.YEAR) != gLastDayOpened.get(Calendar.YEAR) 
        || gToday.get(Calendar.MONTH) != gLastDayOpened.get(Calendar.MONTH)
        || gToday.get(Calendar.DAY_OF_MONTH) != gLastDayOpened.get(Calendar.DAY_OF_MONTH))
            trialDaysLeft--;
        
        // No more days left on the trial version.
        if (trialDaysLeft <= 0)
            trialDaysLeft = 0;
        
        prefs.putInt(EMBROIDERYDRAW_COUNT, trialDaysLeft);
        
        return trialDaysLeft;
    }
    
    /** Debug purposes only. This will set the number of trial days left.
     */
    public void setTrialDays(int trialDays) {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.putInt(EMBROIDERYDRAW_COUNT, trialDays);        
    }
    
    /** This will get the last day that the program was opened and it will set the
     * last day opened to today.
     * @return the last day program was opened.
     */
    private GregorianCalendar getLastdayProgramOpened() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        
        int year = prefs.getInt(EMBROIDERYDRAW_LAST_DATE+"y", -1);
        int month = prefs.getInt(EMBROIDERYDRAW_LAST_DATE+"m", -1);
        int day = prefs.getInt(EMBROIDERYDRAW_LAST_DATE+"d", -1);
        
        // Set the last day opened to this day.
        GregorianCalendar gCalToday = new GregorianCalendar();
        prefs.putInt(EMBROIDERYDRAW_LAST_DATE+"y", gCalToday.get(Calendar.YEAR));
        prefs.putInt(EMBROIDERYDRAW_LAST_DATE+"m", gCalToday.get(Calendar.MONTH));
        prefs.putInt(EMBROIDERYDRAW_LAST_DATE+"d", gCalToday.get(Calendar.DAY_OF_MONTH));
        
        // If the last day does not exist then set it to the current day.
        if (year == -1)
            return gCalToday;        
        
        return new GregorianCalendar(year, month, day);
    }
        
    /** This will save the program key.
     */
    public void saveProgramKey(String programKey) {
        if (programKey.length() == 18)
            programKey = programKey.substring(0, 6) + "-" + programKey.substring(6, 12) + "-" + programKey.substring(12, 18);
        
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.put(EMBROIDERYDRAW_2_0, programKey);
    }    
}