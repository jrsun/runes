package clientGUI;

import java.io.IOException;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

public class ConnectorApplet extends JApplet {
	
	ConnectorGUI connector;
	
    //Called when this applet is loaded into the browser.
    public void init() {
    	this.setFocusable(false); // do not want applet to be focusable
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
            	public void run() {
    				ConnectorGUI connector = null;
    				try {
    					connector = new ConnectorGUI();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				setConnector(connector);
    				connector.setVisible(true);
    			}
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }
    
    public void destroy(){
    	connector.kill();
    }
    
    public void setConnector(ConnectorGUI connector){
    	this.connector = connector;
    }
}