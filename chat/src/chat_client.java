import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class chat_client extends Frame {
	Socket socket = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	TextField textField = new TextField();
	TextArea textArea = new TextArea();
	Thread thread = null;
	server s = null;
	String str = "";
	public static void main(String[] args) {
		new chat_client().launchFrame();
	}

	public void launchFrame() {
		setLocation(400, 400);
		this.setSize(300, 300);
		add(textField, BorderLayout.SOUTH);
		add(textArea, BorderLayout.NORTH);
		pack();// 使textField、textArea自动适应屏幕
		/**
		 * 点击关闭按钮，关闭窗口
		 */
		this.addWindowListener(new WindowAdapter() {// 匿名内部类:其实就是实现WindowAdapter抽象类的一个对象
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		textField.addActionListener(new textFieldListener());
		setVisible(true);
		connect();
		s = new server();
		thread = new Thread(s);
		thread.start();
	}

	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public void disconnect() {
		try {
			thread.stop();
			if (dis != null) {
				dis.close();
			}
			if (dos != null) {
				dos.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class textFieldListener implements ActionListener {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			try {
				// 向服务器写出数据
				str= textField.getText().trim();
				textField.setText("");
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	class server implements Runnable {

		public void run() {
			try {
				while (true) {
						// 从服务器读入数据
						String str = dis.readUTF();
						if (textArea.getText().trim().equals("")) {
							textArea.setText(str);
						} else {
							textArea.setText(textArea.getText().trim() + "\n" + str);
						}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
