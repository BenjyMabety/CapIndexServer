package tmg.za;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static void main(String[] args) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(80);
			ServerConnector thread = new ServerConnector(serverSocket);
			while (true) {
				thread.run();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
