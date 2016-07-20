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
	 * Erzeugt einen Assembler.
	 * 
	 * @param vm
	 *            Dazugehörige Virtuelle Maschine
	 */
	public MyAssembler(MyVirtualMachine vm) {

		this.vm = vm;
	}

	/**
	 * Startet den Assembler, der eine Textdatei zeilenweise liest und die
	 * runAssembler Methode für jede Zeile aufruft bis das Ende der Datei
	 * erreicht wurde
	 * 
	 * @param inputData
	 *            Dateiname des Assamblercodedatei
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
				// run()-Methode auf, wenn es noch eine Zeile gibt.
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
		vm.setProfiler(programmCounter);
	}

	/**
	 * Wandelt zeilenweise den Programmcode in OpCode um.
	 * 
	 * @param programmCode
	 *            Aktueller Programmcode aus der Datei
	 * @param vm
	 *            Die verwendete Virtuelle Maschine.
	 * @throws AssemblerException
	 */
	private void runAssembler(String programmCode) throws AssemblerException {

		String command = "";
		String temp = "";
		int opCode = 0;
		int fromMemory;
		int toMemory;
		Scanner stringScanner = null;

		try {

			stringScanner = new Scanner(programmCode);

			// Liest den ersten Teil der Zeile mit dem Befehl.
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

				temp = removeChar(temp, '#');

				opCode += addWert(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "MOV":
				opCode = MOV;
				temp = stringScanner.next();

				// Überliest die Klammern bei dem ersten Register.
				if (temp.charAt(0) == '(') {
					temp = temp.substring(1);
					temp = temp.substring(0, temp.length() - 1);
					toMemory = 1;
				} else {
					toMemory = 0;
				}

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				// Überliest die Klammern bei dem zweiten Register.
				if (temp.charAt(0) == '(') {
					temp = temp.substring(1);
					temp = temp.substring(0, temp.length() - 1);
					fromMemory = 1;
				} else {
					fromMemory = 0;
				}

				temp = removeChar(temp, 'R');

				opCode += addRY(temp);
				opCode += (fromMemory << 12);
				opCode += (toMemory << 13);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "ADD":
				opCode = ADD;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');
				opCode += addRY(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;
			case "SUB":
				opCode = SUB;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				stringScanner.next();

				// Überliest das Komma.
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRY(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "MUL":
				opCode = MUL;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				stringScanner.next();

				// Überliest das ,-Zeichen.
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRY(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "DIV":
				opCode = DIV;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				stringScanner.next();

				// Überliest das ,-Zeichen.
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRY(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "PUSH":

				opCode = PUSH;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "POP":

				opCode = POP;
				temp = stringScanner.next();

				temp = removeChar(temp, 'R');

				opCode += addRX(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JMP":
				opCode = JMP;
				temp = stringScanner.next();

				temp = removeChar(temp, '#');

				opCode += addWert(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JIZ":
				opCode = JIZ;
				temp = stringScanner.next();

				temp = removeChar(temp, '#');

				opCode += addWert(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JIH":
				opCode = JIH;
				temp = stringScanner.next();

				temp = removeChar(temp, '#');

				opCode += addWert(temp);
				vm.getMemory()[programmCounter++] = opCode;
				break;

			case "JSR":
				opCode = JSR;
				temp = stringScanner.next();

				temp = removeChar(temp, '#');

				opCode += addWert(temp);
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

	private int addWert(String temp) {
		return (Integer.parseInt(temp) << 4);
	}

	private int addRX(String temp) {
		return (Integer.parseInt(temp) << 4);
	}

	private int addRY(String temp) {
		return (Integer.parseInt(temp) << 8);
	}

	private int addFromMemory(String temp) {
		return 0;
	}

	private int addToMemory(String temp) {
		return 0;
	}

	private String removeChar(String temp, char removed) throws AssemblerException {
		if (temp.charAt(0) == removed) {
			return temp.substring(1);
		} else {
			throw new AssemblerException("Code ist fehlerhaft in Zeile " + (programmCounter + 1) + " !");
		}
	}
}
