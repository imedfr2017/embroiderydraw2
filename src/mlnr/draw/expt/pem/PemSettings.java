/*
 * PemSettings.java
 *
 * Created on December 1, 2006, 11:30 AM
 *
 */

package mlnr.draw.expt.pem;

import java.util.prefs.Preferences;
import mlnr.util.InterfaceSettings;

/**
 *
 * @author Robert Molnar II
 */
public class PemSettings implements InterfaceSettings {
    static private String PEM_SAMPLE_DISTANCE = "SampleDistanceV4";  // Distance to sample the file to export as a pem.
    
    /** Creates a new instance of PemSettings */
    public PemSettings() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Static Public Methods ">
    
    /** @return the distance to sample the curves. In measurements a.k.a. millimeters.
     */
    static public float getSamplingRate() {
        return SampledPointProcess.getSamplingRate();
    }
    
    /** @param samplingRate is the distance to sample the curves. In measurements a.k.a. millimeters.
     */
    static public void setSamplingRate(float samplingRate) {
        SampledPointProcess.setSamplingRate(samplingRate);
    }
    
    // </editor-fold>
    
    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        prefs.putFloat(PEM_SAMPLE_DISTANCE, SampledPointProcess.getSamplingRate());
    }
    
    public void load() {
        Preferences prefs = Preferences.userNodeForPackage(mlnr.embd.Version.getVersion());
        SampledPointProcess.setSamplingRate(prefs.getFloat(PEM_SAMPLE_DISTANCE, SampledPointProcess.getSamplingRate()));                
    }
}