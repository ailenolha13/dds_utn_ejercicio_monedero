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
    var esDeposito = true;
    validarMontoPositivo(monto);
    validarLimiteDepositosDiarios();
    agregarMovimiento(LocalDate.now(), monto, esDeposito);
  }

  private void validarMontoPositivo(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto);
    }
  }

  private void validarLimiteDepositosDiarios(){
    var limite = 3;
    if (getMovimientos().stream()
        .filter(movimiento -> movimiento.fueDepositado(LocalDate.now()))
        .count() >= limite) {
      throw new MaximaCantidadDepositosException(limite);
    }
  }

  public void sacar(double monto) {
    var esDeposito = false;
    validarMontoPositivo(monto);
    validarSaldoDisponible(monto);
    validarLimiteExtraccionDiaria(monto);
    agregarMovimiento(LocalDate.now(), monto, esDeposito);
  }

  private void validarSaldoDisponible(double montoAExtraer){
    var saldoDisponible = getSaldo();
    if ((saldoDisponible - montoAExtraer) < 0) {
      throw new SaldoMenorException(saldoDisponible);
    }
  }

  private void validarLimiteExtraccionDiaria(double montoAExtraer){
    var montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    var topeMaximo = 1000;
    var limite = topeMaximo - montoExtraidoHoy;
    if (montoAExtraer > limite) {
      throw new MaximoExtraccionDiarioException(topeMaximo, limite);
    }
  }

  public void agregarMovimiento(LocalDate fecha, double monto, boolean esDeposito) {
    var movimiento = new Movimiento(fecha, monto, esDeposito);
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
