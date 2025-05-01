package dds.monedero.exceptions;

public class MaximoExtraccionDiarioException extends RuntimeException {
  public MaximoExtraccionDiarioException(double topeMaximo, double limite) {
    super("No puede extraer mas de $ " + topeMaximo + " diarios, " + "límite: " + limite);
  }
}