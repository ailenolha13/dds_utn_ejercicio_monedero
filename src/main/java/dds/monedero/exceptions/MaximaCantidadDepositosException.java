package dds.monedero.exceptions;

public class MaximaCantidadDepositosException extends RuntimeException {

  public MaximaCantidadDepositosException(double limite) {
    super("Ya excedio los " + limite + " depositos diarios");
  }

}