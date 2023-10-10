package edu.umg.domain.usuarios;

import edu.umg.datos.Conexion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    //Setencias de SQL
    private static final String SQL_INSERT = "INSERT INTO usuario(username, password) VALUES(?,?)";
    private static final String SQL_SELECT_BY_USER_PASS = "SELECT * FROM usuario WHERE username =? AND password = ?";
    private static final String SQL_SELECT_BY_USER = "SELECT * FROM usuario WHERE username = ?";

    //Mensaje de la notificacion para informar que la conexion fue exitosa
    private static final String SUCCESSFUL_CONNECTION_MESSAGE = "La Conexion con la base de Datos ha sido extisa";

    private Connection conexionTrans;


    // Este método se utiliza para insertar un nuevo usuario en la base de datos
    // a partir de un objeto UsuarioDTO que contiene la información del usuario.
    // Devuelve true si la inserción es exitosa y false en caso contrario.
    public boolean insertUsuario(UsuarioDTO usuario) throws SQLException {
        Connection conn = null; // Declaración de la conexión a la base de datos
        PreparedStatement stmt = null; // Declaración de la sentencia preparada

        try{
            conn = obtenerConexion(); // Obtener una conexión a la base de datos
            stmt = conn.prepareStatement(SQL_INSERT); // Preparar la sentencia SQL para la inserción, utilizando la consulta SQL definida en SQL_INSERT
            // Establecer los valores en los marcadores de posición de la sentencia preparada
            stmt.setString(1, usuario.getNombreUsuario()); // Primer marcador: nombre de usuario
            stmt.setString(2, encriptarContaseña(usuario.getContraseña())); // Segundo marcador: contraseña encriptada

            int rows = stmt.executeUpdate();  // Ejecutar la sentencia de inserción y obtener el número de filas afectadas
            return rows > 0; // Devolver true si al menos una fila fue insertada con éxito
        }finally {
            // Realizar la limpieza de recursos
            Conexion.close(stmt); // Cerrar la sentencia preparada
            cerrarConexion(conn);  // Cerrar la conexión a la base de datos
        }
    }

    // Este método se utiliza para validar las credenciales de un usuario en la base de datos.
    // Toma un nombre de usuario (username) y una contraseña (password) como parámetros y devuelve true si
    // las credenciales son válidas, es decir, si existe un registro en la base de datos con el mismo
    // nombre de usuario y contraseña.
    public boolean validarUsuario(String username, String password) throws SQLException{
        Connection conn = null; // Declaración de la conexión a la base de datos
        PreparedStatement stmt = null; // Declaración de la conexión a la base de datos
        ResultSet rs = null; // Declaración del conjunto de resultados
        boolean usuarioValido = false; // Variable para indicar si el usuario es válido o no

        try{
            // Obtener una conexión a la base de datos
            conn = obtenerConexion();
            // Preparar la sentencia SQL para la selección de un usuario por nombre de usuario y contraseña,
            // utilizando la consulta SQL definida en SQL_SELECT_BY_USER_PASS
            stmt = conn.prepareStatement(SQL_SELECT_BY_USER_PASS);
            // Establecer los valores en los marcadores de posición de la sentencia preparada
            stmt.setString(1, username); // Primer marcador: nombre de usuario
            stmt.setString(2, encriptarContaseña(password)); // Segundo marcador: contraseña encriptada

            // Ejecutar la sentencia de selección y obtener el conjunto de resultados
            rs = stmt.executeQuery();

            // Comprobar si existe al menos un resultado en el conjunto de resultados
            usuarioValido = rs.next();
        }finally {
            // Realizar la limpieza de recursos
            Conexion.close(rs); // Cerrar el conjunto de resultados
            Conexion.close(stmt); // Cerrar la sentencia preparada
            cerrarConexion(conn); // Cerrar la conexión a la base de datos
        }

        // Devolver true si se encontró un usuario válido, de lo contrario, devolver false
        return usuarioValido;
    }


    // Este método se utiliza para obtener la información de un usuario de la base de datos
    // a partir de su nombre de usuario (username).
    // Devuelve un objeto UsuarioDTO que contiene la información del usuario si se encuentra,
    // o null si no se encuentra.
    public UsuarioDTO ObtenerUsuario(String username) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        UsuarioDTO usuario = null;


        try{
            conn = obtenerConexion();

            // Preparar la sentencia SQL para la selección de un usuario por nombre de usuario,
            // utilizando la consulta SQL definida en SQL_SELECT_BY_USER
            stmt = conn.prepareStatement(SQL_SELECT_BY_USER);
            stmt.setString(1, username);

            // Ejecutar la sentencia de selección y obtener el conjunto de resultados
            rs = stmt.executeQuery();

            // Comprobar si existe al menos un resultado en el conjunto de resultados
            if (rs.next()){
                // Crear un objeto UsuarioDTO y asignarle los valores del resultado
                usuario = new UsuarioDTO();
                usuario.setNombreUsuario(rs.getString("username"));
                usuario.setContraseña(rs.getString("password"));
            }
        }finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            cerrarConexion(conn);
        }
        // Devolver el objeto UsuarioDTO que contiene la información del usuario
        // o null si no se encontró ningún usuario con el nombre de usuario proporcionado.
        return usuario;
    }





    private Connection obtenerConexion() throws SQLException{
        Connection conn = this.conexionTrans !=null ? this.conexionTrans: Conexion.getConnection();
        System.out.println(SUCCESSFUL_CONNECTION_MESSAGE);
        return conn;
    }

    private void cerrarConexion(Connection conn) throws SQLException{
        if (this.conexionTrans == null && conn != null){
            conn.close();
        }
    }

    private String encriptarContaseña(String contraseña){
        try{
            // Crear una instancia de MessageDigest para utilizar el algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Calcular el hash de la contraseña proporcionada como un arreglo de bytes
            byte[] hash = md.digest(contraseña.getBytes());

            // Crear un objeto StringBuilder para construir la representación hexadecimal del hash
            StringBuilder hexString = new StringBuilder();

            // Iterar a través de los bytes del hash y convertirlos a formato hexadecimal
            for(byte b : hash){
                hexString.append(String.format("%02x", b));  // Agregar cada byte al StringBuilder como una cadena hexadecimal de dos dígitos
            }

            // Devolver la representación hexadecimal del hash como una cadena
            return hexString.toString();
        }catch(NoSuchAlgorithmException e){

            // Capturar una posible excepción si el algoritmo SHA-256 no está disponible
            // y lanzar una RuntimeException con un mensaje de error
            throw  new RuntimeException("Ha ocurrido un error al encriptar la contraseña", e);
        }
    }

}
