import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
class Operatie {
    Operatie left;  // operatie sau numar la stanga operatorului
    Operatie right; // operatie sau numar la dreapta operatorului
    String simbol;  // operator sau numar

    // Constructor pentru noduri care reprezinta operatori
    public Operatie(Operatie left, Operatie right, String simbol) {
        this.left = left;
        this.right = right;
        this.simbol = simbol;
    }

    // Constructor pentru noduri care reprezinta numere
    public Operatie(String simbol) {
        this.simbol = simbol;
    }

    // Metoda rezultat calculeaza rezultatul operatiei
    public double rezultat() {
        double rez;
        switch (simbol) {
            case "+" -> rez = left.rezultat() + right.rezultat();
            case "-" -> rez = left.rezultat() - right.rezultat();
            case "*" -> rez = left.rezultat() * right.rezultat();
            case "/" -> rez = left.rezultat() / right.rezultat();
            case "**" -> rez = Math.pow(left.rezultat(), right.rezultat());
            case "//" -> rez = Math.pow(left.rezultat(), 1.0 / right.rezultat());
            case "%" -> rez = left.rezultat() % right.rezultat();
            default -> rez = Double.parseDouble(simbol); // cazul cand nodul este un numar
        }
        return rez;
    }
}

public class Main {
    public static void main(String[] args) {
        File file = new File("operatii.txt");  // fisierul cu operatii

        try {
            Scanner scanner = new Scanner(file);

            // Citeste fiecare linie din fisier
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line, " ");

                Deque<String> stack = new ArrayDeque<>();  // stiva pentru operatori
                List<String> postfix = new ArrayList<>();  // lista pentru expresia postfixata

                // Transforma expresia in forma postfixata
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    // Daca tokenul este un numar, il adauga in lista postfix
                    if (token.matches("\\d+")) {
                        postfix.add(token);
                    } else if (token.equals("(")) { // Daca tokenul este o paranteza deschisa, il adauga in stiva
                        stack.push(token);
                    } else if (token.equals(")")) { // Daca tokenul este o paranteza inchisa, muta operatorii din stiva in lista postfix pana la paranteza deschisa
                        while (!stack.isEmpty() && !stack.peek().equals("(")) {
                            postfix.add(stack.pop());
                        }
                        stack.pop(); // Elimina paranteza deschisa din stiva
                    } else { // Daca tokenul este un operator
                        // Muta operatorii cu precedenta mai mare sau egala din stiva in lista postfix
                        while (!stack.isEmpty() && !stack.peek().equals("(") && precedence(token) <= precedence(stack.peek())) {
                            postfix.add(stack.pop());
                        }
                        stack.push(token); // Adauga operatorul curent in stiva
                    }
                }
                // Muta toti operatorii ramasi in stiva in lista postfix
                while (!stack.isEmpty()) {
                    postfix.add(stack.pop());
                }

                // Construieste arborele de operatii
                Deque<Operatie> treeStack = new ArrayDeque<>();
                for (String token : postfix) {
                    // Daca tokenul este un numar, creeaza un nod si il adauga in stiva
                    if (token.matches("\\d+")) {
                        treeStack.push(new Operatie(token));
                    } else { // Daca tokenul este un operator
                        // Creeaza un nod cu operatorul si cu ultimele doua noduri adaugate in stiva ca fii
                        Operatie right = treeStack.pop(); // Nodul drept
                        Operatie left = null; // Nodul stang
                        if (!treeStack.isEmpty()) {
                            left = treeStack.pop();
                        }
                        treeStack.push(new Operatie(left, right, token)); // Adauga nodul in stiva
                    }
                }
                // Calculeaza si afiseaza rezultatul operatiei
                System.out.println("Result: " + treeStack.pop().rezultat());
            }
            scanner.close(); // Inchide scannerul
        } catch (FileNotFoundException e) { // Trateaza exceptia in cazul in care fisierul nu a fost gasit
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Metoda precedence determina precedenta operatorilor
    public static int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "**", "//", "%" -> 3;
            default -> -1;
        };
    }
}