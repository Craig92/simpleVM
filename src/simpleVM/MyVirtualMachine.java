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

		try {

			int opCode = memory[programmCounter];
			int command = opCode & 0xF;
			int wert = (opCode >> 4) & 0xFFF;
			int rx = (opCode >> 4) & 0xF;
			int ry = (opCode >> 8) & 0xF;
			int fromMemory = (opCode >> 12) & 0x1;
			int toMemory = (opCode >> 13) & 0x1;

			switch (command) {

			case MyAssembler.NOP:
				programmCounter++;
				break;

			case MyAssembler.LOAD:
				register[0] = wert;
				programmCounter++;
				break;

			case MyAssembler.MOV:
				if (toMemory == 1 && fromMemory == 1) {
					memory[register[rx]] = memory[register[ry]];
				} else if (toMemory == 1 && fromMemory == 0) {
					memory[register[rx]] = register[ry];
				} else if (toMemory == 0 && fromMemory == 1) {
					register[rx] = memory[register[ry]];
				} else {
					register[rx] = register[ry];
				}
				programmCounter++;

				break;

			case MyAssembler.ADD:
				register[rx] = register[rx] + register[ry];
				programmCounter++;
				break;

			case MyAssembler.SUB:
				register[rx] = register[rx] - register[ry];
				programmCounter++;
				break;

			case MyAssembler.MUL:
				register[rx] = register[rx] * register[ry];
				programmCounter++;
				break;

			case MyAssembler.DIV:
				register[rx] = register[rx] / register[ry];
				programmCounter++;
				break;

			case MyAssembler.PUSH:
				registerStack.push(register[rx]);
				programmCounter++;
				break;

			case MyAssembler.POP:
				if (!registerStack.isEmpty()) {
					register[rx] = registerStack.pop();
					programmCounter++;
				} else {
					isFinish = true;
				}

				break;

			case MyAssembler.JMP:
				programmCounter = wert;
				break;

			case MyAssembler.JIZ:
				if (register[0] == 0) {
					programmCounter = wert;
				} else {
					programmCounter++;
				}

				break;

			case MyAssembler.JIH:
				if (register[0] > 0) {
					programmCounter = wert;
				} else {
					programmCounter++;
				}

				break;

			case MyAssembler.JSR:
				subRoutineStack.push(programmCounter + 1);
				programmCounter = wert;
				break;

			case MyAssembler.RTS:
				if (!subRoutineStack.isEmpty()) {
					programmCounter = subRoutineStack.pop();
				} else {
					isFinish = true;
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
	 * Vorsicht: Kann je nach Programmumfang sehr lange dauern!
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
					".../ProfilerFile.txt");
			for (int i = 0; i != profiler.length; i++) {
				if (profiler[i] != 0) {
					System.out.println("Die Zeile " + (i + 1) + " wurde " + profiler[i] + "mal mit einem Anteil von "
							+ new DecimalFormat("0.0000").format((profiler[i] / summe) * 100) + "% aufgerufen.");
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

	/**
	 * Legt die Größe des Arrays des Profilers fest.
	 * @param length Länge des Arrays
	 */
	public void setProfilerArray(int length) {
		this.profiler = new int[length];
	}

	/**
	 * Liefert den Speicher als Array zurück.
	 * @return Speicher-Array
	 */
	public int[] getMemory() {
		return this.memory;
	}

	/**
	 * Liefert die Größe des Registers zurück.
	 * @return Größe des Registers
	 */
	public int getRegisterSize() {
		return this.register.length;
	}

	/**
	 * Liefert die Größe des Speichers zurück.
	 * @return Größe des Speichers
	 */
	public int getMemorySize() {
		return this.memory.length;
	}

}
