package edu.umg.domain.usuarios;

import java.sql.Connection;

public class UsuarioDTO {

    private String nombreUsuario;
    private String contraseña;

    public UsuarioDTO(){

    }
    //Constructor
    public UsuarioDTO(String nombreUsuario, String contraseña){
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString(){
        return "UsuarioDTO" + "Nombre de Usuario" + nombreUsuario + "Contraseña" + contraseña;
    }
}
