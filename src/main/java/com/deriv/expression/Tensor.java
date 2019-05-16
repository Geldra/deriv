package com.deriv.expression;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Tensor implements Expression {
  private List<Expression> _lines;

  private Tensor(List<Expression> lines) {
    this._lines = lines;
  }

  private static boolean isValid(List<Expression> lines) {
//    return lines.stream().map(Expression::getDepth)
//             .reduce((a, b) -> a.equals(b) ? a : -1)
//             .map(x -> !x.equals(-1))
//             .orElse(false)
//             && lines.size() != 0;
    return true;
  }

  public static Expression of(Expression... lines) {
    return of(Arrays.asList(lines));
  }

  public static Expression of(List<Expression> lines) {
    if(!isValid(lines)) {
      throw new RuntimeException("Each Expression must have the same depth!");
    }

    return new Tensor(lines);
  }

  @Override
  public String toString() {
    return _lines.stream()
             .map(Expression::toString)
             .collect(toList()).toString();
  }

  @Override
  public Optional<Expression> evaluate(Variable var, Expression input) {
//    return new Tensor(_lines.stream().map(x -> x.evaluate(var, input)).collect(toList())); // TODO
    return Optional.empty();
  }

  @Override
  public Optional<Expression> differentiate(Variable var) {
    return Optional.empty(); // TODO
  }

  @Override
  public String toLaTex() {
    return ""; // TODO
  }
}