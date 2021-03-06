package ryanairScanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.*;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class RyanairInspector2{

	static final int ERROR_GENERICO = -99;
	static final int ERROR_PASO_2 = -2;
	static final int OK_TODO = 5;
	
	boolean ocupado= false;
	static int numPantalla = 0;
	int hiloConsultas = 0;
	String origenActual= "";
	String destinoActual= "";
	Calendar fechaActual;
	
	private MotorJavascript motorJS = new MotorJavascript();
//	private testThread testthread = new testThread();
	

	
	//final static String rutaActual= "calculadora/";
	//final static String rutaActual= System.getProperty("user.dir")+"\\bin\\calculadora\\";
	//final static String rutaActual= System.getProperty("user.dir")+"\\"; //EL BUENO
	final static String rutaActual= System.getProperty("user.dir")+"\\src\\ryanairScanner\\";
	
	//final static String rutaActual= RyanairInspector2.class.getResource("RyanairInspector2.class").getPath();
	//final static String rutaActual= System.getProperty("user.dir")+"\\bin\\calculadora\\";
	final static String nomFichConsultas= "dataConsultas.txt";
	
	
	

	
	/*********************** CONSTRUCTOR *****************************/
	public RyanairInspector2() {
			

		
        /*
	    // Create producers
	    for (int i=0; i < NP; i++)
	      new Producer(b, i).start();
		*/
	    // Create consumers
		/*
	    for (int i=0; i < NC; i++)
	      new Consumer(b, i).start();
		*/
		
		
		//TODO Aqui nunca llega... se queda en espera el GUI
//		System.out.println("ESTO SE EJECUTA .... DESPUES DEL iniGUI()");
		
	}

	
	/******************************************************************/
	protected boolean capturaVuelo(Browser brw, Table table, Vuelo vuelo) {
		boolean resultado = false;
		String Origen= vuelo.getOrigen();
		String Destino= vuelo.getDestino();
		Calendar fecha= vuelo.getFecha();
		
		if(numPantalla==0){
			if(!brw.setUrl("http://www.bookryanair.com/SkySales/FRSearch.aspx?culture=ES-ES&pos=HEAD")){
				System.out.println("Error al caragar la p�gina principal de busqueda de vuelos Ryanair.");
			}else{
				numPantalla++;
			}
			System.out.println("Fin numPantalla = 0 con "+Destino);
				
		}
		else if(numPantalla==1){
			
			if (!brw.execute(motorJS.componJS(Origen, Destino, fecha))) {
				System.out.println("componJS.\n"+motorJS.componJS(Origen, Destino, fecha));
				System.out.println("ERROR: ejecutando el javascript de la selecci�n del vuelo.");
			}else if(!brw.execute(motorJS.getSubmit())){
					System.out.println("ERROR: ejecutando el submit.");
				}else{
					//Se ha ejecutado CORRECTAMENTE
					numPantalla++;
					//Obtenemos los Calendar del fechas.txt con sus intervalos de fechas
					//Llamamos a componJS por cada fecha
					
				}
			System.out.println("Fin numPantalla = 1 con "+Destino);
			
			
		}else if(numPantalla==2){
			
			Pattern pat= Pattern.compile("No hay vuelos");
			Matcher m= pat.matcher(brw.getText().replaceAll("[\r\n]", "")); //Le quitamos los retornos de carro, para que realice bien la busqueda
			if(m.find()){
				//Si NO hay vuelo ese dia
				numPantalla= 0;
				resultado= true;
				setFilaTable(table, new String[] { Origen, Destino,  UtilsFechas.getFechaEDDMMYYYY(fecha), "NO_VUELO" });
				//TODO metemos en la db los dias sin vuelo?
				//new String[] { "a", "b", "c", "d" }
				System.out.println("NO HAY VUELO " +Origen+ " - " +Destino+ " el dia "+fecha.getTime());
			}else {
				//Si hay vuelo ese dia
				String jsTemp = motorJS.getSubmit2();
				if(!brw.execute(jsTemp)){
					System.out.println("Error al ejecutar el submit de la pantalla: "+(numPantalla+1));
				}else{
					//Se ha ejecutado CORRECTAMENTE
					numPantalla++;
				}
				System.out.println("Fin numPantalla = 2 con "+Destino);
			}
			
		}else if(numPantalla==3){
			Pattern pat= Pattern.compile("Importe total del vuelo.*[0-9]*(,|\\.)[0-9]*(| )(EUR|L|�|GBP|SEK|NOK|LTL|LVL|PLN|CZK|HUF|DKK)");
			Matcher m= pat.matcher(brw.getText().replaceAll("[\r\n]", "")); //Le quitamos los retornos de carro, para que realice bien la busqueda
			
			String precio="";
			//Buscamos cadenas coincidentes con la RegEx
			int contador= 0;
			while (m.find()){
				//Si hay siguiente patron encontrado
				contador++;
				//System.out.println(m.group());
				Pattern pat2= Pattern.compile("[0-9]*(,|\\.)[0-9]*(| )(EUR|L|�|GBP|SEK|NOK|LTL|LVL|PLN|CZK|HUF|DKK)");
				Matcher m2= pat2.matcher(m.group());
				while (m2.find()) {
					precio= m2.group();
					System.out.println(precio);
					setFilaTable(table, new String[] { Origen, Destino,  UtilsFechas.getFechaEDDMMYYYY(fecha),  precio});
					//TODO db (pero bien...) aqu�
					//Date Dfecha = fecha.getTime();
					DB.setDatosVuelo(Origen, Destino, precio, fecha );
					resultado= true;
					
				}
			}
			
			//Informamos de errores
			if(contador==0) System.out.println("ERROR: no se ha encontrado importe alguno en la p�gina.");
			else if(contador!=1) System.out.println("ERROR: se han encontrado m�s importes ("+m.groupCount()+") de los esperados en la p�gina.");
			
			
			//numPantalla++;
			numPantalla= 0;
			System.out.println("Fin PASOS numPantalla = 3 con "+Destino);
			System.out.println("--------------------------------------");
			
		}else{
			brw.execute("alert('Completado paso "+numPantalla+"');");
		}
	
		return resultado;
	}
	

	
	/******************************************************************/
	private static void addFechaFichero(Calendar fecha) {
		
	}
	
	/******************************************************************/
	private static void addFechasFichero(Set fechas) {

	}
	
	


	
	private void setFilaTable(Table table ,String[] fila) {
		TableItem elemento = new TableItem(table, SWT.NONE);
		elemento.setText(fila);
	}
	

}


/* CREAR UN FICHERO
		// Creamos el objeto que encapsula el fichero
	   File fichero = new File("c:\\temp\\miFichero.txt");

	   try {
	      // A partir del objeto File creamos el fichero f�sicamente
	      if (fichero.createNewFile())
	        System.out.println("El fichero se ha creado correctamente");
	      else
	        System.out.println("No ha podido ser creado el fichero");
	    } catch (IOException ioe) {
	      ioe.printStackTrace();
	    }
 */
