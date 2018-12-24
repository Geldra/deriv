package horeilly1101.Expression;

import java.util.*;

import static horeilly1101.Expression.Add.add;
import static java.util.stream.Collectors.toList;
import static horeilly1101.Expression.Constant.*;

public class Mult implements Expression {
  private List<Expression> factors;

  /**
   * Instantiates a Mult. Avoid using as much as possible! Use the easy constructor
   * instead.
   *
   * Data definition: a term is a list of Expressions (factors). This is analogous
   * to the factors in an expression.
   */
  private Mult(List<Expression> factors) {
    this.factors = factors;
  }

  /**
   * Use this to create a mult object.
   */
  private static Expression mult(List<Expression> factors) {
    if (factors.isEmpty()) {
      // bad
      throw new RuntimeException("Don't instantiate a term with an empty list!");
    }

    List<Expression> simplified = simplify(factors);
    return simplified.size() == 1 ? simplified.get(0) : new Mult(simplified);
  }

  /**
   * Or use this if you really want to.
   */
  static Expression mult(Expression... factors) {
    return mult(Arrays.asList(factors));
  }

  private List<Expression> getFactors() {
    return factors;
  }

  @Override
  public Constant getConstantFactor() {
    List<Expression> constant = factors.stream().filter(Expression::isConstant).collect(toList());
    return constant.isEmpty() ? multID() : constant.get(0).asConstant();
  }

  @Override
  public Expression getSymbolicFactors() {
    return mult(factors.stream().filter(x -> !x.isConstant()).collect(toList()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Mult)) {
      return false;
    }

    Mult mul = (Mult) o;
    return mul.factors.equals(this.factors);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode() + 8;
  }

  @Override
  public String toString() {
    return "(" + factors.get(0).toString()
               + factors.subList(1, factors.size()).stream() //  sublist is O(1)
                     .map(Expression::toString)
                     .reduce("", (a, b) -> a + " * " + b) + ")";
  }

  public Expression evaluate(String var, Double input) {
    // multiplies factors together
    return mult(factors.stream()
                  .map(x -> x.evaluate(var, input))
                  .collect(toList()));
  }

  public Expression differentiate(String var) {
    return add(
              mult(
                  factors.get(0),
                  mult(factors.subList(1, factors.size())).differentiate(var)
              ),
              mult(
                  factors.get(0).differentiate(var),
                  mult(factors.subList(1, factors.size()))
              ));
  }

  /*
  Private, static methods to help simplify Mult objects when instantiated
   */

  /**
   * This function brings together all the simplify functions. It runs
   * recursively until there is nothing left to simplify.
   *
   * (Everything but the (possible) recursive call runs in expected linear
   * time.)
   */
  private static List<Expression> simplify(List<Expression> factors) {
    return isSimplified(factors)
               ? factors
               : simplify(
                   simplifyFactors(
                       simplifyConstantFactors(
                           withoutNesting(factors))));
  }

  /**
   * This method simplifies a list of factors by ensuring factors
   * are taken to the proper exponents. (e.g. we want to write x * x
   * as x ^ 2.0.)
   *
   * @return List<Expression> simplified
   */
  private static List<Expression> simplifyFactors(List<Expression> factors) {
    HashMap<Expression, List<Expression>> powerMap = new HashMap<>();

    for (Expression fac : factors) {
      if (powerMap.containsKey(fac.getBase())) {
        List<Expression> newList = powerMap.get(fac.getBase());
        newList.add(fac.getExponent());
        powerMap.replace(fac.getBase(), newList);
      } else {
        List<Expression> newList = new ArrayList<>();
        newList.add(fac.getExponent());
        powerMap.put(fac.getBase(), newList);
      }
    }

    // add up the exponents
    return powerMap.keySet().stream()
               .map(key -> Power.power(
                   key,
                   add(powerMap.get(key))))
               .collect(toList());
  }

  /**
   * This method simplifies a list of factors to get rid of extraneous
   * constant factors. (e.g. multipling an expression by 1 should yield
   * the expression, multiplying an expression by 0 should yield zero,
   * and so on.)
   *
   * It also multiplies the values of all constants together, so that each
   * mult has a single Constant.
   *
   * @return List<Expression> simplified
   */
  private static List<Expression> simplifyConstantFactors(List<Expression> factors) {
    // keep track of constants' values
    List<Expression> noConstants = new ArrayList<>();
    Double constants = 1.0;

    for (Expression factor : factors) {
      if (factor.isConstant()) {
        // checked cast
        constants *= factor.asConstant().getVal();
      } else {
        noConstants.add(factor);
      }
    }

    // multiplicative identity?
    if (constants == 1.0 && noConstants.isEmpty()) {
      noConstants.add(Constant.constant(1.0));
      // zero?
    } else if (constants == 0.0) {
      // all factors go to zero
      noConstants.clear();
      noConstants.add(Constant.constant(0.0));
    } else if (constants != 1.0) {
      noConstants.add(Constant.constant(constants));
    }

    return noConstants;
  }

  /**
   * This method simplifies a list of factors by taking advantage of
   * the associativity of multiplication. (i.e. a Mult object multiplied
   * by a Mult object should not yield a Mult object of two Mult objects.
   * It should yield a Mult object of whatever was in the original objects,
   * flatmapped together.)
   *
   * @return List<Expression> simplified
   */
  private static List<Expression> withoutNesting(List<Expression> factors) {
    List<Expression> newList = new ArrayList<>();

    for (Expression factor : factors) {
      if (factor.isMult()) {
        // checked cast
        newList.addAll(factor.asMult().getFactors());
      } else {
        newList.add(factor);
      }
    }

    return newList;
  }

  /**
   * This function figures out whether or not the list of factors is
   * fully simplified.
   */
  private static Boolean isSimplified(List<Expression> factors) {
    // we want to make sure there is at most 1 constant in factors
    int conCount = 0;
    Set<Expression> bases = new HashSet<>();

    for (Expression fac : factors) {
      if (fac.isConstant()) {
        conCount += 1;
      }

      // all of these conditions imply factors is not simplified
      if (conCount > 1
              || (fac.equals(multID()) && factors.size() > 1)
              || (fac.equals(addID()) && factors.size() > 1)
              || fac.isMult()
              || bases.contains(fac.getBase())) {
        return false;
      }

      bases.add(fac.getBase());
    }

    return true;
  }
}