package clientGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.GameClient;

public class ConnectorGUI extends JFrame{

	private JLabel errorLabel;
	private JLabel hostLabel;
	private JLabel portLabel;
	private JLabel userLabel;
	private JTextField hostField;
	private JTextField portField;
	private JTextField userField;
	private JButton submitButton;
	private Object lock = new Object();
	private MainGUI main;
	
	public ConnectorGUI() throws IOException {
		
		setTitle("Connect to Server");
		
		//initialize all the components of the connector gui

		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		hostLabel = new JLabel();
		hostLabel.setText("Host:");
		portLabel = new JLabel();
		portLabel.setText("Port:");
		userLabel = new JLabel();
		userLabel.setText("Username:");
		
		hostField = new JTextField();
		hostField.setText("jrsun.xvm.mit.edu");
		portField = new JTextField();
		portField.setText("4321");
		userField = new JTextField();
		
		//buttons
		submitButton = new JButton();
		submitButton.setForeground(Color.GREEN);
		submitButton.setText("Connect");
		
		//create layout of gui using GroupLayout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addComponent(hostLabel)
				.addComponent(hostField)
				.addComponent(portLabel)
				.addComponent(portField)
				.addComponent(userLabel)
				.addComponent(userField, 100, 100, 100)
				.addComponent(submitButton))
			.addComponent(errorLabel)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(hostLabel)
				.addComponent(hostField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(portLabel)
				.addComponent(portField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(userLabel)
				.addComponent(userField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(submitButton))
			.addComponent(errorLabel)
		);
		
		// listeners for connecting
		
		hostField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				//This method connects to the server on the host name and port number specified and with the username provided.
				ConnectorGUI.this.connect();
			}
		});
		
		portField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				//This method connects to the server on the host name and port number specified and with the username provided.
				ConnectorGUI.this.connect();
			}
		});
		
		userField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				//This method connects to the server on the host name and port number specified and with the username provided.
				ConnectorGUI.this.connect();
			}
		});
		
		submitButton.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e){
				ConnectorGUI.this.connect();
			}
		});
		
		class WindowList implements WindowListener{
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowOpened(WindowEvent e) {}
		}
		addWindowListener(new WindowList());
		
		pack();
		setVisible(true);
	}
	
	public void setMain(MainGUI main){
		this.main = main;
	}
	
	public void kill(){
		main.client.sendRequest("bye");
	}
	
	private void connect(){
		String host = hostField.getText();
		String port = portField.getText();
		String user = userField.getText();
		if(host != null && port != null && user != null &&
				!user.contains(" ") && !user.contains(":")){
			MainGUI m = null;
			//send this message to the server.
			try {
				Socket s = new Socket(host, Integer.parseInt(port));
				GameClient client = new GameClient(s);
				client.sendRequest(String.format("signin %s", user)); // TODO: THIS GOES IN GUICONNECTOR
				
				m = new MainGUI(client); //TODO: wrap in invokeLater
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				errorLabel.setText(e1.getMessage());
			} finally {
				setMain(m);
				m.setVisible(true);
				setVisible(false);
			}
		} else {
			errorLabel.setText("Invalid host, port, or name!");
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ConnectorGUI connector = null;
				try {
					connector = new ConnectorGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
