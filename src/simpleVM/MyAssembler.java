package simpleVM;

import java.io.*;
import java.util.*;

import simpleVM.Exceptions.AssemblerException;

public class MyAssembler {

	private MyVirtualMachine vm;
	private int programmCounter = 0;

	public static final int NOP = 0b0000;
	public static final int LOAD = 0b0001;
	public static final int MOV = 0b0010;
	public static final int ADD = 0b0011;
	public static final int SUB = 0b0100;
	public static final int MUL = 0b0101;
	public static final int DIV = 0b0110;
	public static final int PUSH = 0b0111;
	public static final int POP = 0b1000;
	public static final int JMP = 0b1001;
	public static final int JIZ = 0b1010;
	public static final int JIH = 0b1011;
	public static final int JSR = 0b1100;
	public static final int RTS = 0b1101;

	/**
	 * Erzeugt einen Assembler mit einer virtuellen Maschine.
	 * 
	 * @param vm
	 *            Virtuelle Maschine
	 */
	public MyAssembler(MyVirtualMachine vm) {

		this.vm = vm;
	}

	/**
	 * Startet den Assembler, der eine Textdatei zeilenweise ausliest und die
	 * runAssembler() Methode für jede Zeile aufruft bis das Ende der Datei
	 * erreicht wurde,
	 * 
	 * @param inputData
	 *            Dateiname des Assemblerprogramms.
	 * @throws AssemblerException
	 */
	public void startAssembler(String inputData) throws AssemblerException {

		File inputFile = new File(inputData);

		// Prüft ob Datei existiert und lesbar ist.
		if (!inputFile.exists() || !inputFile.canRead() || !inputFile.isFile()) {
			throw new AssemblerException("Fehler bei der Öffnen der Datei!");

		} else {

			Scanner fileScanner = null;

			try {
				fileScanner = new Scanner(inputFile);

				// Prüft ob es eine nächste Zeile gibt und ruft die
				// runAssembler()-Methode auf, wenn es noch eine Zeile gibt.
				while (fileScanner.hasNextLine()) {
					runAssembler(fileScanner.nextLine());
				}

			} catch (IOException ex) {
				throw new AssemblerException("Fehler beim Verarbeiten der Datei!");

			} finally {

				if (fileScanner != null) {
					fileScanner.close();
				}
			}
		}

		// Setzt die Größe des Profiler Arrays
		vm.setProfilerArray(programmCounter);
	}

	/**
	 * Wandelt zeilenweise den Programmcode in OpCode um.
	 * 
	 * @param programmCode
	 *            Aktuelle Textzeile aus der Datei.
	 * @param vm
	 *            Virtuelle Maschine.
	 * @throws AssemblerException
	 */
	private void runAssembler(String programmCode) throws AssemblerException {

		String command = "";
		String temp = "";
		int opCode = 0;
		int toMemory;
		int fromMemory;
		Scanner stringScanner = null;

		try {

			stringScanner = new Scanner(programmCode);

			// Liest dem Assemblerbefehl aus der Textzeile
			if (stringScanner.hasNext()) {
				command = stringScanner.next();
			}

			switch (command) {

			case "NOP":
				opCode = NOP;
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "LOAD":
				opCode = LOAD;
				temp = stringScanner.next();
				opCode += addWert(removeChar(temp, '#'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "MOV":
				opCode = MOV;
				temp = stringScanner.next();

				if (temp.charAt(0) == '(') {
					temp = removeClamps(temp);
					toMemory = 1;
				} else {
					toMemory = 0;
				}

				opCode += addRX(removeChar(temp, 'R'));

				stringScanner.next();

				temp = stringScanner.next();

				if (temp.charAt(0) == '(') {
					temp = removeClamps(temp);
					fromMemory = 1;
				} else {
					fromMemory = 0;
				}

				opCode += addRY(removeChar(temp, 'R'));
				opCode += (fromMemory << 12);
				opCode += (toMemory << 13);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "ADD":
				opCode = ADD;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				stringScanner.next();
				temp = stringScanner.next();
				opCode += addRY(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;
			case "SUB":
				opCode = SUB;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				stringScanner.next();
				temp = stringScanner.next();
				opCode += addRY(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "MUL":
				opCode = MUL;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				stringScanner.next();
				temp = stringScanner.next();
				opCode += addRY(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "DIV":
				opCode = DIV;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				stringScanner.next();
				temp = stringScanner.next();
				opCode += addRY(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "PUSH":
				opCode = PUSH;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "POP":
				opCode = POP;
				temp = stringScanner.next();
				opCode += addRX(removeChar(temp, 'R'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JMP":
				opCode = JMP;
				temp = stringScanner.next();
				opCode += addWert(removeChar(temp, '#'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JIZ":
				opCode = JIZ;
				temp = stringScanner.next();
				opCode += addWert(removeChar(temp, '#'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JIH":
				opCode = JIH;
				temp = stringScanner.next();
				opCode += addWert(removeChar(temp, '#'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JSR":
				opCode = JSR;
				temp = stringScanner.next();
				opCode += addWert(removeChar(temp, '#'));
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "RTS":
				opCode = RTS;
				vm.getMemory()[programmCounter++] = opCode;
				break;

			default:

				break;
			}

		} catch (IndexOutOfBoundsException | NumberFormatException | NoSuchElementException ex) {
			throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmCounter + 1) + " !");

		} finally {

			if (stringScanner != null) {
				stringScanner.close();
			}

		}
	}

	/**
	 * Liest den Wert aus dem String und passt in entsprechend für den OpCode
	 * an.
	 * 
	 * @param temp
	 *            String mit Wert
	 * @return Wert
	 */
	private int addWert(String temp) {
		return (Integer.parseInt(temp) << 4);
	}

	/**
	 * Liest den RX-Wert aus dem String und passt in entsprechend für den OpCode
	 * an.
	 * 
	 * @param temp
	 *            String mit RX-Wert
	 * @return RX
	 */
	private int addRX(String temp) {
		return (Integer.parseInt(temp) << 4);
	}

	/**
	 * Liest den RY-Wert aus dem String und passt in entsprechend für den OpCode
	 * an.
	 * 
	 * @param temp
	 *            String mit RY-Wert
	 * @return RY
	 */
	private int addRY(String temp) {
		return (Integer.parseInt(temp) << 8);
	}

	/**
	 * Entfernt das ersten und letzte Zeichen des Strings
	 * 
	 * @param temp
	 *            String mit Klammern
	 * @return String ohne Klammern
	 */
	private String removeClamps(String temp) {
		temp = temp.substring(1);
		temp = temp.substring(0, temp.length() - 1);
		return temp;
	}

	/**
	 * Entfernt das erste Zeichen des Strings, wenn es unerwünscht ist
	 * 
	 * @param temp
	 *            String mit unerwünschtem Zeichen an
	 * @param removed
	 *            unerwüschte Zeichen
	 * @return String ohne unerwünschten Zeichen
	 * @throws AssemblerException
	 */
	private String removeChar(String temp, char removed) throws AssemblerException {
		if (temp.charAt(0) == removed) {
			return temp.substring(1);
		} else {
			throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmCounter + 1) + " !");
		}
	}
}
