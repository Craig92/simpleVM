# simpleVM
Eine einfache Virtuelle Maschine, die im Rahmen der Vorlesung "Hardwarenahe Programmierung" an der Hochschule Mannheim erstellt wurde.

Bei dieser Version der Virteullen Maschine sind folgende Besonderheiten zu berücksichtigen:

Register müssen immer mit einem 'R' beginnen: z.B ADD R3 , R4

Klammern müssen unmittelbar vor bzw. hinter einem Register stehen: z.B. MOV (R3) , R8

Werte müssen immer mit einem '#' beginnen: z.B LOAD #1000

Der Assembler gibt an an welcher Stelle im Assemblercode ein fehler aufgetreten ist und es werden entsprechende Fehlermeldungen ausgegeben.

Eine genau Beschreibung der Aufgabestellung gib es hier:
http://services.informatik.hs-mannheim.de/~fischer/lectures/HWP_Files/Pflichtuebung.pdf

