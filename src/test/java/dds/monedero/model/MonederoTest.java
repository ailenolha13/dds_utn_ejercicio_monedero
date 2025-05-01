package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  @DisplayName("Es posible poner $1500 en una cuenta vacía")
  void Poner() {
    assertEquals(0, cuenta.getSaldo());
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  @DisplayName("El saldo se actualiza correctamente después de hacer depósitos y extracciones")
  void SaldoDespuesDePoner() {
    cuenta.poner(1000);
    assertEquals(1000, cuenta.getSaldo());
    cuenta.poner(500);
    assertEquals(1500, cuenta.getSaldo());
    cuenta.sacar(500);
    assertEquals(1000, cuenta.getSaldo());
    cuenta.sacar(500);
    assertEquals(500, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible poner montos negativos")
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  @DisplayName("Es posible realizar múltiples depósitos consecutivos")
  void TresDepositos() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
    cuenta.poner(456);
    assertEquals(1956, cuenta.getSaldo());
    cuenta.poner(1900);
    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible superar la máxima cantidad de depositos diarios")
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
      cuenta.poner(245);
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el saldo disponible")
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.poner(90);
      cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el límite diario")
  void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.poner(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("Es posible realizar múltiples extracciones sin superar el límite diario")
  void MultipleExtraccionesDentroDelLimite() {
    cuenta.poner(2000);
    cuenta.sacar(300);
    assertEquals(1700, cuenta.getSaldo());
    cuenta.sacar(200);
    assertEquals(1500, cuenta.getSaldo());
    cuenta.sacar(450);
    assertEquals(1050, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible extraer un monto negativo")
  void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  @DisplayName("Se guarda correctamente un movimiento al hacer un depósito")
  void RegistroDeDeposito() {
    cuenta.poner(1000);
    assertEquals(1, cuenta.getMovimientos().size());
    assertTrue(cuenta.getMovimientos().get(0).fueDepositado(LocalDate.now()));
    assertEquals(100, cuenta.getMovimientos().get(0).getMonto());
  }

  @Test
  @DisplayName("Se guarda correctamente un movimiento al hacer una extracción")
  void RegistroDeExtraccion() {
    cuenta.poner(2000);
    cuenta.sacar(500);
    assertTrue(cuenta.getMovimientos().get(1).fueExtraido(LocalDate.now()));
    assertEquals(500, cuenta.getMovimientos().get(1).getMonto());
  }

}