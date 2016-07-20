package simpleVM;

import java.util.Scanner;

public class MyMenu {

	public static void main(String[] args) {

		MyVirtualMachine virtualMachine = new MyVirtualMachine(16, 4096);
		MyAssembler assembler = new MyAssembler(virtualMachine);

		System.out.println("Es wird eine Virtuelle Maschine mit folgenden Eigenschaften iniziallisiert:");
		System.out.print(virtualMachine.getRegisterSize() + " Register | ");
		System.out.println(virtualMachine.getMemorySize() + " Speicheradressen");
		System.out.println("____________________________________________________");

		System.out.println("Geben Sie den Dateinamen des Programms ein:");

		Scanner scanner = new Scanner(System.in);

		String fileName = scanner.nextLine();
				
		System.out.println("");

		try {
			assembler.startAssembler(fileName);

			System.out.println("Der Assembler hat die Datei verarbeitet.");
			System.out.println("____________________________________________________");

			virtualMachine.startVirtualMaschine();

			System.out.println("____________________________________________________");
			System.out.println("Die Virtuelle Maschine hat das Programm verarbeitet.");

			System.out.println("Der Profiler hat seine Daten in die ProfilerFile.txt Datei geschrieben.");
			virtualMachine.getProfilerData();

		} catch (AssemblerException e) {
			System.err.println("Aufgrund eines Fehlers im Assembler wurde das Programm unerwartet beendet!");
		} catch (VirtualMachineException e) {
			System.err.println("Aufgrund eines Fehlers in der Virtuellen Maschine wurde das Programm unerwartet beendet!");
		}

		scanner.close();
	}
}
