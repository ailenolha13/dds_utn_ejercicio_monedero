package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {
  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    this.saldo = 0;
  }

  public Cuenta(double montoInicial) {
    this.saldo = montoInicial;
  }
  
  public void poner(double monto) {
    // Code smell 3 - se podria usar un try-catch y mejorar el manejo de errores y ademas ser mas
    // expresivos y declarativos con la responsabilidad del metodo
    // Code smell 4 - los mensajes hardcodeados de las excepcion deberian estar en la implementacion de la excepcion
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    // Code smell 5 - la implementacion de la validacion de la cantidad de movimientos podria estar en otro lado
    if (getMovimientos().stream()
        .filter(movimiento -> movimiento.fueDepositado(LocalDate.now()))
        .count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    // Code smell 6 - no se esta utilizando el metodo para agregar el movimiento
    new Movimiento(LocalDate.now(), monto, true).agregateA(this);
  }

  public void sacar(double monto) {
    // Code smell 7 - sucede lo mismo que code smell 3, 4
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    // Code smell 8 - sucede lo mismo que code smell 5
    var montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    var limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, " + "límite: " + limite);
    }
    // Code smell 9 - sucede lo mismo que code smell 6
    new Movimiento(LocalDate.now(), monto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    var movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  // Code smell 10 - el nombre del metodo podria ser mas expresivo
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    // Code smell 11 - se deberia devolver una copia y no la referencia de la lista de movimientos,
    // ya que sino se podria modificar desde afuera de la clase lo cual romperia el enccapsulamiento
    return movimientos;
  }

  // Code smell 12 - no se deberia poder setear de afuera de la clase los movimientos
  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  // Code smell 13 - no se deberia poder setear de afuera de la clase el saldo
  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
