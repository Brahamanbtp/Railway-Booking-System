import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

class Train {
    int trainNumber;
    String trainName, departure, destination;
    double firstClassFare, secondClassFare;
    int firstClassSeats, secondClassSeats;
    ArrayList<Boolean> firstClassAvailability, secondClassAvailability;

    public Train(int trainNumber, String trainName, String departure, String destination, double firstClassFare, double secondClassFare, int firstClassSeats, int secondClassSeats) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.departure = departure;
        this.destination = destination;
        this.firstClassFare = firstClassFare;
        this.secondClassFare = secondClassFare;
        this.firstClassSeats = firstClassSeats;
        this.secondClassSeats = secondClassSeats;
        this.firstClassAvailability = initAvailability(firstClassSeats);
        this.secondClassAvailability = initAvailability(secondClassSeats);
    }

    private ArrayList<Boolean> initAvailability(int seats) {
        return new ArrayList<>(Collections.nCopies(seats, true));
    }
}

interface BookingOperations {
    void book(String passengerName, int trainNumber, int seatNumber, int coachClass) throws BookingException;
    void cancel(int ticketNumber) throws BookingException;
    void displayTrains();
    void displayBookedTickets();
    void displaySeatAvailability(int trainNumber, int coachClass);
}

class RailwayBookingSystem implements BookingOperations {
    ArrayList<Train> trains = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    int bookingCounter = 1;

    public RailwayBookingSystem() {
        trains.add(new Train(101, "Express", "City A", "City B", 100.0, 50.0, 20, 30));
        trains.add(new Train(102, "Local", "City C", "City D", 80.0, 40.0, 10, 20));
        trains.add(new Train(103, "Superfast", "City E", "City F", 120.0, 60.0, 30, 40));
        trains.add(new Train(104, "Slow", "City G", "City H", 90.0, 45.0, 15, 25));
    }

    public void book(String passengerName, int trainNumber, int seatNumber, int coachClass) throws BookingException {
        Train train = findTrain(trainNumber);
        if (train == null) throw new BookingException("Train not found");

        ArrayList<Boolean> availability = (coachClass == 1) ? train.firstClassAvailability : train.secondClassAvailability;
        if (seatNumber < 1 || seatNumber > (coachClass == 1 ? train.firstClassSeats : train.secondClassSeats) || !availability.get(seatNumber - 1)) {
            throw new BookingException("Seat not available");
        }

        Booking booking = new Booking(bookingCounter++, passengerName, train, seatNumber, coachClass);
        bookings.add(booking);
        availability.set(seatNumber - 1, false);
        System.out.println("Ticket booked successfully. Ticket Number: " + booking.ticketNumber);
    }

    public void cancel(int ticketNumber) throws BookingException {
        Booking bookingToRemove = findBooking(ticketNumber);
        if (bookingToRemove == null) throw new BookingException("Ticket not found");

        ArrayList<Boolean> availability = (bookingToRemove.coachClass == 1) ?
            bookingToRemove.train.firstClassAvailability : bookingToRemove.train.secondClassAvailability;

        availability.set(bookingToRemove.seatNumber - 1, true);
        bookings.remove(bookingToRemove);
        System.out.println("Ticket canceled successfully. Ticket Number: " + ticketNumber);
    }

    public void displayTrains() {
        System.out.println("Train Information:");
        trains.forEach(train -> {
            System.out.println("Train Number: " + train.trainNumber);
            System.out.println("Train Name: " + train.trainName);
            System.out.println("Departure: " + train.departure);
            System.out.println("Destination: " + train.destination);
            System.out.println("First Class Fare: $" + train.firstClassFare);
            System.out.println("Second Class Fare: $" + train.secondClassFare);
            System.out.println("Total First Class Seats: " + train.firstClassSeats);
            System.out.println("Total Second Class Seats: " + train.secondClassSeats);
            System.out.println();
        });
    }

    public void displayBookedTickets() {
        if (bookings.isEmpty()) {
            System.out.println("No tickets have been booked yet.");
            return;
        }

        System.out.println("Booked Tickets:");
        bookings.forEach(booking -> {
            System.out.println("Ticket Number: " + booking.ticketNumber);
            System.out.println("Passenger Name: " + booking.passengerName);
            System.out.println("Train Number: " + booking.train.trainNumber);
            System.out.println("Train Name: " + booking.train.trainName);
            System.out.println("Seat Number: " + booking.seatNumber);
            System.out.println("Coach Class: " + (booking.coachClass == 1 ? "First Class" : "Second Class"));
            System.out.println();
        });
    }

    public void displaySeatAvailability(int trainNumber, int coachClass) {
        Train train = findTrain(trainNumber);
        if (train == null) {
            System.out.println("Train not found.");
            return;
        }

        ArrayList<Boolean> availability = (coachClass == 1) ? train.firstClassAvailability : train.secondClassAvailability;
        List<Integer> availableSeats = IntStream.range(0, availability.size())
            .filter(seatIndex -> availability.get(seatIndex))
            .mapToObj(seatIndex -> seatIndex + 1)
            .collect(Collectors.toList());

        System.out.println("Available " + (coachClass == 1 ? "First Class" : "Second Class") + " Seats for Train " + trainNumber + ":");
        System.out.println(availableSeats.isEmpty() ? "No available seats." : availableSeats);
    }

    public Train findTrain(int trainNumber) {
        return trains.stream().filter(train -> train.trainNumber == trainNumber).findFirst().orElse(null);
    }

    private Booking findBooking(int ticketNumber) {
        return bookings.stream().filter(booking -> booking.ticketNumber == ticketNumber).findFirst().orElse(null);
    }
}

class Booking {
    int ticketNumber, coachClass, seatNumber;
    String passengerName;
    Train train;

    public Booking(int ticketNumber, String passengerName, Train train, int seatNumber, int coachClass) {
        this.ticketNumber = ticketNumber;
        this.passengerName = passengerName;
        this.train = train;
        this.seatNumber = seatNumber;
        this.coachClass = coachClass;
    }
}

public class Main {
    public static void main(String[] args) {
        RailwayBookingSystem bookingSystem = new RailwayBookingSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Book Ticket");
            System.out.println("2. Cancel Ticket");
            System.out.println("3. Display Booked Tickets");
            System.out.println("4. Display Train Information");
            System.out.println("5. Display Seat Availability");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter passenger name: ");
                        String passengerName = scanner.nextLine();
                        System.out.print("Enter train number: ");
                        int trainNumber = scanner.nextInt();
                        int coachClass;
                        int seatNumber;

                        do {
                            System.out.print("Enter seat number: ");
                            seatNumber = scanner.nextInt();
                            System.out.print("Enter coach class (1 for First Class, 2 for Second Class): ");
                            coachClass = scanner.nextInt();
                            if (!isSeatAvailable(bookingSystem, trainNumber, coachClass, seatNumber)) {
                                System.out.println("Error: Seat not available");
                            }
                        } while (!isSeatAvailable(bookingSystem, trainNumber, coachClass, seatNumber));

                        bookingSystem.book(passengerName, trainNumber, seatNumber, coachClass);
                        break;
                    case 2:
                        System.out.print("Enter ticket number to cancel: ");
                        int ticketNumber = scanner.nextInt();
                        bookingSystem.cancel(ticketNumber);
                        break;
                    case 3:
                        bookingSystem.displayBookedTickets();
                        break;
                    case 4:
                        bookingSystem.displayTrains();
                        break;
                    case 5:
                        System.out.print("Enter train number: ");
                        int trainToCheck = scanner.nextInt();
                        System.out.print("Enter coach class (1 for First Class, 2 for Second Class): ");
                        int coachToCheck = scanner.nextInt();
                        bookingSystem.displaySeatAvailability(trainToCheck, coachToCheck);
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (BookingException e) {
                System.out.println("Error: " + e.getMessage());
                scanner.close();
            }
        }
    }

    private static boolean isSeatAvailable(RailwayBookingSystem bookingSystem, int trainNumber, int coachClass, int seatNumber) {
        Train train = bookingSystem.findTrain(trainNumber);
        if (train == null) {
            System.out.println("Train not found.");
            return false;
        }

        ArrayList<Boolean> availability = (coachClass == 1) ? train.firstClassAvailability : train.secondClassAvailability;
        if (seatNumber < 1 || seatNumber > (coachClass == 1 ? train.firstClassSeats : train.secondClassSeats) || !availability.get(seatNumber - 1)) {
            return false;
        }

        return true;
    }
}
