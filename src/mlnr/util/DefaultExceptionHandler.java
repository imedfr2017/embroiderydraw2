/*
 * DefaultExceptionHandler.java
 *
 * Created on January 19, 2007, 8:47 PM
 *
 */

package mlnr.util;

import java.awt.Frame;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mlnr.embd.Version;
import mlnr.gui.dlg.DialogErrorMessage;

/**
 *
 * @author Robert Molnar II
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler {
    /** This is the place were exception messages are outputted. */
    private static String exceptionFile = "embroiderydraw.error.log";    
    
    /** Call this to make this class the default exception handler.
     */
    public static void setDefaultExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
    }

    /** This will print the message to the log. Should only be used in rare cases when printing the
     * exception will not work.
     */
    static public void printToLog(String message) {        
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(exceptionFile, true));
            out.println("## ======== Embroidery Draw VERSION: " + Version.getCurrentVersion() + "======== ");
            out.println(message);
            out.close();
        } catch (Exception ee) {// What do you do??? 
        }
    }
    
    /** This will print the exception to the log.
     */
    static public void printExceptionToLog(Throwable e) {
        e.printStackTrace(System.err);
        
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(exceptionFile, true));
            out.println("## ======== Embroidery Draw VERSION: " + Version.getCurrentVersion() + "======== ");
            e.printStackTrace(out);
            out.close();
        } catch (Exception ee) {// What do you do??? 
        }
    }
    
    /** This will print the exception to the log.
     * @param e is the exception incountered.
     * @param userMessage is the message to show the user.
     */
    static public void printExceptionToLog(Throwable e, String userMessage) {
        // Show message.
        JOptionPane.showMessageDialog(null,userMessage, "Error Message", JOptionPane.ERROR_MESSAGE);        
        
        printExceptionToLog(e);
    }
    
    /** This will catch any uncaught errors.
     */
    public void uncaughtException(Thread t, Throwable e) {
        DialogErrorMessage dialog = new DialogErrorMessage(findActiveFrame(), true);
        dialog.setException(e);
        dialog.setVisible(true);
        printExceptionToLog(e);
        e.printStackTrace(System.err);
    }
    
    private Frame findActiveFrame() {
        Frame[] frames = JFrame.getFrames();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].isVisible()) {
                return frames[i];
            }
        }
        return null;
    }
}
