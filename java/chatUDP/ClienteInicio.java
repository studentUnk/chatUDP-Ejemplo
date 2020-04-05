package chatUDP;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClienteInicio extends JPanel{
	
	Socket socket; // Socket para manejar la conexion
	
	JFrame frameJ; // Frame del applet
	JLabel label1; // Nombre o cadena de texto
	JPanel panel; // Panel para organizar los componentes
	JButton botonIngreso; // Boton para procesar los datos
	
	JTextField mensajeS; // Espacio en el que el usuario puede ingresar datos
	PrintWriter out; // Variable que emite los mensajes de salida
	BufferedReader in; // Variable que procesa los mensajes de entrada
	String host; // El host al cual se hara la conexion
	int port; // El puerto al cual se hara la conexion
	String tituloIngreso = "Ingreso al chat"; // Titulo del applet
	
	public ClienteInicio() {};
	
	public void iniciarIngreso() {
		frameJ = new JFrame(); // Inicializacion del objeto
		frameJ.setTitle(tituloIngreso); // Titulo del applet
		frameJ.setPreferredSize(new Dimension(600,300)); // Tamano deseado
		frameJ.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cuando se de click en cerrar debe cerrar el frame
		panel = new JPanel(); // Inicializacion del objeto
		panel.setLayout(new GridLayout(0,1)); // Se establece el tipo de layout como GridLayout
		mensajeS = new JTextField(); // Incializacion del objeto
		mensajeS.setPreferredSize(new Dimension(100,20)); // Se establece dimension deseada
		label1 = new JLabel("Ingrese el nombre que desea"); // Cadena de texto para el usuario
		botonIngreso = new JButton("Ingresar al chat"); // Boton para enviar solicitud de acceso
		botonIngreso.addActionListener(new ActionListener() { //Metodo de escucha del boto
			public void actionPerformed(ActionEvent e) {
				enviarNick(); // Enviar solicitud al servidor
			}
		});
		// Agregar componentes al panel
		panel.add(label1);
		panel.add(mensajeS);
		panel.add(botonIngreso);
		frameJ.addWindowListener(new WindowAdapter() { //Accion para el frame
			public void windowClosing(WindowEvent e) {
				salida(); // Enviar mensaje de salida al servidor, aunque no se haya agregado usuario
			}
		});
		frameJ.getContentPane().add(panel); // Agregar panel al frane
		frameJ.pack(); // Empaquetar
		frameJ.setVisible(true); // Hacer el frame visible
	}
	
	public String obtenerRespuesta() {
		String s = "";
		try {
			s = in.readLine(); //Respuesta del servidor
			System.out.println("-" + s);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public void iniciarSocket() {
		try {
			socket = new Socket(host, port); // Inicializacion del socket
			// Se asocian variables de entrada y salida con el socket
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch(IOException e) {
			System.out.println("El socket no se configuro correctamente");
			System.exit(1);
		}
	}
	
	public void cerrarVentana(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				salidaF();
			}
		});
	}
	
	public void enviarNick() {
		out.println("/n:" + mensajeS.getText()); // Enviar nuevo usuario al servidor
		out.flush(); // Limpiar variable de salida
		String s = "";
		while(true) {
			s = obtenerRespuesta();
			if(s.equals("/nt") || s.substring(0, 3).equals("/nt")) { // El nombre de usuario ya esta en uso
				System.out.println("Usuario ya en uso");
				JOptionPane.showMessageDialog(frameJ, "El nombre ingresado ya esta en uso", "Ingreso fallido", JOptionPane.ERROR_MESSAGE);
				break;
			}
			// El nombre de usuario esta permitido
			if(s.equals("SERVIDOR: '" + mensajeS.getText() + "' ha ingresado a la sala")) {
				System.out.println("Usuario asignado");
				JOptionPane.showMessageDialog(frameJ, "Su nombre ha sido aceptado", "Ingreso exitoso", JOptionPane.INFORMATION_MESSAGE);
				frameJ.dispose(); // Cerrar frame actual
				ClienteChat cc = new ClienteChat();
				salidaF(); // Enviar mensaje de salida
				try{
					socket.close(); // Cerrar el socket para evitar errores
					System.out.println("Socket cerrado");
				} catch(IOException e) {
					e.getStackTrace();
					System.out.println("Socket no cerrado");
				}
				cc.ejecutarInicio(host, port, mensajeS.getText()); // Iniciar nuevo frame
				break;
			}
		}
	}
	
	public void salida() {
		out.println("/d"); // Mensaje de salida correcto para el servidor
		out.flush();
		System.exit(0);
	}
	
	public void salidaF() {
		out.println("/d");
		out.flush();
	}
	
	public void ejecutarInicio() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				iniciarIngreso(); // Iniciar construccion del frame
			}
		});
	}
	
	public static void main(String [] args) {
		ClienteInicio ci = new ClienteInicio();
		ci.host = (args.length > 0) ? args[0]:"localhost"; // Escoger direccion introduciendo argumento al ejecutar
		ci.port = (args.length > 1) ? Integer.parseInt(args[1]) : 54321; // Puerto por defecto
		ci.iniciarSocket();
		ci.ejecutarInicio();
	}
}
