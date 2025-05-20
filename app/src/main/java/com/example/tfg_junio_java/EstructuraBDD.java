package com.example.tfg_junio_java;

public class EstructuraBDD {
    public static final String tabla_usuarios= "DatosUsuarios";
    public static final String column_id= "id";
    public static final String column_usuario= "usuario";
    public static final String column_contraseña= "contraseña";
    public static final String column_nombre= "nombre";
    public static final String column_fechaNac= "fechaNacimiento";
    public static final String column_avatar= "avatar";




    public static final String SQL_Entar_Datos= "CREATE TABLE " + EstructuraBDD.tabla_usuarios + " (" +
            EstructuraBDD.column_id + " INTEGER PRIMARY KEY, " +
            EstructuraBDD.column_usuario + " TEXT," +
            EstructuraBDD.column_contraseña + " TEXT," +
            EstructuraBDD.column_nombre + " TEXT," +
            EstructuraBDD.column_fechaNac + " TEXT," +
            EstructuraBDD.column_avatar + " INT)";

    public static final String SQL_Borrar_Datos= "DROP TABLE IF EXISTS " + EstructuraBDD.tabla_usuarios;

    public static final String[] projection = {
            EstructuraBDD.column_id,
            EstructuraBDD.column_usuario,
            EstructuraBDD.column_contraseña,
            EstructuraBDD.column_nombre,
            EstructuraBDD.column_fechaNac,
            EstructuraBDD.column_avatar
    };

    public static final String selection = EstructuraBDD.column_usuario+ " =? AND " + EstructuraBDD.column_contraseña + "=?";
    public static final String[] selectionArgs = null;

    public static final String sortOrderDesc =
            EstructuraBDD.column_usuario + " DESC";

    public static final String sortOrderAsc =
            EstructuraBDD.column_usuario + " ASC";


}



