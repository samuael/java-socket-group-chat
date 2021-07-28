
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

	public static void main(String args[]){
	
		MainClient.Client();
	
	}

}


class MainClient {

	public static void Client() {
		// TODO Auto-generated method stub
//		chatroom cr = new chatroom();
		Thread clientThread = new Thread( new Clients() );
		clientThread.start();
	}

}
class Clients extends JFrame  implements Runnable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2574921023213319492L;
	JTextField input;
	JTextArea area;
	JLabel label;
	JButton butn;
	OutputStream out;
	InputStream inputstream ;
	ScrollPane scroll ;
	JTextField usernameEntry; 
	JLabel showUsername;
	JLabel showID ;
	ArrayList<String>  users;
	GridLayout textareaandstudentsContainer;
	
	JList<String> studentsList ;
	
	
	Clients()
	{
		
		setBackground(Color.BLACK);
		 users = new ArrayList<String>();

		 users.add("abebe");
		 users.add("kebede");
		 users.add("samuael");
		 users.add("chala");
		 users.add("Obsa");
		 
		label  = new JLabel(".");
		setLayout(new FlowLayout());
		scroll = new ScrollPane();
		scroll.setBounds(5, 0, 500, 652);
		setTitle("          Client         ");
		
//		studentsList = new JList<String>();
//		studentsList.setListData( (String[]) users.toArray());
		showUsername = new JLabel();
		showUsername .setSize(10, 10);
		JPanel  panelo = new JPanel();
		showUsername.setForeground(Color.BLUE);
		showID = new JLabel();
		showID.setSize(10, 10);
		showID.setSize(new Dimension(500 , 100));
		textareaandstudentsContainer = new GridLayout(0 , 2);
		
		this.setBackground(Color.black);
		this.setForeground(Color.black);
		setResizable(false);
//		this.setLocation(500, 300);
		
		add(showUsername);
		add(panelo);
		add(showID);

		JPanel panel = new JPanel();
		area= new JTextArea(100,200);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		textareaandstudentsContainer.addLayoutComponent("Students",  studentsList  );
		textareaandstudentsContainer.addLayoutComponent("Messages" ,  area);
		scroll.add(area);
		add(scroll );
		input = new JTextField(20);{
			{
				input.addActionListener(new perform("Username" ));
				add(input);
			}
		}
		butn = new JButton("Send");{
			{
				butn.addActionListener(new perform("Username"));
				add(butn);
			}
		}
		
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
	setSize(600,800);
		
	}
	PrintStream writer;
	String str;
	String HOST ; 
	
	
	Socket soket;
	
	BufferedReader readd ; 
	BufferedWriter bufferedWriter ;
	private BufferedReader bufferedReader;
	
	String username ;
	String ID ;
	boolean once =true;
	@Override
	public void run() {
		ID= ""+System.currentTimeMillis();
		while(  username ==null || username.isEmpty() ) {
			username = JOptionPane.showInputDialog("To Join the CHAT\n Please Enter Your USERNAME ");
			if(username == null ) {
				System.exit(-1);
			}
		}
		showUsername.setText("Username : "+ username);
		
//		System.out.println("Username : "+ this.username  + "  ID  : " + this.ID );
		
		showID.setText("ID : "+ID);
		while(HOST==null || HOST.isEmpty()) {
			HOST = JOptionPane.showInputDialog("Enter Server Host Address ");
			try {
				boolean success = validateHost(HOST);
				if( HOST==null ) {
					System.exit(-1);
				}else if(!success) {
					HOST=null ;
				}
			}catch(Exception e ) {
				System.exit(-1);
			}
		}
		
		boolean hostNotValid = false;
		while(soket == null || !soket.isConnected()) {
			try {
				while(hostNotValid) {
					HOST = JOptionPane.showInputDialog("Enter Server Host Address \n Can't reach the Host You Give Previously");
					boolean success = validateHost(HOST);
					if(!success) {
						HOST= "";
					}else {
						hostNotValid=false;
					}
					
				}
				System.out.println(HOST);
				soket = new Socket(HOST,9999);
				
				if(soket.isConnected()) {
					System.out.println("I Got Connected nigga ");
				}				
				while( soket.isConnected()) {

					if( this.bufferedReader == null ) {//|| this.bufferedWriter == null  ) {
						try {
							this.bufferedReader = new BufferedReader( new InputStreamReader(soket.getInputStream()));
//							this.bufferedWriter = this.bufferedWriter == null ? new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())) : this.bufferedWriter;
						}catch(Exception e ) {	
						}
					}
				
					if( soket != null) {
						try {
							writer =  new PrintStream(soket.getOutputStream());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					String message ;
					
					if(once) {
						sendUserID(ID);
						sendUsername(username);
						once= false;
					}
					try {
						message = bufferedReader.readLine();
						if(message != null ){
						try {
							System.out.println("the incomint message is "+ message );
							String[] formats = message.split(":");
//							System.out.println("Message Arrived nigga ");
							if( formats[0].equals("msg") ) {
//								System.out.println("I have got message "+ formats[2]);
								String newMessage =  "_______________________________\n"+ ( formats[2].equals(ID) ? "----------------You-----------------" : "Username : "+formats[1] )+ "\nUser ID : "+formats[2] + "\n--Message --\n\t"+formats[3]+"\n----------------------------------\n";
								area.append(newMessage);
								area.append("\n");
							}else if( formats[0].equals("join") ) {
								String newJoin = "------------------ "+(  formats[2].equals(ID) ? "You" : formats[1]  )+" Just Joined the Group Chat --------------------\n";
								area.append(newJoin);
								area.append("\n");
							}
						}catch(Exception e ) {	
//							System.out.println("Error While Revieving the message ");
							e.printStackTrace();
						}
					}
					}catch(Exception e ) {
					}
				}
			} catch (Exception e) {
				System.out.println("Ooops");
				hostNotValid = true;
//				e.printStackTrace();
			} 
		}
	}
	
	public boolean validateHost(String host  ) {
		String[] forms = host.split(".");
		if(host.equals("localhost")) {
			return true;
		}
		for(String el : forms) {
			try {
				Integer.parseInt(el);
			}catch(Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public void changeUsername() {
		this.username = JOptionPane.showInputDialog("Enter Username :");
		if(this.username != null && !this.username.isEmpty()) {
			sendUsername(this.username);
			sendUserID(this.ID);
		}
	}
	
	public void sendUsername(String username ) {
		try {
			if(writer != null  && soket != null) {
				writer =  new PrintStream(soket.getOutputStream());
			}
			String structuredMessage = "username:"+this.username;
			writer.println(structuredMessage );
			
			writer.flush();
			
		}catch(Exception e) {
			
		}
	}
	
	public void sendUserID(String userID ) {
		try {
			if(writer != null  && soket != null) {
				writer =  new PrintStream(soket.getOutputStream());
			}
			String structuredMessage = "id:"+this.ID;
			writer.println(structuredMessage );
			
			writer.flush();
		}catch(Exception e) {
			
		}
	}
	
	String collect ;
	public class perform implements ActionListener
	{

		public String username ;
		public perform(String username ) {
			this.username = username ;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource()==input) 
			{
				collect =input.getText();
				str +=collect;
				label.setText(str);
				
			}
			/*else if( arg0.getActionCommand() !=null || !(arg0.getActionCommand().equals(""))  ) {
				try {
					if(writer != null  && soket != null) {
						writer =  new PrintStream(soket.getOutputStream());
					}
					String structuredMessage = "msg:"+this.username +":"+arg0.getActionCommand();
					if(once) {
						sendUserID(ID);
						sendUsername(username);
						once= false;
					}
					writer.println(structuredMessage );
					
					writer.flush();
					input.setText("");
				}catch(Exception e) {
					
				}
				
			}
			*/
			else if (arg0.getSource()==butn)
			{
				try {
					if(writer != null  && soket != null) {
						writer =  new PrintStream(soket.getOutputStream());
					}
					collect = input.getText();
					String structuredMessage = "msg:"+this.username +":"+collect;
					if(once) {
						sendUserID(ID);
						sendUsername(username);
						once= false;
					}
					writer.println(structuredMessage );
					
					writer.flush();
					input.setText("");
				}catch(Exception e) {
					
				}
			}	
		}	
	}
}
