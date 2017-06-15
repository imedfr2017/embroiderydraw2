/*
 * Main.java
 *
 * Created on July 24, 2006, 4:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mlnr;

import com.jpackages.execj.SplashScreen;
import java.io.File;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.UIManager;
import mlnr.gui.FrameEmbroideryDraw;
import mlnr.util.DefaultExceptionHandler;
import mlnr.util.ProgramActivation;

/**
 *
 * @author Robert Molnar II
 */
public class Main {
    static File f = null;
    static FrameEmbroideryDraw frame = null;
    
    static final int PORT_NUMBER = 31052;
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    static public void main(String []args) {
        // Get the file to load.
        if (args.length > 0)
            f = new File(args[0]);
        
        // Make this the default exception handler.
        DefaultExceptionHandler.setDefaultExceptionHandler();
        
        // Set look and feel.
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            DefaultExceptionHandler.printExceptionToLog(e);            
        }
        
        // Now see if there exists any other connection and if so then send the file and exit.
        String fileName = null;
        if (args.length != 0)
            fileName = args[0];        
        boolean anotherInstance = anotherInstanceExist_sendFileName(fileName);
        
        // Exit program because another instance is running.
        if (anotherInstance)
            System.exit(0);
        
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
          
        // Run the server now.
        runServer();
    }
    
    /** This will run a server to check for incoming connections from another instance of the program.
     */
    static void runServer() {        
        byte b[] = new byte[256];
        // Now run the server.
        try {
            ServerSocket myServer = new ServerSocket(PORT_NUMBER);
            
            while (true) {
                // Get the next TCP client.
                Socket nextClient = myServer.accept();
                
                // Get the message.
                int byteCount = nextClient.getInputStream().read(b);
                String message = new String(b, 0, byteCount);
                
                // Now decode the message.
                StringTokenizer stokens = new StringTokenizer(message, "=");
                
                // Must be two tokens, the second one is the file name.
                if (stokens.countTokens() != 2)
                    continue;
                stokens.nextToken();
                String filename = stokens.nextToken();
                
                // No filename.
                if (filename.equals("NULL"))
                    continue;
                
                // Send the file to the program.
                f = new File(filename);
                frame.fileMenuListOpen(f);

            }
            
        } catch (Exception e) {
            System.exit(0);
        }
    }
    
    /** This will attempt to connect to another instance if it is already up. If so then send filename.
     * @param filename is the name of the file to open, or null if no file to open.
     * @return true if another instance already exists, else false can proceed to create instance of application.
      */
    static boolean anotherInstanceExist_sendFileName(String filename) {
        try {            
            Socket s = new Socket((String)null, PORT_NUMBER);
            
            // Now that we are connected to the server, send the filename.
            if (filename != null) {
                String message = "ED2=" + filename;            
                s.getOutputStream().write(message.getBytes());
            } else {
                String message = "ED2=NULL";
                s.getOutputStream().write(message.getBytes());
            }
            
            // Done
            return true;
            
        } catch (Exception e) {
            return false;   // The server is not listening.
        }
    }
    
    /** Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Can the user run the program?
        ProgramActivation programAct = new ProgramActivation();
        if (programAct.isProgramUseable()) {
            // Fire up the program since the user can use it!
            frame = new FrameEmbroideryDraw();
            if (f != null)
                frame.fileMenuListOpen(f);
            SplashScreen.dispose(); // Dispose of the splash screen.
            frame.setVisible(true);
        } else {
            // Exit the JVM
            System.exit(0);
        }
    }
    
    
}
