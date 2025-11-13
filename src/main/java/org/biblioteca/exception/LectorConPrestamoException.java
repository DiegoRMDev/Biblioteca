package org.biblioteca.exception;

public class LectorConPrestamoException extends RuntimeException {
  // Constructor que acepta mensaje y causa (ya existía)
  public LectorConPrestamoException(String message, Throwable cause) {
    super(message, cause);
  }

  // 💡 CONSTRUCTOR AÑADIDO: Acepta solo el mensaje
  public LectorConPrestamoException(String message) {
    super(message);
  }

}
