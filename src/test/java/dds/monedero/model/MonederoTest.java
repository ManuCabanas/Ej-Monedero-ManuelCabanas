package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    //List<Movimiento> movimientos = new ArrayList<>();
    cuenta = new Cuenta(0, new ArrayList<>());
  }

  @Test
  void PonerMonto() {
    cuenta.agregarMonto(1500);
    assertEquals(cuenta.getSaldo(), 1500, 0 );
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.agregarMonto(-1500));
  }

  @Test
  void TresDepositos() {
    cuenta.agregarMonto(1500);
    cuenta.agregarMonto(456);
    cuenta.agregarMonto(1900);
    assertEquals(cuenta.getSaldo(), 3856, 0);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.agregarMonto(1500);
          cuenta.agregarMonto(456);
          cuenta.agregarMonto(1900);
          cuenta.agregarMonto(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.extraerMonto(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.extraerMonto(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraerMonto(-500));
  }

}