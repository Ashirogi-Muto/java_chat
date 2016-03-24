package chatroomServer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server()
	{
		super("Ashirogi Instant Messaging");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener
		(			
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}			
		);
		
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	//set up and run  the server
	
	public void startRunning()
	{
		try
		{
			server = new ServerSocket(6789, 100);
			while(true)
			{
				try
				{
					waitForConnection();
					setupStreams();
					whileChatting();
				}
				catch(EOFException eof)
				{
					showMessage("\n Srver ended the connection");
				}
				finally
				{
					closeCrap();
				}
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//wait for connection then display info
	
	private void waitForConnection() throws IOException
	{
		showMessage("Waiting to connect with someone..\n");
		connection = server.accept();
		showMessage("Now connected to" + connection.getInetAddress().getHostName());
	}
	
	//get stream to send and receive data
	
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup\n");
		
	}
	
	//chat conversation
	
	private void whileChatting() throws IOException
	{
		String msg = "You are now connected";
		showMessage(msg);
		ableToType(true);
		do
		{
			try
			{
				msg  = (String) input.readObject();
				showMessage("\n" + msg);
			}
			catch(ClassNotFoundException c)
			{
				showMessage("\n IDK");
			}
		}
		while(!msg.equals("CLIENT - END"));
	}
	
	// close streams and sockets
	private void closeCrap()
	{
		showMessage("\n Closing connections");
		ableToType(false);
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}
	}
	
	private void sendMessage( String message)
	{
		try
		{
			output.writeObject("Server - " + message);
			output.flush();
			showMessage("\nServer - " + message);
		}
		catch(IOException i)
		{
			chatWindow.append("Can't send that message");
		}
	}
	
	//update chat window
	
	private void showMessage(final String text)
	{
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{
						chatWindow.append(text);
					}
				}
		);
	}
	
	//let user type
	private void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{
						userText.setEditable(tof);
					}
				}
		);
	}
	
}
