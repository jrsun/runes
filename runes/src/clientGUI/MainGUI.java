package clientGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.GameClient;

public class MainGUI extends JFrame{
	public GameClient client;
	
	private JTextField roomNameToCreate;
	private JTextField chatBox;
	private JButton createButton;
	private DefaultListModel<String> chatModel;
	private JList<String> chatWindow;
	private JScrollPane chatScroll;
	private DefaultListModel<String> roomModel;
	private JList<String> roomList;
	private JScrollPane roomScroll;
	private DefaultListModel<String> playerModel;
	private JList<String> playerList;
	private JScrollPane playerScroll;
	private Object lock = new Object();
	
	public MainGUI(final GameClient client) throws IOException {
		
		this.client = client;
		
		setTitle("Runes");
		
		//initialize all the components of the chat gui

		//buttons
		JButton createButton = new JButton("Create New Room");
		createButton.setForeground(Color.BLUE);
		createButton.setName("joinButton");

		//text fields
		roomNameToCreate = new JTextField();
		roomNameToCreate.setName("roomNameToCreate");
		chatBox = new JTextField();
		chatBox.setName("chatBox");

		//lists
		
		chatModel = new DefaultListModel<String>();
		chatWindow = new JList<String>(chatModel);
		chatWindow.setName("chatWindow");
		chatModel.addElement("Chat Window");
		
		roomModel = new DefaultListModel<String>();
		roomList = new JList<String>(roomModel);
		roomList.setName("roomList");
		roomModel.addElement("Current Rooms");
		
		playerModel = new DefaultListModel<String>();
		playerList = new JList<String>(playerModel);
		playerList.setName("playerList");
		playerModel.addElement("Current Players");
		
		chatScroll = new JScrollPane(chatWindow);
		roomScroll = new JScrollPane(roomList);
		playerScroll = new JScrollPane(playerList);
		
		//create layout of gui using GroupLayout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addComponent(chatScroll, 300, 300, 300)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(roomNameToCreate, 100, 100, 100)
						.addComponent(createButton))
					.addComponent(roomScroll)
					.addComponent(playerScroll)))
			.addComponent(chatBox)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(chatScroll, 500, 500, 500)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(roomNameToCreate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(createButton))
					.addComponent(roomScroll)
					.addComponent(playerScroll)))
			.addComponent(chatBox,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		
		// listener for sending a chat
		
		chatBox.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e){
				String text= chatBox.getText();
				//send this message to the server.
				client.sendRequest("text lobby "+text);
				chatBox.setText("");
			}
		});
		
		// listener for creating new room
		
		createButton.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e){				
				final String roomName = roomNameToCreate.getText();
				if(roomName.equals("") || roomName.contains(" ")){
					roomNameToCreate.setText("Invalid name!");
					return;
				} else {
					roomNameToCreate.setText("");
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							RoomGUI rGUI = null;
							try {
								rGUI = new RoomGUI(roomName, MainGUI.this);

								client.sendRequest("join "+roomName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							rGUI.pack();
							rGUI.setVisible(true);
						}
					});
				}
			}
		});
		
		// listener for joining existing room
		
		MouseListener roomListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					final int index = roomList.locationToIndex(e.getPoint());
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							RoomGUI rGUI = null;
							try {
								String roomName = roomModel.getElementAt(index);
								rGUI = new RoomGUI(roomName, MainGUI.this);

								client.sendRequest("join "+roomName);
								client.sendRequest("list_players "+roomName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							rGUI.pack();
							rGUI.setVisible(true);
						}
					});
				}
			}
		};
		roomList.addMouseListener(roomListener);
		
		class WindowList implements WindowListener{
			@Override
			public void windowClosing(WindowEvent e) {
				//Tell the server to close this connection and leave all open conversations.				
				client.sendRequest("leave lobby");
				client.sendRequest("bye");
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
		
		
		client.gui = this;
		
		client.sendRequest("join lobby");
		client.sendRequest("list_rooms");
		client.sendRequest("list_players lobby");
		
		pack();
		chatWindow.setCellRenderer(new ColorCellRenderer(chatWindow.getWidth()));
	}
	
	public void addToChatWindow(String sender, String message){
		chatModel.addElement(sender + ": " + message);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				int lastIndex = chatModel.getSize() - 1;
				if (lastIndex >= 0) {
				   chatWindow.ensureIndexIsVisible(lastIndex);
				}
			}
		});
	}
	
	public DefaultListModel<String> getPlayerModel(){
		return playerModel;
	}
	
	public DefaultListModel<String> getRoomModel(){
		return roomModel;
	}
	
	private class ColorCellRenderer extends DefaultListCellRenderer {

		private int cWidth;
		public static final String HTML_1 = "<html><body style='width: ";
		public static final String HTML_2 = "px;color: ";
		public static final String HTML_3 = "'>";
		public static final String HTML_4 = "</html>";
		
		public ColorCellRenderer(int width) {
			setOpaque(true);
			this.cWidth = width - 150;
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus){
			// TODO Auto-generated method stub
			String text = value.toString();
			Pattern p = Pattern.compile("(/)(.+?) ");
			Matcher m = p.matcher(text);
			String color = "black";
			if(m.find()){
				color = m.group(2);
				text = text.replace(m.group(1)+m.group(2), "");
			}

			text = HTML_1+String.valueOf(cWidth)+HTML_2+
					color+HTML_3+text+HTML_4;
			return super.getListCellRendererComponent(list, text, index, isSelected,
		            cellHasFocus);
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainGUI main = null;
				try {
					main = new MainGUI(new GameClient(new Socket("localhost", 4321)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				main.setVisible(true);
			}
		});
	}
}
