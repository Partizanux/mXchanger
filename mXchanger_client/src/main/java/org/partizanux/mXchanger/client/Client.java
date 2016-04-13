package org.partizanux.mXchanger.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Client {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);

	private static final String HOST = "localhost";
	private static final int PORT = 9123;
	
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	
	private Socket session;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public void dispose() throws IOException {
		if(reader != null)
			reader.close();
		if(writer != null)
			writer.close();
		if(session != null)
			session.close();
	}

	public String sendMessage(String request, boolean keepSession) {
		
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader br = null;
		
		try{
			socket = new Socket(HOST, PORT);
			out = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), CHARSET), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			if(keepSession) {
				socket.setKeepAlive(true);
				session = socket;
				writer = out;
				reader = br;
			}
			// add end line symbols, cause Server uses TextLineCodecFactory
			out.println(request);
			
			int c;
			StringBuilder sb = new StringBuilder();
			
			// NOTE: Server uses TextLineCodecFactory, 
			// though we'll get messages with "\n" (ascii LF, 10 in DEX) symbol at the end
			// So we'll use not -1, but 10 like the end message symbol
			while((c = br.read()) != 10)
				sb.append((char)c);
			
			return sb.toString();

		} catch (UnknownHostException e) {
			logger.error("Exception in sendMessage:\n", e);
			return "";
		} catch (IOException e1) {
			logger.error("Exception in sendMessage:\n", e1);
			return "";
		} finally {
			try {
				if(!keepSession) {
					socket.close();
					br.close();
					out.close();
				}
			} catch (IOException e) {
				logger.error("Exception in sendMessage: exception while closing streams or sockets\n", e);
			}
		}
	}
	
	public String sendMessageInSession(String request) {
		
		if (session == null) {
			logger.error("Exception in sendMessageInSession: session == null\n");
			return "";
			}
		
		PrintWriter out = null;
		BufferedReader br = null;
		
		try{
			//out = new PrintWriter(new OutputStreamWriter(session.getOutputStream(), CHARSET), true);
			out = writer;
			
			// add end line symbols, cause Server uses TextLineCodecFactory
			out.println(request);
			
			br = reader;
			int c;
			StringBuilder sb = new StringBuilder();
			
			while((c = br.read()) != 10)
				sb.append((char)c);
			
			return sb.toString();
			
		} catch (IOException e) {
			logger.error("Exception in sendMessageInSession: exception while closing streams or sockets\n", e);
			return "";
		} finally {
			try {
				br.close();
				out.close();
				session.close();
			} catch (IOException e) {
				logger.error("Exception in sendMessageInSession: exception while closing streams or sockets\n", e);
			}
		}
	}
	
	
}
