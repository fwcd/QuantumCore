package com.fwcd.quantum.gates;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.fwcd.fructose.math.Complex;
import com.fwcd.fructose.math.ExtMath;
import com.fwcd.fructose.math.Matrix;
import com.fwcd.fructose.math.Vector;
import com.fwcd.quantum.utils.ArrayUtils;
import com.fwcd.quantum.utils.BitMath;
import com.fwcd.quantum.wrapper.Ket;

/**
 * A convenience class to represent matrix based
 * quantum gates.
 */
public abstract class MatrixGate implements QuantumGate {
	private final StateMappingTable mappings = getMappings();
	private final int qubitCount = mappings.qubitCount();
	
	@Override
	public Vector<Complex> apply(Vector<Complex> quantumState, int... qubitIndices) {
		int totalQubits = ExtMath.log2Floor(quantumState.size());
		Matrix<Complex> gateMatrix = createGateMatrix(totalQubits, qubitIndices);
		return gateMatrix.multiply(quantumState);
	}
	
	public Matrix<Complex> createGateMatrix(int totalQubits, int[] qubitIndices) {
		assertCorrectGateSize(qubitIndices.length);
		assertValidQubitIndices(totalQubits, qubitIndices);
		// The output matrix is square-shaped
		int matrixSideLength = (int) Math.pow(2, totalQubits);
		List<List<Complex>> transposedMatrix = new ArrayList<>();
		
		for (int col=0; col<matrixSideLength; col++) {
			// The col index can be interpreted as an input state in binary
			transposedMatrix.add(createMatrixCol(col, totalQubits, qubitIndices));
		}
		
		return new Matrix<>(transposedMatrix).transpose();
	}
	
	private List<Complex> createMatrixCol(int binaryInputState, int totalQubits, int[] qubitIndices) {
		int gateInputState = BitMath.extractBits(binaryInputState, totalQubits, qubitIndices);
		List<Vector<Complex>> factors = mappings.mappedFactorsOf(gateInputState);
		Vector<Complex> accumulated = null;
		
		for (int i=0; i<totalQubits; i++) {
			// TODO: indexOf(...) takes O(n) time each iteration, improve performance
			OptionalInt factorIndex = ArrayUtils.indexOf(i, qubitIndices);
			Vector<Complex> current;
			
			if (factorIndex.isPresent()) {
				// Qubits that are affected by the gate are mapped
				current = factors.get(factorIndex.orElse(-1));
			} else {
				// Unaffected qubits are not mapped
				current = Ket.ofBit(BitMath.getBit(binaryInputState, totalQubits, i));
			}
			
			if (accumulated == null) {
				accumulated = current;
			} else {
				accumulated = accumulated.kronecker(current);
			}
		}
		
		return accumulated.stream().collect(Collectors.toList());
	}
	
	protected abstract StateMappingTable getMappings();
	
	private void assertValidQubitIndices(int totalQubits, int[] qubitIndices) {
		for (int index : qubitIndices) {
			if (index < 0) {
				throw new IllegalArgumentException("Qubit index (" + index + ") is smaller than zero");
			} else if (index >= totalQubits) {
				throw new IllegalArgumentException(
					"Qubit index ("
					+ index
					+ ") is outside of the total qubits ("
					+ totalQubits
					+ ")"
				);
			}
		}
	}
	
	private void assertCorrectGateSize(int inputQubitCount) {
		if (qubitCount != inputQubitCount) {
			throw new IllegalArgumentException(
				"Tried to apply a "
				+ getClass().getSimpleName()
				+ " gate that operates on "
				+ qubitCount
				+ " qubits to "
				+ inputQubitCount
				+ " qubits"
			);
		}
	}
	
	public int qubitCount() {
		return qubitCount;
	}
}
