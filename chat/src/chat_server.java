import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class chat_server {

	static List<Client> clients = new ArrayList<chat_server.Client>();

	public static void main(String[] args) {
		boolean started = false;
		ServerSocket s = null;
		try {
			s = new ServerSocket(8888);
			started = true;
			while (started) {
				Socket socket = s.accept();
				chat_server chat_server = new chat_server();
				Client client = chat_server.new Client(socket);
				new Thread(client).start();
				clients.add(client);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				started = false;
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class Client implements Runnable {
		Socket socket = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean isConnected = false;

		public Client(Socket socket) {
			this.socket = socket;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				isConnected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void send(String string) {
			try {
				dos.writeUTF(string);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("有人退出了,已經移除");
			}
		}

		@Override
		public void run() {

			try {
				while (isConnected) {
					String string = dis.readUTF();
					System.out.println(string);
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.send(string);
					}
				}
			} catch (EOFException e) {
				System.out.println("a client closed");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				isConnected = false;
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (dos != null) {
					try {
						dos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
	}
}
