package tmg.za;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import tmg.za.shared.MySQLConnection;
import tmg.za.shared.data.GetUser;

public class ServerConnector implements Runnable {

	ServerSocket serverSocket;
	MySQLConnection conn = new MySQLConnection();
	GetUser user;

	public ServerConnector(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		try {
			System.out.println("Server started, waiting for client...");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected: " + clientSocket.getInetAddress());

			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			String data = "";

			Scanner myReader = new Scanner(in);
			while (myReader.hasNextLine()) {
				data += parse(myReader.nextLine() + "\n");
			}
			myReader.close();
			// try this close, if it's not working. Check how to look for end of
			// file...otherwise hardcode hack check for last tag
			// implement on runnable thread
			in.close();

			System.out.println("Received from client: " + data);
			buildUser(data);
			String response = data.toUpperCase();
			// out.println(response);
			System.out.println("Sent to client: " + response);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildUser(String data) {
		try {
			user = new GetUser();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(data));
			Document document = builder.parse(inputSource);

			document.getDocumentElement().normalize();

			// Access elements
			Element root = document.getDocumentElement();
			System.out.println("Root element: " + root.getNodeName());

			NodeList nodeList = document.getElementsByTagName("SetUserInfo");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element userInfo = (Element) nodeList.item(i);
				Element firstName = (Element) userInfo.getElementsByTagName("SetFirstName").item(0);
				Element lastName = (Element) userInfo.getElementsByTagName("SetLastName").item(0);
				Element cardNumber = (Element) userInfo.getElementsByTagName("SetCardNumber").item(0);
				Element bank = (Element) userInfo.getElementsByTagName("SetBank").item(0);
				Element loyalty = (Element) userInfo.getElementsByTagName("SetLoyalty").item(0);
				Element userID = (Element) userInfo.getElementsByTagName("SetUserID").item(0);
				System.out.println("Order " + (i + 1) + ":");
				System.out.println("First Name: " + firstName.getTextContent());
				System.out.println("Last Name: " + lastName.getTextContent());
				System.out.println("Card Number: " + cardNumber.getTextContent());
				System.out.println("Bank: " + bank.getTextContent());
				System.out.println("Loyalty: " + loyalty.getTextContent());
				System.out.println("UserID: " + userID.getTextContent());
				user.setId(userID.getTextContent());
				user.setFirstName(firstName.getTextContent());
				user.setLastName(lastName.getTextContent());
				user.setCardNumber(cardNumber.getTextContent());
				user.setBankID(Integer.valueOf(bank.getTextContent()));
				user.setLoyaltyCredit(Double.valueOf(loyalty.getTextContent()));

				conn.setUser(user);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String parse(String line) {
		if (line.equals("")) {
			return "\n";
		}
		return line;
	}

}
