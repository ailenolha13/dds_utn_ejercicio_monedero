package dds.monedero.exceptions;

public class MontoNegativoException extends RuntimeException {
  public MontoNegativoException(double monto) {
    super(monto + ": el monto a ingresar debe ser un valor positivo");
  }
}