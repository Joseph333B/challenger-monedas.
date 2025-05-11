import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Principal {
    private static final String API_URL =
            "https://v6.exchangerate-api.com/v6/2920b04351c83ef812f5ff2c/latest/USD";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Sea bienvenido/a al Convertor de Monedas");

        gson ratesData = fetchRates();

        Map<String, Double> rates = ratesData.getConversion_rates();


        String[] codes = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};

        String[] descriptions = {
                "ARS - Peso argentino",
                "BOB - Boliviano boliviano",
                "BRL - Real brasileño",
                "CLP - Peso chileno",
                "COP - Peso colombiano",
                "USD - Dólar estadounidense"
        };

        int option;
        do {
            for (int i = 0; i < descriptions.length; i++) {
                System.out.printf("%d. %s%n", i + 1, descriptions[i]);
            }
            System.out.println("0. Salir");
            System.out.print("> ");

            option = readOption(scanner, 0, codes.length);
            if (option == 0) break;

            String fromCode = codes[option - 1];

            System.out.println("Elige a cuál deseas convertir:");
            for (int i = 0; i < descriptions.length; i++) {
                System.out.printf("%d. %s%n", i + 1, descriptions[i]);
            }
            System.out.print("> ");

            int toOpt = readOption(scanner, 1, codes.length);
            String toCode = codes[toOpt - 1];

            double amount = readAmount(scanner);

            Double fromRate = rates.get(fromCode);
            Double toRate = rates.get(toCode);


            double inUsd = amount / fromRate;
            double result = inUsd * toRate;

            System.out.printf("%.2f %s = %.2f %s%n",
                    amount, fromCode, result, toCode);

        } while (true);

        System.out.println("¡Gracias por usar el Conversor de Moneda!");
        scanner.close();
    }

    private static gson fetchRates() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("HTTP error: " + response.statusCode());
                return null;
            }
            String json = response.body();
            return new Gson().fromJson(json, gson.class);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("Error al parsear JSON: " + e.getMessage());
        }
        return null;
    }

    private static int readOption(Scanner scanner, int min, int max) {
        int option = -1;
        boolean valid = false;
        while (!valid) {
            try {
                option = scanner.nextInt();
                if (option >= min && option <= max) {
                    valid = true;
                } else {
                    System.out.println("Elija una opción válida.");
                    System.out.print("> ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Debe ingresar un número.");
                System.out.print("> ");
                scanner.next();
            }
        }
        return option;
    }

    private static double readAmount(Scanner scanner) {
        double amount = -1;
        boolean valid = false;
        while (!valid) {
            System.out.print("Cantidad a convertir: ");
            try {
                amount = scanner.nextDouble();
                if (amount > 0) {
                    valid = true;
                } else {
                    System.out.println("Debe ingresar un monto positivo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Ingrese un número válido.");
                scanner.next();
            }
        }
        return amount;
    }
}