package horeilly1101.Expression;

import java.util.stream.Collectors;

import static horeilly1101.Expression.Constant.*;
import static horeilly1101.Expression.Mult.*;

public class Power implements Expression {
  private Expression base;
  private Expression exponent;

  /**
   * Instantiates a Power object. Avoid using as much as possible! Use the easy constructor
   * instead. (A power is the more general form of a polynomial and an exponential.)
   *
   * Data definition: a power is two Expressions (a base and an exponent).
   */
  Power(Expression base, Expression exponent) {
    this.base = base;
    this.exponent = exponent;
  }

  /**
   * Use this to instantiate a power.
   */
  static Expression power(Expression base, Expression exponent) {
    // is exponent 1.0?
    if (exponent.equals(Constant.multID())) {
      return base;
    }

    // is it 0.0?
    if (exponent.equals(Constant.addID())) {
      return Constant.multID();
    }

    // is the whole expression a constant?
    if (base.isConstant() && exponent.isConstant()) {
      return constant((int)
                          Math.round(
                              Math.pow(
                                  base.asConstant().getVal(),
                                  exponent.asConstant().getVal())));
    }

    // is the base a mult?
    if (base.isMult()) {
      return mult(base.asMult().getFactors().stream()
                 .map(x -> power(x, exponent)).collect(Collectors.toList()));
    }

    // is the base a power?
    if (base.isPower()) {
      return power(base.getBase(), mult(exponent, base.getExponent()));
    }

    return new Power(base, exponent);
  }

  static Expression poly(Expression base, Integer exponent) {
    return power(base, constant(exponent));
  }

  static Expression exponential(Integer base, Expression exponent) {
    return new Power(constant(base), exponent);
  }

  @Override
  public Expression getExponent() {
    return exponent;
  }

  @Override
  public Expression getBase() {
    return base;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Power)) {
      return false;
    }

    Power pow = (Power) o;
    return pow.base.equals(this.base) && pow.exponent.equals(this.exponent);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode() + 12;
  }

  @Override
  public String toString() {
    return base.toString() + " ^ " + exponent.toString();
  }

  public Expression evaluate(String var, Double input) {
    return power(
        base.evaluate(var, input),
        exponent.evaluate(var, input));
  }

  public Expression differentiate(String var) {
    // I'm not using any of the cookie cutter power rules here.
    // This is a more general approach to differentiating powers.
    // Take the derivative of f(x) ^ g(x) for arbitrary f, g and
    // this is what you'll get.
    return Mult.mult(
        power(base, exponent),
        Add.add(
            Mult.mult(
                exponent.differentiate(var), Log.ln(base)),
            Mult.mult(exponent,
                base.differentiate(var),
                power(base, constant(-1)))));
  }
}