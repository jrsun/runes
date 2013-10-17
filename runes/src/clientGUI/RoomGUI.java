package clientGUI;

import game.Spell;

import java.util.concurrent.TimeUnit;
import java.util.regex.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

import utils.utils;
import client.GameClient;

public class RoomGUI extends JFrame{

	private GameClient client;
	
	private JLabel first;
	private JLabel firstHP;
	private JLabel firstMP;
	
	private FlowLayout firstSpellsView;
	private JPanel firstSpells;
	
	private JLabel second;
	private JLabel secondHP;
	private JLabel secondMP;
	
	private FlowLayout secondSpellsView;
	private JPanel secondSpells;
	
	private GridLayout boardView;
	private JPanel boardPanel;
	private JButton ready;
	private JButton surrender;
	private JButton leave;
	private JTextField chatBox;
	

	private DefaultListModel<String> playerModel;
	private JList<String> playerList;
	private JScrollPane playerScroll;
	
	private DefaultListModel<String> chatModel;
	private JList<String> chatWindow;
	private JScrollPane chatScroll;
	
	private Object lock = new Object();
	private boolean endOnExit;
	
	private String roomName;
	private MainGUI parent;
	
	private boolean playing;
	

    private BufferedImage whiteGem;
    private BufferedImage blackGem;
    private BufferedImage whiteStone;
    private BufferedImage blackStone;
	
	public RoomGUI(String roomName, MainGUI parent) throws IOException {
		
		this.roomName = roomName;
		this.parent = parent;
		this.playing = false;
		this.endOnExit = false;
		
		setTitle(roomName);
		
		//initialize all the components of the chat gui
		
		//labels
		first = new JLabel(" ");
		second = new JLabel("");
		
		firstHP = new JLabel("");
		firstMP = new JLabel("");
		
		secondHP = new JLabel("");
		secondMP = new JLabel("");
		
		//buttons
		ready = new JButton("Ready");
		ready.setForeground(Color.GREEN);
		ready.setName("ready");
		
		surrender = new JButton("Give up");
		surrender.setForeground(Color.BLUE);
		surrender.setName("surrender");
		
		leave = new JButton("Leave");
		leave.setForeground(Color.RED);
		leave.setName("leave");

		//text fields
		chatBox = new JTextField();
		chatBox.setName("chatBox");

		//lists
		chatModel = new DefaultListModel();
		chatWindow = new JList(chatModel);
		chatWindow.setName("chatWindow");
		chatModel.addElement("Chat Window");
		chatScroll = new JScrollPane(chatWindow);

		playerModel = new DefaultListModel<String>();
		playerList = new JList<String>(playerModel);
		playerList.setName("playerList");
		playerModel.addElement("Current Players");
		playerScroll = new JScrollPane(playerList);
		
		//grid
		boardView = new GridLayout(utils.BOARD_SIZE, utils.BOARD_SIZE);
		boardPanel = new JPanel();
		boardPanel.setLayout(boardView);
		for(int i=0;i<boardView.getRows();i++){
			for(int j=0;j<boardView.getColumns();j++){
				final SquarePanel square = new SquarePanel();
				square.setName(String.valueOf(i)+","+String.valueOf(j));
				boardPanel.add(square);
			}
		}
		
		//spells
		ToolTipManager.sharedInstance().setInitialDelay(0);
		firstSpellsView = new FlowLayout();
		firstSpells = new JPanel();
		firstSpells.setLayout(firstSpellsView);
		
		secondSpellsView = new FlowLayout();
		secondSpells = new JPanel();
		secondSpells.setLayout(secondSpellsView);
		
		//create layout of gui using GroupLayout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(first)
						.addComponent(firstHP)
						.addComponent(firstMP))
					.addComponent(firstSpells))
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(second)
						.addComponent(secondHP)
						.addComponent(secondMP))
					.addComponent(secondSpells)))
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(boardPanel, 300, 300, 300)
					.addGroup(layout.createSequentialGroup()
						.addComponent(ready)))
				.addGroup(layout.createParallelGroup()
					.addComponent(playerScroll, 250, 250, 250)))
			.addComponent(chatScroll)
			.addComponent(chatBox)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(first)
								.addComponent(firstHP)
								.addComponent(firstMP))
							.addComponent(firstSpells, 50, 50, 50))
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
								.addComponent(second)
								.addComponent(secondHP)
								.addComponent(secondMP))
							.addComponent(secondSpells, 50, 50, 50)))
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(boardPanel, 300, 300, 300)
						.addGroup(layout.createParallelGroup()
							.addComponent(ready)))
					.addGroup(layout.createSequentialGroup()
						.addComponent(playerScroll)))
				.addComponent(chatScroll, 100, 100, 100)
				.addComponent(chatBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			);
		
		loadStoneImages();
		
		pack();
		connect();
		chatWindow.setCellRenderer(new ColorCellRenderer(chatWindow.getWidth()));
		
		// listener for sending a chat
		
		chatBox.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e){
				String text= chatBox.getText();
				//send this message to the server.
				client.sendRequest("text "+getName()+" "+text);
				chatBox.setText("");
			}
		});
		
		// listener for ready
		
		ready.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(ready.getText().equals("Ready")){
					client.sendRequest("ready "+getName());
					ready.setText("Unready");
				} else if(ready.getText().equals("Unready")){
					client.sendRequest("unready "+getName());
					ready.setText("Ready");
				}
			}
			
		});
		
		// listener for give up
		
		// listener for leave
		
		leave.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				client.sendRequest("leave "+getName());
				setVisible(false);
			}
			
		});
		
		class WindowList implements WindowListener{
			@Override
			public void windowClosing(WindowEvent e) {
				// Close room, close socket
				client.sendRequest("leave "+getName());
				setVisible(false);
				if(endOnExit){System.exit(0);}
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
		
	}
	
	private void loadStoneImages() {
		InputStream stream1 = this.getClass().getClassLoader().getResourceAsStream("white.png");
		InputStream stream2 = this.getClass().getClassLoader().getResourceAsStream("black.png");
		InputStream stream3 = this.getClass().getClassLoader().getResourceAsStream("white-used.png");
		InputStream stream4 = this.getClass().getClassLoader().getResourceAsStream("black-used.png");
		try {
			whiteGem = ImageIO.read(stream1);
			blackGem = ImageIO.read(stream2);
			whiteStone = ImageIO.read(stream3);
			blackStone = ImageIO.read(stream4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connect() throws IOException{
		client = parent.client;
		client.activeRooms.put(roomName, this);
	}
	
	public synchronized void addToChatWindow(final String sender, final String message){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatModel.addElement(sender + ": " + message);
				final int lastIndex = chatModel.getSize() - 1;
				if (lastIndex >= 0) {
					chatWindow.ensureIndexIsVisible(lastIndex);
				}
			}
		});
	}
	
	public synchronized void populateFirstSpellsPanel(final String[] spellNames){
		if(spellNames.length > firstSpells.getComponentCount()){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					firstSpells.removeAll();
					for(String spellName : spellNames){
						Spell spell = utils.SPELLS.get(spellName.toUpperCase());
						firstSpells.add(new SpellPanel(spell));
					}
					firstSpells.setVisible(true);
					repaint();
				}
			});
		}
	}
	
	public synchronized void populateSecondSpellsPanel(final String[] spellNames){
		if(spellNames.length > secondSpells.getComponentCount()){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					secondSpells.removeAll();
					for(String spellName : spellNames){
						Spell spell = utils.SPELLS.get(spellName.toUpperCase());
						secondSpells.add(new SpellPanel(spell));
					}
					secondSpells.setVisible(true);
					repaint();
				}
			});
		}
	}
	
	class SpellPanel extends JPanel implements MouseListener{
		
		Spell spell;
		BufferedImage image;
		
		SpellPanel(Spell spell){
			this.spell = spell;
			setPreferredSize(new Dimension(40, 40));
			setVisible(true);
			setSpellImage(spell.getName());
			setToolTipText(getSpellPattern());
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics grphcs){
			super.paintComponent(grphcs);
	        Graphics2D g2d = (Graphics2D) grphcs;
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.drawImage(image, 0, 0, getWidth()
	        		, getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
		}
		
		private void setSpellImage(String spellName){
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(String.format("%s.png", spellName));
			try {
				image = ImageIO.read(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private String getSpellPattern(){
			return spell.getPatternStrings().get(0).trim();
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
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
	
	class SquarePanel extends JPanel implements MouseListener{
		
		int color = 0;
	    Border blueBorder = BorderFactory.createLineBorder(Color.BLUE,3);
	    Border yellowBorder = BorderFactory.createLineBorder(Color.YELLOW,3);
		
		SquarePanel(){
			addMouseListener(this);
			setBorder(BorderFactory.createLineBorder(Color.gray));
			setBackground(utils.DARK_GREEN);
		}
		
		@Override
	    protected void paintComponent(Graphics grphcs) {
	        super.paintComponent(grphcs);
	        Graphics2D g2d = (Graphics2D) grphcs;
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        if(color == utils.WHITE){
	            g2d.setColor(Color.WHITE);
	            //g2d.fillOval(5, 5, (getWidth()-10), (getHeight()-10));
	            g2d.drawImage(whiteGem, 0, 0, getWidth(), getHeight(), 0, 0, whiteGem.getWidth(), whiteGem.getHeight(), null);
	        } else if(color == utils.BLACK){
	        	g2d.setColor(Color.BLACK);
	        	g2d.drawImage(blackGem, 0, 0, getWidth(), getHeight(), 0, 0, blackGem.getWidth(), blackGem.getHeight(), null);
	 	    } else if(color == utils.WHITE_USED){
	        	g2d.setColor(Color.LIGHT_GRAY);
	        	g2d.drawImage(whiteStone, 0, 0, getWidth(), getHeight(), 0, 0, whiteStone.getWidth(), whiteStone.getHeight(), null);
	 	    } else if(color == utils.BLACK_USED){
	        	g2d.setColor(Color.GRAY);
	        	g2d.drawImage(blackStone, 0, 0, getWidth(), getHeight(), 0, 0, blackStone.getWidth(), blackStone.getHeight(), null);
	 	    }
	        //setBackground(playing ? utils.GREEN : utils.DARK_GREEN);
	    }
		
		public void highlight() {
			this.setBackground(Color.YELLOW);
			repaint();
		}
	    
		@Override
		public void mouseClicked(MouseEvent e) {
			if(playing){
				client.sendRequest(String.format("place %s %s %s", roomName,
					getName().split(",")[0],
					getName().split(",")[1]));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			if(playing){setBorder(blueBorder);}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if(playing){setBorder(BorderFactory.createLineBorder(Color.gray));}
		}
		
		public void setColor(int color){
			this.color = color;
		}
		
		public int getColor(){
			return color;
		}
			
	}
	
	public void paintBoard(int row, int col){
		paintBackground(utils.GREEN);
		SquarePanel square = (SquarePanel) boardPanel.getComponent(col+utils.BOARD_SIZE*row);
		square.highlight();
	}
	
	public void updateBoard(String boardRepr){
		for(int i=0;i<boardRepr.length();i++){
			SquarePanel square = (SquarePanel) boardPanel.getComponent(i);
			if(boardRepr.charAt(i) == '-'){
				square.setColor(utils.EMPTY);
			} else if(boardRepr.charAt(i) == 'W'){
				square.setColor(utils.WHITE);
			} else if(boardRepr.charAt(i) == 'B'){
				square.setColor(utils.BLACK);
			} else if(boardRepr.charAt(i) == 'w'){
				square.setColor(utils.WHITE_USED);
			} else if(boardRepr.charAt(i) == 'b'){
				square.setColor(utils.BLACK_USED);
			}
			square.repaint();
		}
	}
	
	public void setFirst(String playerName){
		first.setText(playerName);
	}
	
	public void setSecond(String playerName){
		second.setText(playerName);
	}
	
	public void updatePlayerInfo(String p1HP, String p1MP, String p2HP, String p2MP){
		firstHP.setText(p1HP);
		firstMP.setText(p1MP);
		secondHP.setText(p2HP);
		secondMP.setText(p2MP);
	}
	
	public DefaultListModel<String> getPlayerModel(){
		return playerModel;
	}
	
	public String getName(){
		return roomName;
	}
	
	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
		if(playing){
			paintBackground(utils.GREEN);
		} else {
			paintBackground(utils.DARK_GREEN);
		}
		repaint();
	}
	
	public void paintBackground(Color color){
		for(Component square : boardPanel.getComponents()){
			square.setBackground(color);
		}
	}

	public void end(){
		setPlaying(false);
		setFirst("");
		setSecond("");
		repaint();
	}
	
	public void removeReady(){
		remove(ready);
	}
	
	public void setEndOnExit(boolean endOnExit){
		this.endOnExit = endOnExit;
	}
	
	public static void main(String[] args) throws IOException{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				RoomGUI rGUI = null;
				try {
					rGUI = new RoomGUI("Default", 
							new MainGUI(new GameClient(new Socket("localhost", 4321))));
					
					rGUI.setEndOnExit(true);
					
					rGUI.client.sendRequest("signin roomtester"); 
					rGUI.client.sendRequest("join Default");
					rGUI.client.sendRequest("leave lobby");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
				rGUI.setVisible(true);
			}
		});
	}
}