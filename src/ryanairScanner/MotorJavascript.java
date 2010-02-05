package ryanairScanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MotorJavascript {
	
	//Calcula la posicion en el "<select" del "Valladolid" (funciona bien, pero mu lioso, mejor con jQuery)
	//Mejor con option[INDICE].text	 Specifies the text for the option.
	//			option[INDICE].value Specifies the value for the option
	//y RegEx en javascript: RegExp.test(string);   String.match(pattern);
	//http://www.javascriptkit.com/javatutors/redev3.shtml
	
	//Supongo q el submit lo podremos hacer siempre con "document.forms[0].submit();" (no lo he probado)
	private String Submit= "document.getElementById('AvailabilitySearchInputFRSearchView_ButtonSubmit').click();";
	private String Submit2= "document.getElementById('AvailabilityInputFRSelectView_ButtonSubmit').click();";
	
	private String VueloIda= "$(document).ready(function(){" +
	
								"document.getElementById('AvailabilitySearchInputFRSearchView_OneWay').click();" +
									//Origen
									"var posicion;" +
									"for(var i in Stations){" +
										"if(Stations[i].name=='%ORIGEN%') {" +
											//"alert(Stations[i].code);" +
											"cont=0;" +
											"for(var codStaAct in SortedStations){" +
												"if(SortedStations[cont] == Stations[i].code){" +
													"posicion=cont;" +
													//"alert('Posicion='+cont);" +
													"break;" +
												"}" +
												"cont++;" +
											"} " +
									"} };" +
									
									"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketOrigin1').options[posicion+1].selected=true;" +
									
									//Destino:
									"for(var i in Stations){" +
										"if(Stations[i].name=='%DESTINO%') {" +
											//"alert(Stations[i].code);" +
											"cont=0;" +
											"for(var codStaAct in SortedStations){" +
												"if(SortedStations[cont] == Stations[i].code){" +
													"posicion=cont;" +
													//"alert('Posicion='+cont);" +
													"break;" +
												"}" +
												"cont++;" +
											"} " +
									"} };" +
									
									"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketDestination1').options[posicion+1].selected=true;" +

									//Dia
									"var listaDia= document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketDay1');" +
									"for (var i=0; i<listaDia.options.length; i++){" +
										"if(listaDia.options[i].value=='%DIA%') listaDia.options[i].selected= true;" +
									"}" +
									//
									//"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketDay1').options[28].selected=true;" +
									
									//Año-Mes
									"var listaAnnoMes= document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketMonth1');" +
									"for (var i=0; i<listaAnnoMes.options.length; i++){" +
										"if(listaAnnoMes.options[i].value=='%ANNOMES%') listaAnnoMes.options[i].selected= true;" +
									"}" +
									
									"});";
									//"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketMonth1').options[4].selected=true;";
	
	/*String buscaRadios= "var inputes = document.getElementsByTagName('input');" +
							"for(var i=0;i<inputes.length; i++){" +
							" if (inputes[i].type=='radio') {alert(inputes[i].onclick); inputes[i].click();inputes[i].checked=true;}" +
							"}";*/	
	
	/*final String jsSelecVuelo = "document.getElementById('AvailabilitySearchInputFRSearchView_OneWay').checked = true;\r\n"+
								"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketOrigin1')[2].selected=true;\r\n"+
								"document.getElementById('AvailabilitySearchInputFRSearchView_DropDownListMarketDestination1')[1].selected=true;alert('zagna');\r\n"+
								"document.getElementById('AvailabilitySearchInputFRSearchView_ButtonSubmit').click();";*/
	
	
	
	
	
	
	public String getSubmit() {
		return Submit;
	}
	
	public String getSubmit2() {
		return Submit2;
	}
	
	public String getVueloIda() {
		return VueloIda;
	}
	
	
	/** Compone el codigo JS a ejecutar
	 * 
	 * @param Origen
	 * @param Destino
	 * @param fecha
	 * @return
	 */
	public String componJS(String Origen, String Destino, Calendar fecha) {
		//Ej: "Valladolid" - "Londres Stansted" "16" "2009-09"       (yyyy-MM-dd)
		DateFormat df1 = new SimpleDateFormat("dd");
		DateFormat df2 = new SimpleDateFormat("yyyy-MM");
		
		/*
		System.out.println(df1.format(fecha.getTime()));
		System.out.println(df2.format(fecha.getTime()));
		*/
		/*if(Destino=="Bruselas (Charleroi)")System.out.println(df1.format(fecha.getTime()));
		if(Destino=="Bruselas (Charleroi)")System.out.println(df2.format(fecha.getTime()));
		*/
		
		return getVueloIda().replace("%ORIGEN%", Origen).replace("%DESTINO%", Destino).replace("%DIA%", df1.format(fecha.getTime())).replace("%ANNOMES%", df2.format(fecha.getTime()));
		
	}
	
	
	
}

