package ryanairScanner;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class DB { 
	
	private static void inicilizaDB() {
		try {

			Class.forName("org.sqlite.JDBC");
			Connection connIni = DriverManager.getConnection("jdbc:sqlite:C:\\vuelos.s3db");
			Statement statIni = connIni.createStatement();
			//Borra la table (eliminar)
			statIni.executeUpdate("drop table if exists vuelos;");
			statIni.executeUpdate("CREATE TABLE [vuelos] (" +
								"[vuelosKey] INTEGER  PRIMARY KEY NOT NULL," +
								"[origen] TEXT  NULL," +
								"[destino] TEXT  NULL," +
								"[precio] TEXT  NULL," +
								"[fecha] DATE  NULL," +
								"[actualizado] DATE DEFAULT CURRENT_TIMESTAMP NULL" +
								")"	  );
			
			statIni.executeUpdate("CREATE TRIGGER [ON_TBL_VUELOS_] " +
								"AFTER INSERT ON [vuelos] " +
								"FOR EACH ROW " +
								"BEGIN " +
								"UPDATE vuelos SET actualizado = DATETIME('NOW', 'LOCALTIME')  WHERE rowid = new.rowid;" +
								"" +
								"END"  );
			                
			
			
			//Para borrar la el contenido de la tabla (vaciar)
			//statIni.executeUpdate("DELETE from vuelos WHERE 1;");
			connIni.close();
			
		} catch (SQLException sqlex) {
			System.out.println("Error SQL: " + sqlex.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private void getDatos() {
		try{
			
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\vuelos.s3db");
			
			//Lectura db
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from vuelos;");
			System.out.println("LECTURA ======");
			while (rs.next()) {
				System.out.println("fecha = " + rs.getDate("fecha").toGMTString() );
			}
			
			conn.close();
		}catch(SQLException sqlex){
			System.out.println("Error SQL: "+ sqlex.getMessage());
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	
	public static void setDatosVuelo(String origen, String destino, String precio, Calendar fechavuelo) {
		if(!(new File("C:\\vuelos.s3db").exists())){
			System.out.println("Vamos a inicializar la DB");
			inicilizaDB();
		}
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\vuelos.s3db");
			
			PreparedStatement prep = conn.prepareStatement(
			    "insert into vuelos (origen, destino, precio, fecha) values (?, ?, ?, ?);");
			
			prep.setString(1, origen);
			prep.setString(2, destino);
			prep.setString(3, precio);
			java.sql.Date sqlDate = new java.sql.Date(((java.util.Date)fechavuelo.getTime()).getTime());
			System.out.println(""+ fechavuelo.getTime().toString() +"\n"+ sqlDate.toString() +"\n\n");
			//Pasamos de java.util.Date a java.sql.Date
			//java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlDateVuelo = new java.sql.Date(fechavuelo.getTime().getTime());
			prep.setDate(4, sqlDateVuelo);
			prep.addBatch();
			
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();
		}catch(SQLException sqlex){
			System.out.println("Error SQL: "+ sqlex.getMessage());
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
}
	
/*
    try {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\test.sqlite"); 
        
        Statement stat = conn.createStatement();
        stat.execute("DELETE FROM autores");
        
        
//        INSERT INTO autores VALUES (1,'Ceballos');
//        INSERT INTO autores VALUES (2,'Deittel');
        
        
        PreparedStatement prep0 = conn.prepareStatement("CREATE TABLE autores (id_autor INT PRIMARY KEY,nombre VARCHAR(32));");
        conn.setAutoCommit(false);
        prep0.executeBatch();
        conn.setAutoCommit(true);
        
        PreparedStatement prep = conn.prepareStatement("INSERT INTO autores (id_autor,nombre) VALUES (?, ?);");
        prep.setInt(1,1);
        prep.setString(2,"Deitel");
        prep.addBatch();
        prep.setInt(1,2);
        prep.setString(2,"Ceballos");
        prep.addBatch();
        prep.setInt(1,3);
        prep.setString(2,"Joyanes Aguilar");
        prep.addBatch();
        
        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);

        ResultSet rs = stat.executeQuery("select * from autores;");
        while (rs.next()) {
            System.out.println("ID_AUTOR...: " + rs.getString("id_autor"));
            System.out.println("NOMBRE.....: " + rs.getString("nombre"));
            System.out.println("-----------------------------------");
        }
        rs.close();
        stat.close();
        conn.close(); 
         
    } catch (SQLException exec) {
        System.out.println(exec.getMessage());
    } catch (ClassNotFoundException ex) {
        System.out.println(ex.getMessage());
    }
	*/
	/*
	try{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\test2.db");
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table people (name, occupation);");
		stat.executeUpdate("insert into people values ('Gandhi', 'politics');");
		stat.executeUpdate("insert into people values ('Turing', 'computers');");
		stat.executeUpdate("insert into people values ('Wittgenstein', 'smartypants');");
	
		ResultSet rs = stat.executeQuery("select * from people;");
		while (rs.next()) {
			System.out.println("name = " + rs.getString("name"));
			System.out.println("occupation = " + rs.getString("occupation"));
		}
		rs.close();
		conn.close();	
	}catch (Exception exec) {
	    System.out.println(exec.getMessage());
	}

}*/





/*

import java.sql.*;

public class Test {
  public static void main(String[] args) throws Exception {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
      Statement stat = conn.createStatement();
      stat.executeUpdate("drop table if exists people;");
      stat.executeUpdate("create table people (name, occupation);");
      PreparedStatement prep = conn.prepareStatement(
          "insert into people values (?, ?);");

      prep.setString(1, "Gandhi");
      prep.setString(2, "politics");
      prep.addBatch();
      prep.setString(1, "Turing");
      prep.setString(2, "computers");
      prep.addBatch();
      prep.setString(1, "Wittgenstein");
      prep.setString(2, "smartypants");
      prep.addBatch();

      conn.setAutoCommit(false);
      prep.executeBatch();
      conn.setAutoCommit(true);

      ResultSet rs = stat.executeQuery("select * from people;");
      while (rs.next()) {
          System.out.println("name = " + rs.getString("name"));
          System.out.println("job = " + rs.getString("occupation"));
      }
      rs.close();
      conn.close();
  }
}


*/
