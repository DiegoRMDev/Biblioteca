package org.biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;



/**
 * Panel personalizado para dibujar una imagen como fondo, escalándola para cubrir el panel.
 */

public class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        // Carga la imagen desde la ruta del recurso (dentro del JAR o classpath)
        URL url = getClass().getResource(imagePath);
        if (url != null) {
            try {
                this.backgroundImage = ImageIO.read(url);
            } catch (IOException e) {
                System.err.println("Error al cargar la imagen de fondo desde: " + imagePath);
                e.printStackTrace();
            }
        } else {
            System.err.println("Recurso de imagen no encontrado. Verifica la ruta: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();

            // Dibuja la imagen escalada para que cubra todo el panel
            g2d.drawImage(backgroundImage, 0, 0, width, height, this);
            g2d.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Define el tamaño inicial de la ventana de login
        return new Dimension(500, 350);
    }
}
