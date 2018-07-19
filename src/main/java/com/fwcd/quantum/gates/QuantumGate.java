package com.fwcd.quantum.gates;

import com.fwcd.fructose.math.ComplexVector;

/**
 * A quantum gates takes a superposition vector as it's
 * input and produces a new superposition vector as it's
 * output.
 */
@FunctionalInterface
public interface QuantumGate {
	/**
	 * Applies this quantum gate to a given vector.
	 * 
	 * @param possibleStates - The input superposition (or list of probabilities for possible qubit states)
	 * @param qubitIndex - The start qubit index of the qubits on which the gate will be applied
	 * @return An output superposition
	 */
	ComplexVector apply(ComplexVector possibleStates, int qubitIndex);
	
	default String getSymbol() {
		return "?";
	}
}
