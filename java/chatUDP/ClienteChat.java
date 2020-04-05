package chatUDP;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
//import java.util.*;
import javax.swing.*;

public class ClienteChat extends Frame{
	
	Socket socket;
	
	JFrame frameJ;
	JPanel panel;
	
	List conversacion;
	JTextField mensajeS;
	TextField orden;
	PrintWriter out;
	BufferedReader in;
	String host;
	String usuario;
	int port;
	String tituloSala = "Chat";
	
	public ClienteChat() {};
	
	public void iniciarSala() {
		frameJ = new JFrame(); // Inicializacion del objeto
		frameJ.setTitle(tituloSala); // Titulo del frame
		frameJ.setPreferredSize(new Dimension(600,300)); // Dimension del frame
		frameJ.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		orden = new TextField(40); // Ingreso de datos
		conversacion = new List(20,false); // Objeto que mostrara los mensajes del servidor
		mensajeBienvenida(); // Cargar mensaje de bienvendia a la lista
		panel = new JPanel(); // Inicializacion del objeto
		panel.setLayout(new BorderLayout()); // Establecer el tipo de panel como BorderLayout
		panel.add(conversacion, BorderLayout.CENTER); // Agregar a las posiciones deseadas
		panel.add(orden, BorderLayout.PAGE_END);
		frameJ.getContentPane().add(panel); // Agregar componentes al frame
		// Salida segura con el servidor
		frameJ.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				salida(); 
			}
		});
		// Cada vez que el usuario ingrese "Enter" los mensajes seran enviados
		orden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviar();
				orden.setText("");
			}
		});
		
		frameJ.pack(); // Empaquetar componentes
		frameJ.setVisible(true); // Mostrar los componentes del frame
	}
	
	public String obtenerRespuesta() {
		String s = "";
		try {
			s = in.readLine(); // Respuesta del servidor
			System.out.println("-" + s);
		}catch(IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public void mensajeBienvenida() {
		String mensajeB [] = new String [4];
		mensajeB[0] = "Bienvenido " + usuario + " al chat de desocupados";
		mensajeB[1] = "Los mensajes se leen de abajo para arriba >.<";
		mensajeB[2] = "Para cambiar de nombre ingresa '/n:' (sin comillas) mas el nombre";
		mensajeB[3] = "-----------------------------------------------------------------";
		for(int m = 0; m < mensajeB.length; m++) conversacion.add(mensajeB[m],0);
	}
	
	public void iniciarSocket() {
		try {
			socket = new Socket(host, port); // Inicializacion del socket y sus variables correspondientes
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch(IOException e) {
			System.out.println("El socket no se configuro correctamente");
			System.exit(1);
		}
	}
	
	public String procesar(String s) {
		System.out.println(s); // Facilitar bug!
		if(s.equals("/nt")) return "SERVIDOR> Nickname en uso, pruebe con otro"; //Validar que el usuario no exista
		else return s;
	}
	
	public void enviarNick() {
		out.println("/n:" + usuario); // Enviar nombre de usuario
		out.flush(); // Limpiar la variable
		String s = "";
		while(true) {
			s = obtenerRespuesta();
			if(s.equals("/nt") || s.substring(0, 3).equals("/nt")) {
				System.out.println("Usuario ya en uso");
				System.out.println("Otro usuario lo adquirio primero");
				break;
			}
			if(s.equals("SERVIDOR: '" + usuario + "' ha ingresado a la sala")) {
				System.out.println("Usuario asignado");
				System.out.println("Apuesta contrareloj ganada");
				break;
			}
		}
	}
	
	public void enviar() {
		out.println(orden.getText());
		out.flush();
	}
	
	public void salida() {
		out.println("/d");
		out.flush();
		System.exit(0);
	}
	
	public void ejecutarInicio(String host, int port, String nick) {
		this.host = host;
		this.port = port;
		usuario = nick;
		iniciarSocket();
		enviarNick();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				iniciarSala(); // Se carga el frame
				Thread hiloApplet = new Thread() {
					public void run() {
						while(true) {
							System.out.println("Respuesta del servidor");
							// Se inicia el metodo que esta siempre a la escucha del servidor
							conversacion.add(procesar(obtenerRespuesta()), 0);
						}
					}
				};
				hiloApplet.start(); // Creacion de hilo para evitar conflicto con la actualizacion
			}
		});
	}
}
