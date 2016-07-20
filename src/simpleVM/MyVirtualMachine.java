package simpleVM;

import java.io.*;
import java.text.*;
import java.util.*;

import simpleVM.Exceptions.VirtualMachineException;

public class MyVirtualMachine {

	private int[] profiler;

	private boolean isFinish;
	private int[] register;
	private int[] memory;
	private int programmCounter;
	private Stack<Integer> subRoutineStack;
	private Stack<Integer> registerStack;

	private static final int NOP = 0b0000;
	private static final int LOAD = 0b0001;
	private static final int MOV = 0b0010;
	private static final int ADD = 0b0011;
	private static final int SUB = 0b0100;
	private static final int MUL = 0b0101;
	private static final int DIV = 0b0110;
	private static final int PUSH = 0b0111;
	private static final int POP = 0b1000;
	private static final int JMP = 0b1001;
	private static final int JIZ = 0b1010;
	private static final int JIH = 0b1011;
	private static final int JSR = 0b1100;
	private static final int RTS = 0b1101;

	/**
	 * Inizialisiert die Virtuelle Maschine.
	 * 
	 * @param registerSize
	 *            Größe des Registers.
	 * @param memorySize
	 *            Größe des Speichers.
	 */
	public MyVirtualMachine(int registerSize, int memorySize) {

		this.programmCounter = 0;
		this.isFinish = false;

		this.subRoutineStack = new Stack<Integer>();
		this.registerStack = new Stack<Integer>();

		if (registerSize > 0) {
			this.register = new int[registerSize];
		} else {
			this.register = new int[16];
		}

		if (memorySize > 0) {
			this.memory = new int[memorySize];
		} else {
			this.memory = new int[4096];
		}
	}

	/**
	 * Die Virtuellemaschine wird solange ausgeführt bis sie zum Ende gelangt.
	 * 
	 * @throws VirtualMachineException
	 */
	public void startVirtualMaschine() throws VirtualMachineException {

		do {
			profiler[programmCounter]++;
			runVirtualMaschine();
			// getRegisterData();

		} while (!isFinish);

		getMemoryData();
	}

	/**
	 * Die VirtuelleMaschine führt den Maschinencode entsprechend der Befehle
	 * aus.
	 * 
	 * @throws VirtualMachineException
	 */
	private void runVirtualMaschine() throws VirtualMachineException {

//		System.out.println("Virtuelle Maschine führt den Code aus: ");

		try {

			int opCode = memory[programmCounter];
			int command = opCode & 0xF;
			int wert;
			int rx;
			int ry;
			int toMemory;
			int fromMemory;

			switch (command) {

			case NOP:
				programmCounter++;
				// System.out.println("NOP Befehl ausgeführt");

				break;

			case LOAD:
				wert = (opCode >> 4) & 0xFFF;
				register[0] = wert;
				programmCounter++;
				// System.out.println("LOAD #" + wert + " Befehl ausgeführt");

				break;

			case MOV:
				rx = (opCode >> 4) & 0xF;
				ry = (opCode >> 8) & 0xF;
				fromMemory = (opCode >> 12) & 0x1;
				toMemory = (opCode >> 13) & 0x1;

				if (toMemory == 1 && fromMemory == 1) {
					// System.out.println("MOV (R" + rx + ") , (R" + ry + ")
					// Befehl ausgeführt");
					memory[register[rx]] = memory[register[ry]];
				} else if (toMemory == 1 && fromMemory == 0) {
					// System.out.println("MOV (R" + rx + ") , R" + ry + "
					// Befehl ausgeführt");
					memory[register[rx]] = register[ry];
				} else if (toMemory == 0 && fromMemory == 1) {
					// System.out.println("MOV R" + rx + " , (R" + ry + ")
					// Befehl ausgeführt");
					register[rx] = memory[register[ry]];
				} else {
					// System.out.println("MOV R" + rx + " , R" + ry + " Befehl
					// ausgeführt");
					register[rx] = register[ry];
				}
				programmCounter++;

				break;

			case ADD:
				rx = (opCode >> 4) & 0xF;
				ry = (opCode >> 8) & 0xF;

				register[rx] = register[rx] + register[ry];
				programmCounter++;
				// System.out.println("ADD R" + rx + " , R" + ry + " Befehl
				// ausgeführt");

				break;

			case SUB:
				rx = (opCode >> 4) & 0xF;
				ry = (opCode >> 8) & 0xF;

				register[rx] = register[rx] - register[ry];
				programmCounter++;
				// System.out.println("SUB R" + rx + " , R" + ry + " Befehl
				// ausgeführt");

				break;

			case MUL:
				rx = (opCode >> 4) & 0xF;
				ry = (opCode >> 8) & 0xF;

				register[rx] = register[rx] * register[ry];
				programmCounter++;
				// System.out.println("MUL R" + rx + " , R" + ry + " Befehl
				// ausgeführt");

				break;

			case DIV:
				rx = (opCode >> 4) & 0xF;
				ry = (opCode >> 8) & 0xF;

				register[rx] = register[rx] / register[ry];
				programmCounter++;
				// System.out.println("DIV R" + rx + " , R" + ry + " Befehl
				// ausgeführt");

				break;

			case PUSH:
				rx = (opCode >> 4) & 0xF;

				registerStack.push(register[rx]);
				programmCounter++;
				// System.out.println("PUSH R" + rx + " Befehl ausgeführt");

				break;

			case POP:
				rx = (opCode >> 4) & 0xF;

				if (!registerStack.isEmpty()) {
					register[rx] = registerStack.pop();
					programmCounter++;
					// System.out.println("POP R" + rx + " Befehl ausgeführt");
				} else {
					isFinish = true;
					// System.out.println("POP R" + rx + " Befehl ausgeführt
					// ENDE");
				}

				break;

			case JMP:
				wert = (opCode >> 4) & 0xFFF;
				programmCounter = wert;
				// System.out.println("JMP #" + wert + " Befehl ausgeführt");

				break;

			case JIZ:
				wert = (opCode >> 4) & 0xFFF;

				if (register[0] == 0) {
					programmCounter = wert;
					// System.out.println("JIZ #" + wert + " Befehl
					// ausgeführt");
				} else {
					programmCounter++;
					// System.out.println("JIZ #" + wert + " Befehl nicht
					// ausgeführt");
				}

				break;

			case JIH:
				wert = (opCode >> 4) & 0xFFF;

				if (register[0] > 0) {
					programmCounter = wert;
					// System.out.println("JIH #" + wert + " Befehl
					// ausgeführt");
				} else {
					programmCounter++;
					// System.out.println("JIH #" + wert + " Befehl nicht
					// ausgeführt");
				}

				break;

			case JSR:
				wert = (opCode >> 4) & 0xFFF;

				subRoutineStack.push(programmCounter + 1);
				programmCounter = wert;
				// System.out.println("JSR #" + wert + " Befehl ausgeführt");

				break;

			case RTS:

				if (!subRoutineStack.isEmpty()) {
					programmCounter = subRoutineStack.pop();
//					System.out.println("RTS Befehl ausgeführt");
				} else {
					isFinish = true;
//					System.out.println("RTS Befehl ausgeführt ENDE");
				}

				break;

			default:
				isFinish = true;
				break;

			}

		} catch (Exception ex) {
			throw new VirtualMachineException("Ungültiger Zugriff aus Systemressourcen");
		}
	}

	/**
	 * Gibt die Memory Inhalte auf der Konsole aus, die nicht null sind.
	 */
	private void getMemoryData() {
		
		for (int i = 0; i != memory.length; i++) {
			if ( memory[i] != 0 && i >= 1000) {
				System.out.println("Fibonacci[" + (i - 999) + "]: " + memory[i]);
			}
		}
	}

	/**
	 * Gibt die Register Inhalte auf der Konsole aus.
	 */
	private void getRegisterData() {
		for (int i = 0; i != register.length / 2; i++) {
			System.out.println("Register[" + i + "]: " + register[i] + " | Register[" + (i + register.length / 2)
					+ "]: " + register[(i + register.length / 2)]);
		}
	}

	/**
	 * Gibt die Profilerinformationen in die Konsole und in eine Datei aus.
	 * 
	 * @throws VirtualMachineException
	 */
	public void getProfilerData() throws VirtualMachineException {

		double summe = 0;

		for (int i = 0; i != profiler.length; i++) {
			summe += profiler[i];
		}

		FileWriter output = null;

		try {

			output = new FileWriter(
					"C:/Users/Thorsten/Documents/Eclipse/Eclipse Umgebung/HWP_SS16/src/simpleVM/ProfilerFile.txt");

			for (int i = 0; i != profiler.length; i++) {
				if (profiler[i] != 0) {
//					System.out.println("Die Zeile " + (i + 1) + " wurde " + profiler[i] + "mal mit einem Anteil von "
//							+ new DecimalFormat("0.0000").format((profiler[i] / summe) * 100) + "% aufgerufen.");
					output.write("Die Zeile " + (i + 1) + " wurde " + profiler[i] + "mal  mit einem Anteil von "
							+ new DecimalFormat("0.0000").format((profiler[i] / summe) * 100) + "% aufgerufen. \n");
				}
			}
			output.flush();
			output.close();

		} catch (IOException ex) {
			throw new VirtualMachineException("PROFILER ERROR Fehler beim Beschreiben der Datei!");
		}

	}

	public void setProfiler(int length) {
		this.profiler = new int[length];
	}

	public int[] getMemory() {
		return this.memory;
	}

	public int getRegisterSize() {
		return this.register.length;
	}

	public int getMemorySize() {
		return this.memory.length;
	}

}
