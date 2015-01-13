package jellyfish;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jellyfish.util.Base64;

public class Jellyfish extends Thread {

//    private ServerSocket serverSocket;
//
//    public Jellyfish(int port) throws IOException {
//        port = 6066;
//
//        serverSocket = new ServerSocket(port);
//        serverSocket.setSoTimeout(10000);
//    }
//    public void run() {
//        while (true) {
//            try {
//                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
//                Socket server = serverSocket.accept();
//                
//                System.out.println("Just connected to " + server.getRemoteSocketAddress());
//                DataInputStream in  = new DataInputStream(server.getInputStream());
//                System.out.println(in.readUTF());
//                DataOutputStream out  = new DataOutputStream(server.getOutputStream());
//                out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
//                server.close();
//            } catch (SocketTimeoutException s) {
//                System.out.println("Socket timed out!");
//                break;
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//    }
//    public static void main(String[] args) {
////      int port = Integer.parseInt(args[0]);
//        int port = 6066;
//        try {
//            Thread t = new Jellyfish(port);
//            t.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
	public static void main(String[] args) throws InterruptedException {

		new Thread(new SimpleServer()).start();
		//new Thread(new SimpleClient()).start();

//                Thread.sleep(20000);
//                new Thread(new SimpleClient()).start();
	}

	static class Hand {

		public Hand() {
			
		}
		
		public String shake(String in) {
			return generateFinalKey(in);
		}

		private String generateFinalKey(String in) {
			String seckey = in.trim();
			String acc = seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			MessageDigest sh1;
			try {
				sh1 = MessageDigest.getInstance("SHA1");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			return Base64.encodeBytes(sh1.digest(acc.getBytes()));
		}
	}
	
	

	static class SimpleServer implements Runnable {

		@Override
		public void run() {
			Hand hand = new Hand();
			ServerSocket serverSocket = null;
			
			try {

				serverSocket = new ServerSocket(12345);
				
//				serverSocket.setSoTimeout(7000);
				try {
					Socket clientSocket = serverSocket.accept();

					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

					BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//						DataInputStream inputReader = new DataInputStream(clientSocket.getInputStream());
//                                                while (true) {
//                                                    System.out.println("Client said :"+ inputReader.readLine());
//                                                }

					String inputLine;
					while ((inputLine = inputReader.readLine()) != null) {
						System.out.println("Server: " + inputLine);
						//out.println(inputLine); 
						if(inputLine.contains("Sec-WebSocket-Key: ")){
							
							out.println("HTTP/1.1 101 Switching Protocols");
							out.println("Upgrade: websocket");
							out.println("Connection: Upgrade");
							out.println("Sec-WebSocket-Accept: "+hand.shake(inputLine.substring(19)));
							//out.println("Sec-WebSocket-Protocol: chat");
							out.println("\n");
							//out.flush();
						}

						if (inputLine.equals("Bye.")) {
							break;
						}
					}

				} catch (SocketTimeoutException e) {
					e.printStackTrace();

				}

			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	static class SimpleClient implements Runnable {

		@Override
		public void run() {

			Socket socket = null;
			try {

				Thread.sleep(3000);

				socket = new Socket("localhost", 3333);

				PrintWriter outWriter = new PrintWriter(
						socket.getOutputStream(), true);

				outWriter.println("Hello Mr. Server!");
				outWriter.println("Hello Mr. Server!");
				outWriter.println("Hello Mr. Server!");
				outWriter.println("Hello Mr. Server!");
				outWriter.println("Hello Mr. Server!");

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

}
