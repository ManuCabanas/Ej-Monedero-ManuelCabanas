package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Cuenta {

  @Getter @Setter
  private double saldo;
  // private double saldo = 0;  Code Smell: Cuando creo una cuenta, inicializo el saldo con un valor
  @Getter
  private List<Movimiento> movimientos;

  // public Cuenta() { saldo = 0;  } Code Smell, le paso el valor 0 al contructor con parametro y listo

  /* public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;                        Code Smell, le paso la lista en el constructor
  } */

  public Cuenta(double montoInicial, List<Movimiento> movimientos) {

    this.movimientos = movimientos;
    this.saldo = montoInicial;

  }

  public void agregarMonto(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }

    if(this.cantDepositosDiarios(LocalDate.now()) >= 3){
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    this.agregarMovimiento(LocalDate.now(), monto, true);
  }

  public void extraerMonto(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (this.saldo - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoEnFecha(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }

    this.agregarMovimiento(LocalDate.now(), monto, false);
  }

  public void agregarMovimiento(LocalDate fecha, double monto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, monto, esDeposito);
    movimientos.add(movimiento);
    this.actualizarSaldo(movimiento);
  }

  public void actualizarSaldo(Movimiento movimiento){
    if(movimiento.isDeposito()){
      this.saldo += movimiento.getMonto();
    }

    if(movimiento.isExtraccion()){
      this.saldo -= movimiento.getMonto();
    }
  }

  /* public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }  Code Smell: puedo usar metodo fueExtraido, que ya tiene la logica del filter */

  public double getMontoExtraidoEnFecha(LocalDate fecha){
    return movimientos.stream().filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto).sum();
  }

  public int cantDepositosDiarios(LocalDate fecha){
    return this.movimientos.stream().filter(movimiento -> movimiento.fueDepositado((fecha))).toList().size();
  }

  /* Public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {                HAGO ANNOTATION DE GETTER Y SETTER
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  } */

}
