package com.fwcd.quantum.gates.binary;

import com.fwcd.fructose.math.Complex;
import com.fwcd.fructose.math.ComplexMatrix;
import com.fwcd.quantum.gates.MatrixGate;
import com.fwcd.quantum.gates.QuantumGateVisitor;

/**
 * Half swaps the input qubits.
 */
public class SqrtSwapGate extends MatrixGate {
	@Override
	protected ComplexMatrix getMatrix() {
		return new ComplexMatrix(new Complex[][] {
				{Complex.ONE, Complex.ZERO, Complex.ZERO, Complex.ZERO},
				{Complex.ZERO, new Complex(0.5F, 0.5F), new Complex(0.5F, -0.5F), Complex.ZERO},
				{Complex.ZERO, new Complex(0.5F, -0.5F), new Complex(0.5F, 0.5F), Complex.ZERO},
				{Complex.ZERO, Complex.ZERO, Complex.ZERO, Complex.ONE}
		});
	}
	
	@Override
	public String getSymbol() {
		return "sqrt(Swap)";
	}
	
	@Override
	public void accept(QuantumGateVisitor visitor) {
		visitor.visitSqrtSwap(this);
	}
}
