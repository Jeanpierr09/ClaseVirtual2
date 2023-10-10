package edu.umg.test;

import edu.umg.datos.Conexion;
import edu.umg.datos.PersonaJDBC;
import edu.umg.domain.Persona;
import java.sql.*;
import java.util.Scanner;

public class ManejoPersonas {

    public static void main(String[] args) {

        //definimos la variable conexion
        Connection conexion = null;
        try {
            conexion = Conexion.getConnection();
            //el autocommit por default es true, lo pasamos a false
            if (conexion.getAutoCommit()) {
                conexion.setAutoCommit(false);
            }

            PersonaJDBC personaJdbc = new PersonaJDBC(conexion);

            //se inserta sin telefono ni correo
//            Persona nuevaPersona = new Persona();
//            nuevaPersona.setNombre("Luis");
//            nuevaPersona.setApellido("Garcia");
//            personaJdbc.insert(nuevaPersona);
//            conexion.commit();
//            System.out.println("Se ha hecho commit de la transaccion");

            Scanner sc = new Scanner(System.in);

//vamos a actalizar sus datos
            Persona cambioPersona = new Persona();
            cambioPersona.setId_persona(1);
            cambioPersona.setNombre("Luis");
            cambioPersona.setApellido("Garcia");
            cambioPersona.setEmail("nuevocorreo@gmail.com");
            cambioPersona.setTelefono("555555555");
            personaJdbc.update(cambioPersona);
            //pausa con sc
            System.out.println("enter para continuar");
            sc.nextLine();
            conexion.commit();
            System.out.println("Se ha hecho commit de la transaccion");


        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            System.out.println("Entramos al rollback");
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace(System.out);
            }
        }
    }
}
