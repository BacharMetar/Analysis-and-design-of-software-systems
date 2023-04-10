package PersentationLayer.Suppliers;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ServiceLayer.Suppliers.ReservationService;

public class ReservationSystem {
    private static ReservationService rs = new ReservationService();

    public static void help() {
        String manual = "";
        manual += "===========================================================================================";
        manual += "makereservation [auto/manual] [branch] = open the reservation menu.\n";
        manual += "    auto - enter lines in the format of \"[product_id] [amount]\"\n";
        manual += "    manual - enter lines in the format of \"[supplier_id] [product_id] [amount]\"\n";
        manual += "    for changing amount of product type the line again with the updated amount\n";
        manual += "    for closing the reservation menu enter \"done\"\n";
        manual += "    for aborting the reservation enter \"abort\"\n";
        manual += "    after completing the reservation. You will get a response with the reservation id if was successful\n";
        manual += "cancelreservation [reservation_id] = cancel the reservation\n";
        manual += "readyreservation [reservation_id] = make the reservation ready\n";
        manual += "closereservation [reservation_id] = close the reservation\n";
        manual += "receipt [reservation_id] = show all items, amounts, and prices for this reservation\n";
        manual += "reservations [supplier_id] = show all reservations history with the supplier\n";
        manual += "ready = for each supplier, show the destinations of the reservations that are ready\n";
        manual += "addProduct [id] [name] [manufacturer] = add a new product to the system\n";
        manual += "===========================================================================================";
        System.out.println(manual);
    }

    public static void makereservation(String[] commandTokens, Scanner scanner) {
        if (commandTokens.length != 3) {
            System.out.println("makereservation command requires 3 arguments");
            return;
        }

        if (commandTokens[1].equals("auto")) {
            makeAutoReservation(scanner, commandTokens[2]);
        } else if (commandTokens[1].equals("manual")) {
            makeManualReservation(scanner, commandTokens[2]);
        } else {
            System.out.println("only auto and manual commands are allowed for makereservation");
        }
    }

    private static void makeAutoReservation(Scanner scanner, String destinationBranch) {
        Map<Integer, Integer> productToAmount = new HashMap<>();
        String line;

        while (true) {
            line = scanner.nextLine();
            if (line.equals("done") || line.equals("abort"))
                break;
            String[] command = line.split(" ");
            if (command.length != 2) {
                System.out.println("The format of the command is \"[product_id] [amount]\". Please try again.\n");
                continue;
            }
            int productId = tryParseInt(command[0], Integer.MIN_VALUE);
            if (productId == Integer.MIN_VALUE) {
                System.out.println("product id must be an integer. Please try again.\n");
                continue;
            } else if (productId < 0) {
                System.out.println("product id must be greater or equal to 0. Please try again.\n");
                continue;
            }
            int amount = tryParseInt(command[1], Integer.MIN_VALUE);
            if (amount == Integer.MIN_VALUE) {
                System.out.println("amount must be an integer. Please try again.\n");
                continue;
            } else if (amount <= 0) {
                System.out.println("amount must be greater than 0. Please try again.\n");
                continue;
            }

            productToAmount.put(productId, amount);
        }

        if (line.equals("done") && productToAmount.size() > 0)
            System.out.println(rs.makeAutoReservation(productToAmount, destinationBranch));

    }

    private static void makeManualReservation(Scanner scanner, String destinationBranch) {
        Map<Integer, Map<Integer, Integer>> supplierToproductToAmount = new HashMap<>();
        String line;

        while (true) {
            line = scanner.nextLine();
            if (line.equals("done") || line.equals("abort"))
                break;

            String[] command = line.split(" ");
            if (command.length != 3) {
                System.out.println(
                        "The format of the command is \"[supplier_id] [product_id] [amount]\". Please try again.\n");
                continue;
            }
            int supplierId = tryParseInt(command[0], Integer.MIN_VALUE);
            if (supplierId <= 0) {
                System.out.println("supplier id must be a non negative integer. Please try again.\n");
                continue;
            }
            int productId = tryParseInt(command[1], Integer.MIN_VALUE);
            if (productId <= 0) {
                System.out.println("product id must be a non negative integer. Please try again.\n");
                continue;
            }
            int amount = tryParseInt(command[2], Integer.MIN_VALUE);
            if (amount <= 0) {
                System.out.println("amount must be a non negative integer. Please try again.\n");
                continue;
            }

            supplierToproductToAmount.computeIfAbsent(supplierId, k -> new HashMap<>()).put(productId, amount);

            line = scanner.nextLine();
        }

        if (line.equals("done") && supplierToproductToAmount.size() > 0)
            System.out.println(rs.makeManualReservation(supplierToproductToAmount, destinationBranch));

    }

    public static void cancelreservation(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("cancelreservation command requires 2 arguments");
            return;
        }

        int reservationId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (reservationId == Integer.MIN_VALUE) {
            System.out.println("reservation id must be an integer. Please try again.\n");
            return;
        }
        System.out.println(rs.cancelReservation(reservationId));
    }

    public static void readyreservation(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("readyreservation command requires 2 arguments");
            return;
        }
        int reservationId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (reservationId == Integer.MIN_VALUE) {
            System.out.println("reservation id must be an integer. Please try again.\n");
            return;
        }
        System.out.println(rs.makeReservationReady(reservationId));
    }

    public static void closereservation(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("closereservation command requires 2 arguments");
            return;
        }
        int reservationId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (reservationId == Integer.MIN_VALUE) {
            System.out.println("reservation id must be an integer. Please try again.\n");
            return;
        }
        System.out.println(rs.closeReservation(reservationId));
    }

    public static void receipt(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("receipt command must have 2 arguments. Please try again.\n");
            return;
        }
        int reservationId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (reservationId == Integer.MIN_VALUE) {
            System.out.println("reservation id must be an integer. Please try again.\n");
            return;
        }
        System.out.println(rs.getReservationReceipt(reservationId));
    }

    public static void reservations(String[] commandTokens) {
        if (commandTokens.length != 2) {
            System.out.println("reservations command must have 2 arguments. Please try again.\n");
            return;
        }
        int supplierId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (supplierId == Integer.MIN_VALUE) {
            System.out.println("supplier id must be an integer. Please try again.\n");
            return;
        }
        System.out.println(rs.getSupplierReservations(supplierId));
    }

    public static void ready() {
        System.out.println(rs.getReadySupplierToAddresses());
    }

    public static void addProduct(String[] commandTokens) {
        if (commandTokens.length != 4) {
            System.out.println("addProduct command must have 4 arguments. Please try again.\n");
            return;
        }
        int productId = tryParseInt(commandTokens[1], Integer.MIN_VALUE);
        if (productId == Integer.MIN_VALUE) {
            System.out.println("product id must be an integer. Please try again.\n");
            return;
        }
        String name = commandTokens[2];
        String manufacturer = commandTokens[3];
        System.out.println(rs.addProduct(productId, name, manufacturer));
    }

    public static int tryParseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
