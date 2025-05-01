package dds.monedero.exceptions;

public class MaximoExtraccionDiarioException extends RuntimeException {
  public MaximoExtraccionDiarioException(double baseLimite, double limite) {
    super("No puede extraer mas de $ " + baseLimite + " diarios, " + "límite: " + limite);
  }
}