package horeilly1101;

import org.junit.Test;

import static horeilly1101.Expression.Add.*;
import static horeilly1101.Expression.Constant.*;
import static horeilly1101.Expression.Mult.*;
import static horeilly1101.Expression.Power.*;
import static horeilly1101.Expression.Variable.*;
import static horeilly1101.Expression.Trig.*;
import static org.junit.Assert.assertEquals;

public class ExpressionTest {

  @Test
  public void polyDerivativeTest() {
    // x ^ 2.0
    Expression pol = poly(var(), 2.0);
    // 2.0 * x
    Expression polExpected = mult(constant(2.0), var());
    assertEquals(polExpected, pol.differentiate());

    // x ^ 2.0 + x + 1.0
    Expression pol2 = add(poly(var(), 2.0), var(), constant(1.0));
    // 2.0 * x + 1
    Expression pol2Expected = add(mult(constant(2.0), var()), constant(1.0));
    assertEquals(pol2Expected, pol2.differentiate());
  }

  @Test
  public void multEqualityTest() {
    // We want to be sure that the ordering of the factors does not affect equality

    // x * 2.0
    Expression mul = mult(var(), constant(2.0));
    // 2.0 * x
    Expression mul2 = mult(constant(2.0), var());
    assertEquals(mul, mul2);
  }
}