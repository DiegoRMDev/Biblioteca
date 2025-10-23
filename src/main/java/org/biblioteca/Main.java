package org.biblioteca;

import org.biblioteca.util.DBConnection; //import para la prueba
import org.biblioteca.util.DataInitializer;
import javax.swing.SwingUtilities; //
import org.biblioteca.presentacion.LoginUI;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        DBConnection.getConnection();
        DataInitializer.inicializarDatos();

        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}


