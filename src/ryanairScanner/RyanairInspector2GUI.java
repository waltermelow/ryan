package ryanairScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;


public class RyanairInspector2GUI{
	private Composite compIzq;
	private Composite compDer;
	private TabFolder tabFolder;
	private TabItem itemT1;
	private TabItem itemT2;
	private TabItem itemT3;

	Display display; //= Display.getDefault();
	private Shell shell;
	ArrayList<Navegador> navegadores = new ArrayList<Navegador>();
	private static Table table;
	
	final static String rutaActual= System.getProperty("user.dir")+"\\src\\ryanairScanner\\";
	final static String nomFichConsultas= "dataConsultas.txt";
	final static int SIZE = 10000, NP = 1, NC = 3;
	
	static TreeSet<Vuelo> vuelos;
	private RyanairInspector2 unvueloRyanair;
	
	Buffer bufferVuelos = null;
	Buffer bufferNavegadores = null;
	

	/*****************************  M A I N  *************************************/
	public static void main(String [] args) {
		
		RyanairInspector2GUI ryanairinspector2GUI= new RyanairInspector2GUI();
		
	}

	/*********************** CONSTRUCTOR *****************************/
	public RyanairInspector2GUI() {
		
		bufferNavegadores = new Buffer(new CircularQueue(SIZE));
		for (Navegador n : navegadores) {
			bufferNavegadores.Put(n);
		}
		
		bufferVuelos = new Buffer(new CircularQueue(SIZE));
		System.out.println(rutaActual);
		System.out.println("-- Empieza lectura fichero --");
		vuelos = UtilsIO.leerFicheroDatosConsultaVuelos(rutaActual + nomFichConsultas);
		for (Vuelo v : vuelos) {
			bufferVuelos.Put(v);
			System.out.println(v.toString());
		}
		System.out.println("-- Termina lectura fichero --");
		
		/*__ CREAMOS UNA INSTANCIA PARA CAPTURAR UN VUELO */
		unvueloRyanair= new RyanairInspector2();
		
		creaContenidos();
		
		//Iniciamos la carga de la pagina
		iniciaCaptura(table); 
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		//System.out.println("despues de dispose");
		
	}
	
	
	/** *************************************************************** */
	private void creaContenidos() {
		display = new Display ();
		shell = new Shell();
		
		//EN CASO DE NO RECUPERAR BIEN TOD0 EL CODIGO CNO browser.getText(); lo hacemos con el DOM mediante JAVASCRIPT
		//System.out.println( (String)brw.evaluate("return document.forms[0].innerHTML;") );
		//
		//if ((Boolean)brw.evaluate("return raise('SelectFare', new SelectFareEventArgs(1, 1, 'G'));")) System.out.println("raise() EJEC CORRECMT");;
		
		
		final String html = "<html><title>Snippet</title><body><p id='myid'>Best Friends</p><p id='myid2'>Cat and Dog</p></body></html>";
		//final String jsSelecValladolid = "var i=0; while(Stations[i]!=null){; i++;}";
		
		//Ej de jQuery que funciona: Devuelve el valor del listbox de "Origen Vuelo" selecionado
		final String jQueryRyan= "var orig=$(\"select[@id*='Origin1']\").val(); alert(orig);";
		//"$(\"select[@id*='Origin1']\")[posicion].attr('selected', 'true');";
		

		/*****       VARIABLES  !!!VENTANA¡¡¡      *****/
		
		//SYSTEM TRAY
		Image image = null;
		try {
			//image = new Image(display, /*rutaActual*/"C:\\"+"fr.ico");
			image = new Image(display, rutaActual + "fr.ico");
		} catch (Exception e) {
			e.getMessage();
		}
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println ("El system tray no está disponible");
		} else {
			final TrayItem item = new TrayItem (tray, SWT.NONE);
			item.setToolTipText("Ryanair Inspector v2");
			item.setImage(image);
			//Click simple en en icono
			item.addListener (SWT.Selection, new Listener () {
				public void handleEvent (Event event) {
					//System.out.println("selection");
				}
			});
			//Doble click en en icono
			item.addListener (SWT.DefaultSelection, new Listener () {
				public void handleEvent (Event event) {
					//System.out.println("default selection");
					//mio
					if(shell.getVisible()) shell.setVisible(false);
					else shell.setVisible(true);
					//shell.setMinimized(false);
					//shell.setMinimized(true);
				}
			});
			
		}
		
		//SHELL
		shell.setSize(1054, 727);
		//shell.setLayout(new FillLayout());
		shell.setText("Ryanair checker");
		shell.setImage(image);
		//Con esto al agrandar y empequeñecer se reorganizan los componentes
		//shell.setLayout(new FillLayout());
		
		
		//CREAMOS CONTENEDOR
		/***************************************************************************/
		compIzq = new Composite(shell, SWT.NONE);
		compIzq.setBounds(0, 0, 765, 691);
		final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginWidth = 200;
		fillLayout.marginHeight = 200;
		//comp.setLayout(fillLayout);
		//comp.setSize(200, 200);
		
		compDer = new Composite(shell, SWT.NONE);	
		compDer.setBounds(771, 0, 267, 691);
		
		//CREAMOS CAJA TEXTO Y BOTON PARA HTML OBTENIDO
		/***************************************************************************/
		final Text txtGetHTML = new Text(compDer, SWT.MULTI);
		txtGetHTML.setBounds(0, 0, 251, 198);
		
		
		//RyanairInspector2.class.getResource("RyanairInspector2.class").getPath();
		//System.getProperty("user.dir")+"\\bin\\calculadora\\";
		txtGetHTML.setText(RyanairInspector2.class.getResource("RyanairInspector2.class").getPath() + "\n" + System.getProperty("user.dir"));
		
		
		final Button button = new Button(compDer, SWT.NONE);
		button.setText("button");
		
		//TABS
		/***************************************************************************/
		tabFolder = new TabFolder (compIzq, SWT.BORDER);
		itemT1 = new TabItem (tabFolder, SWT.NONE);
		itemT2 = new TabItem (tabFolder, SWT.NONE);
		itemT3 = new TabItem (tabFolder, SWT.NONE);
		itemT1.setText ("Resultado");
		itemT2.setText ("Debugger");
		itemT3.setText ("Hilos");
		


		
		//TABLE CON DOS COLUMNAS
		/***************************************************************************/
		table = new Table(tabFolder, SWT.VIRTUAL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Origen");
		column1.setWidth(200);
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Destino");
		column2.setWidth(200);
		final TableColumn column3 = new TableColumn(table, SWT.NONE);
		column3.setText("Fecha");
		column3.setWidth(200);
		final TableColumn column4 = new TableColumn(table, SWT.NONE);
		column4.setText("Precio");
		column4.setWidth(150);
		
		/* Metemos elementos en la tabla
		TableItem fila = new TableItem(table, SWT.NONE);
		fila.setText(new String[] { "a", "b", "c", "d" });
		*/
		
		//METEMOS EL TABLE EN LA TAB2
		itemT1.setControl(table);
		tabFolder.setBounds(0, 0, 700, 691);
		tabFolder.pack();		
		
		
		
		
		
		
		//CREAMOS BROWSER
		/***************************************************************************/
		
		try {
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: " + e.getMessage());
			return;
		}
		//CREAMOS BROWSER (2)
		/***************************************************************************/
		//FUNCIONA BIEN SIN TABS: browser = new Browser(compIzq, SWT.BORDER);
		
		//El Normal
		navegadores.add(new Navegador(tabFolder, SWT.BORDER));
		//El Mozilla (hay q ejecutar el xulrunner.exe --register-global)
		//browser = new Browser(tabFolder, SWT.MOZILLA);
		//browser.setSize(1000, 800);
		navegadores.get(0).setBounds(0, 0, 787, 691);
		//browser.setLocation(500, 900);
		/*final FillLayout fillLayoutB = new FillLayout(SWT.VERTICAL);
		fillLayoutB.marginWidth = 200;
		fillLayoutB.marginHeight = 200;
		browser.setLayout(fillLayoutB);
		 */
		
		//METEMOS EL BROWSER EN LA TAB1
		itemT2.setControl(navegadores.get(0));//prueba18-6-09
		//browser.setVisible(false);
		
		tabFolder.setBounds(0, 0, 766, 691);
		tabFolder.pack();		

		navegadores.add(new Navegador(tabFolder, SWT.BORDER));
		navegadores.get(1).setBounds(0, 0, 787, 691);
		itemT3.setControl(navegadores.get(1));

		
		
		//CADA VEZ!!! QUE TERMINE DE CARGAR
		navegadores.get(0).addProgressListener(new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				if(!postSubmit(navegadores.get(0), table)){
					System.out.println("ERROR: Problema al ejecutar la gestion de los datos, finalizar la carga de la pagina.");
				}
			}
		});
		
		//CADA VEZ!!! QUE TERMINE DE CARGAR
		navegadores.get(1).addProgressListener(new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				if(!postSubmit(navegadores.get(1), table)){
					System.out.println("ERROR: Problema al ejecutar la gestion de los datos, finalizar la carga de la pagina.");
				}
			}
		});
		
		
		//BOTON OBTENER HTML
		/***************************************************************************/
		final Button btnGetHTML = new Button(compDer, SWT.PUSH);
		btnGetHTML.setBounds(0, 204, 151, 30);
		btnGetHTML.setText("Obtener codigo html");
		btnGetHTML.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				
				
				addBrowser();
				
				String result = navegadores.get(0).getText();
				txtGetHTML.setText(result);
			}
		});
		
		
		
		//CREAMOS CAJA TEXTO PARA EJECUTAR JS
		/***************************************************************************/
		/*final Text txtJS = new Text(compDer, SWT.MULTI);
		txtJS.setText("var newNode = document.createElement('P'); \r\n"+
				"var text = document.createTextNode('At least when I am around');\r\n"+
				"newNode.appendChild(text);\r\n"+
				"document.getElementById('myid').appendChild(newNode);\r\n"+
				"document.bgColor='yellow';\r\n"+
				"\r\n"+
				"document.getElementById('AvailabilitySearchInputFRSearchView_OneWay').checked = true;\r\n"+
				"document.getElementById('AvailabilitySearchInputFRSearchView_ButtonSubmit').click();\r\n"+
				"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketOrigin1')[2].selected=true;\r\n"+
				"Destination1");
		*/
		
		

		//BOTON LANZAMOS RYANAIR 
		/***************************************************************************/
		/*final Button btnCargarRyan = new Button(compDer, SWT.PUSH);
		btnCargarRyan.setText("Cargar RYANAIR");
		btnCargarRyan.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				//boolean result = browser.execute(text.getText());
				boolean result = browser.setUrl("http://www.bookryanair.com/SkySales/FRSearch.aspx?culture=ES-ES&pos=HEAD");
				if (!result) {
					// Script may fail or may not be supported on certain platforms.
					System.out.println("Script was not executed.");
				}
			}
		});
		*/
		
		//BOTON EJECUTAMOS JAVASCRIPT
		/***************************************************************************/
		final Button btnEjeJS = new Button(compDer, SWT.PUSH);
		btnEjeJS.setBounds(0, 266, 151, 30);
		btnEjeJS.setText("Ejecutar JAVASCRIPT");
		btnEjeJS.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
			}
		});
		
		
		//image.dispose(); //con esto no funciona el shell.setImage(image)
		

	}
	
	
	/** Introduce una fila en el ListView (tabla) del entorno gráfico
	 * 
	 * @param fila
	 */
	public void setFilaTable(String[] fila) {
		TableItem elemento = new TableItem(table, SWT.NONE);
		elemento.setText(fila);
	}
	
	/******************************************************************/
	public boolean addBrowser() {
		Browser browser2;
		browser2 = new Navegador(this.tabFolder, SWT.BORDER);
		browser2.setBounds(0, 0, 787, 691);
		this.itemT3.setControl(browser2);
		
		// METEMOS EL BROWSER EN LA TAB1
		this.itemT3.setControl(browser2);// prueba18-6-09
		// browser.setVisible(false);
		browser2.setUrl("www.google.es");

		this.tabFolder.setBounds(0, 0, 766, 691);
		this.tabFolder.pack();
		
//		shell.open();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
//		display.dispose();
		browser2.execute("alert('no entra aki')");
		browser2.execute("alert('no entra aki')");
		
		
		return true;
	}



	/******************************************************************/
	//Gestiona el evento Fin de la carga de la pagina del BROWSER
	private boolean iniciaCaptura(Table table) {
		
		for (Vuelo v : vuelos) {
			if(v.isSinEmpezar()){ //Interpretemos la marca de si se ha empezado o no a capturar este vuelo
				boolean encontrado= false;
				for (Iterator<Navegador> it = navegadores.iterator(); it.hasNext() && !encontrado;) {
					Navegador n = (Navegador) it.next();
					if(!n.isOcupado()){ //Si el navegador está libre
						//unvueloRyanair.
						//encontrado= true;
					}
					
				}
			}
		}

		/*
		Vuelo v= (Vuelo)bufferVuelos.Get();
		if(v != null){ //Si hay algun vuelo sin capturar
			
			Navegador n= (Navegador)bufferNavegadores.Get();
			if(n != null){ //Si hay algun navegador
			
				while(v.isSinEmpezar()){
					
				}
			
		}*/
		
		
		/*
		if( hiloConsultas < vuelos.size() ){
			Iterator<Vuelo> it = vuelos.iterator();
			Vuelo v = null;
			int i=-1;
			while (it.hasNext() && i<hiloConsultas) {
				v = (Vuelo) it.next();
				i++;
				System.out.print(i);
			}
			System.out.println("\nCONSULTA SOBRE VUELO:  "+v);
			
			boolean respCapturaVuelo= unvueloRyanair.capturaVuelo(brw, table, v.getOrigen(), v.getDestino(), v.getFecha());
			System.out.println("respCapturaVuelo="+respCapturaVuelo);
			if(respCapturaVuelo) {
				
	
				Browser.clearSessions(); //Borra todas las cookies de la sesion 
				
				hiloConsultas++;
				if(hiloConsultas<vuelos.size()){
					it = vuelos.iterator();
					i=-1;
					while (it.hasNext() && i<hiloConsultas) {
						v = (Vuelo) it.next();
						i++;
						System.out.print(i);
					}
					System.out.println("\nCONSULTA SOBRE VUELO:  "+v);
				}else {
					brw.stop();
					System.out.println("brw stop 1");
				}
				
				inicializaCaptura(brw, table);
				//numPantalla=0;
				//gestionaBrowserFin(brw);
				//brw.setUrl("http://www.bookryanair.com/SkySales/FRSearch.aspx?culture=ES-ES&pos=HEAD");
			}
			
			
		}else {
			brw.stop();
			System.out.println("brw stop 2");
		}
		*/
		
		return true;
	}
	
	private boolean postSubmit(Navegador nvg, Table table) {
		return false;
	}
	
	
	
	/*
	 * 1. Inicia captura -> Recoje nextVuelo() -> marca Vuelo en progreso
	 * 2. Lo manda al Browser desocupado, set Vuelo Browser ocupado
	 * 3. callback postSubmit()
	 * 4. postSubmit() -> ejecuta siguiente paso sobre vuelo-browser
	 * 5. callback postSubmit() FIN -> libera browser, marca vuelo finalizado
	 * 
	 * */
	
	
	
	
}
