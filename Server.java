//package Chatting;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import client.Clients;

public class Server{

	public static void main(String[] args){
	
		Main.function();
	}

}

 class Main {
	
	public static void function() 
	{
		Servers sv= new Servers();
		Thread t2= new Thread(sv);
		t2.start();
	}
}

class Servers extends JFrame implements Runnable {
	JTextField input;
	JTextArea area;
	JLabel label;
	JButton butn;
	OutputStream out;
	InputStream inputstream ;
	JPanel panel ;
	JList listview ;
	
	JLabel ConnectedStudentsCount;
	JLabel NumberOfMessages ;
	JLabel ServerInformation ;
	JLabel runningInformation ;
	
	
	ArrayList<ClientThreads> clients ;
	ArrayList<Message >  messages ;
	
	int clientsCount;
	
	
	
	public Servers()
	{
		
		
		ConnectedStudentsCount = new JLabel("Connected Students : 0");
		NumberOfMessages = new JLabel("Messages Count : 0");
		ServerInformation = new JLabel("Socket Chat Group Server ");
		ServerInformation.setText("Socket Chat Group Server ");
		
		
		Font labelFont = ServerInformation.getFont();
		String labelText = ServerInformation.getText();

		int stringWidth = ServerInformation.getFontMetrics(labelFont).stringWidth(labelText);
		int componentWidth = ServerInformation.getWidth();

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;

		int newFontSize = (int)(labelFont.getSize() * widthRatio);
		int componentHeight = ServerInformation.getHeight();

		// Pick a new font size so it will not be larger than the height of label.
		int fontSizeToUse = Math.min(newFontSize, componentHeight);

		// Set the label's font size to the newly determined size.
		ServerInformation.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
		
		
		runningInformation = new JLabel("Running ... ");
		setLayout(new FlowLayout());
		setTitle(" Socket Server");
		setResizable(false);
//		add(new JLabel("BEFORE YOU write something please make sure you pressed enter"));
//		input = new JTextField(20);{
//			{
//				input.addActionListener(new perform1());
//				add(input);
//			}
//		}
		
		
//		butn = new JButton("send");{
//			{
//				butn.addActionListener(new perform1());
//				add(butn);
//			}
//		}
		
		add(new JLabel("                \n "));
		add(new JLabel("                 \n"));
		add(new JLabel("                 \n"));
		add(ServerInformation);
		add(runningInformation );
		add(ConnectedStudentsCount);
		add(NumberOfMessages);
//		add();
//		label= new  JLabel();
//		listview = new JList();
//		
		area= new JTextArea(20,20);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		add(area);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setSize(400,500);
		panel= new JPanel();
		panel.setSize(400,400);
		panel.setMaximumSize(new Dimension(400,400));
		
	}
	BufferedReader readd;
	BufferedWriter writer;
	String str;
	String st;
	Socket soket;
	@Override
	public void run() {
		clientsCount=0;
		clients = new ArrayList<ClientThreads>();
		messages = new ArrayList<Message >();
		try {
			ServerSocket ser= new ServerSocket(9999);
			System.out.println("Listenning .......");
			while( true ) {
				Socket socket = ser.accept();
				
				// This is the new coming socket where each client in the server will be represented by a single thread.
				ClientThreads cl  = new ClientThreads(socket   , this );
				cl.start();
				this.clientsCount++;
				this.clients.add(cl);
				
//				st= TheInput.readLine();
//				if(st != null) 
//				{	
//					System.out.println("Client class : "+st);
				area.append( "New Client \nClient Quantity is : " + clientsCount );
				area.append("\n");
//				}
			}
		} catch (Exception e) {
			System.out.println("Ooops  1" + e.getMessage());
		}	
	}
	
	public void removeMe( ClientThreads cl    ) {
		clients.remove(cl);
		clientsCount--;
		area.append( "New Client \nClient Quantity is : " + clientsCount );
		area.append("\n");	
	}
	
	public void sendMePastMessages(ClientThreads ct) {
		ct.pastMessages(messages);
	}
	
	public void broadcastJoin( String username  , String id  ) {
		String joinMessage = "join:"+username+":"+id;
		for(ClientThreads clo : clients ) {
			clo.writeMessage(joinMessage);
		}
	}
	
	public void broadcastMessage( Message message) {
		String messageString = "msg:"+message.ID+":"+message.username +":"+message.messageBody;
		System.out.println("Server Broadcasting the Messages .... ");
		for(ClientThreads clo : clients ) {
//			if(clo.username == message.username) {
//				continue;
//			}
			System.out.println("SERVER :  I have sent the message ");
			clo.writeMessage(messageString);
		}
		
		
		this.messages.add(message);
	}
	
	String collect ;
	private class perform1 implements ActionListener
	{
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource()==input) 
			{
				collect =input.getText();
				str +=collect;
				
			}else if (arg0.getSource()==butn) 
			{
				try {
					if(soket != null) {
						writer =  new BufferedWriter( new OutputStreamWriter( soket.getOutputStream()));
					}
					collect =input.getText();
					writer.write(collect);
//					writer.flush();
					area.append(">>"+collect);
					area.append("\n");
				}catch(Exception e) {
					
				}
			}	
		}	
	}
}

class ClientThreads  extends Thread  {
	
	public Socket socket ;
	public String username="UNKNOWN" ;
	public BufferedWriter bufferedWriter ;
	public BufferedReader bufferedReader ;
	public Servers Servers;
	public PrintStream writer;
	public String ID ="";
	
	public ClientThreads(Socket socket  , Servers server  ) {
		this.socket = socket ;
		try {
			this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(Exception e ) {
			
		}
		this.Servers= server;
	}
	
	public void pastMessages(ArrayList<Message >  messages ) {
		for(Message message  : messages ) {
			String messageString = "msg:"+message.ID+":"+message.username +":"+message.messageBody;
			writeMessage(messageString);
		}
	}
	
	public void writeMessage(String message ) {
		try {
			if(writer != null  && socket != null) {
				writer =  new PrintStream(socket.getOutputStream());
			}
			writer.println(message);
			writer.flush();
		}catch(Exception e) {
		}
	}
	
	
	boolean sendPast = true;
	boolean broadcastMe = true;
	@Override
	public void run() {
		while(socket.isConnected() ) {
//			System.out.println("client .. running  ....   ");
			if( this.bufferedReader == null ) {//|| this.bufferedWriter == null  ) {
				try {
					this.bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
//					this.bufferedWriter = this.bufferedWriter == null ? new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())) : this.bufferedWriter;
				}catch(Exception e ) {	
				}
			}
			
			if( socket != null) {
				try {
					writer =  new PrintStream(socket.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sendPast) {
				Servers.sendMePastMessages(this);
				sendPast=false;
			}
			if(broadcastMe && username!= null && !(username.isEmpty()) && !(username.equals("UNKNOWN"))  &&  ID != null && !(ID.isEmpty())) {
				
				Servers.broadcastJoin(username  , ID );
				broadcastMe= false;
			}
			String message ;
			try {
				message = bufferedReader.readLine();
//				System.out.println("Message in the client Side ......  ... "+ message );
				String[] formats = message.split(":");
				if(formats[0].equals("username")) {
					this.username = formats[1];
				}else if( formats[0].equals("msg") ) {
					Message messageo = new Message( this.username ,  this.ID  ,  formats[2] );
//					System.out.println("The Incomming Message is : "+ messageo.messageBody);
					Servers.broadcastMessage(messageo);
				}else if( formats[0].equalsIgnoreCase( "id")) {
					// Setting the ID of the User threadd 
					this.ID=formats[1];
					System.out.println("The User ID "+ this.ID );
				}
			}catch(Exception e ) {
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Servers.removeMe(this);
	}
}
class Message {

	public String ID ;
	public String username;
	public String messageBody ;
	public Message( String id ,String username  , String messageBody ) {
		this.messageBody = messageBody ;
		this.username = username ;
		this.ID = id ;
	}
	
}
