// VehicleType.java
public enum VehicleType {
    TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER
}

// Vehicle.java
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }
}

// Car.java
public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.FOUR_WHEELER);
    }
}

// Scooter.java
public class Scooter extends Vehicle {
    public Scooter(String licensePlate) {
        super(licensePlate, VehicleType.TWO_WHEELER);
    }
}

// Auto.java
public class Auto extends Vehicle {
    public Auto(String licensePlate) {
        super(licensePlate, VehicleType.THREE_WHEELER);
    }
}

// ParkingSpot.java
public class ParkingSpot {
    private int spotNumber;
    private VehicleType type;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(int spotNumber, VehicleType type) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public VehicleType getType() {
        return type;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public void parkVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
}

// ParkingFloor.java
import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, int twoWheelerSpots, int threeWheelerSpots, int fourWheelerSpots) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        int spotCounter = 1;
        for (int i = 0; i < twoWheelerSpots; i++) {
            spots.add(new ParkingSpot(spotCounter++, VehicleType.TWO_WHEELER));
        }
        for (int i = 0; i < threeWheelerSpots; i++) {
            spots.add(new ParkingSpot(spotCounter++, VehicleType.THREE_WHEELER));
        }
        for (int i = 0; i < fourWheelerSpots; i++) {
            spots.add(new ParkingSpot(spotCounter++, VehicleType.FOUR_WHEELER));
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public ParkingSpot findFreeSpot(VehicleType type) {
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.getType() == type) {
                return spot;
            }
        }
        return null;
    }
}

// Ticket.java
import java.time.LocalDateTime;

public class Ticket {
    private String ticketId;
    private String vehicleLicensePlate;
    private LocalDateTime entryTime;
    private int floorNumber;
    private int spotNumber;

    public Ticket(String ticketId, String vehicleLicensePlate, LocalDateTime entryTime, int floorNumber, int spotNumber) {
        this.ticketId = ticketId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.entryTime = entryTime;
        this.floorNumber = floorNumber;
        this.spotNumber = spotNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getSpotNumber() {
        return spotNumber;
    }
}

// ParkingLot.java
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParkingLot {
    private static final double HOURLY_RATE = 15.0;
    private List<ParkingFloor> floors;

    public ParkingLot(int numberOfFloors, int twoWheelerSpots, int threeWheelerSpots, int fourWheelerSpots) {
        this.floors = new ArrayList<>();
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new ParkingFloor(i, twoWheelerSpots, threeWheelerSpots, fourWheelerSpots));
        }
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findFreeSpot(vehicle.getType());
            if (spot != null) {
                spot.parkVehicle(vehicle);
                String ticketId = UUID.randomUUID().toString();
                System.out.println("✅ Vehicle parked successfully!");
                System.out.println("Ticket ID: " + ticketId);
                return new Ticket(ticketId, vehicle.getLicensePlate(), LocalDateTime.now(), floor.getFloorNumber(), spot.getSpotNumber());
            }
        }
        System.out.println("😔 No parking spot available for this vehicle type.");
        return null;
    }

    public double exitVehicle(Ticket ticket) {
        LocalDateTime exitTime = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), exitTime);
        if (ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime) % 60 > 0) {
            hours++; // Rounds up to the next hour for any fraction of an hour
        }

        double totalFare = hours * HOURLY_RATE;

        // Find and free the parking spot
        ParkingFloor floor = floors.get(ticket.getFloorNumber() - 1);
        for (ParkingSpot spot : floor.getSpots()) { // Assuming a getSpots method in ParkingFloor
            if (spot.getSpotNumber() == ticket.getSpotNumber()) {
                spot.removeVehicle();
                break;
            }
        }

        System.out.println("✅ Vehicle exited successfully!");
        System.out.println("Total duration: " + hours + " hours.");
        System.out.println("Total fare: ₹" + totalFare);
        return totalFare;
    }
}

// Main.java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create a parking lot with 2 floors, each with 5 two-wheeler, 3 three-wheeler, and 4 four-wheeler spots.
        ParkingLot myParkingLot = new ParkingLot(2, 5, 3, 4);

        // Park some vehicles
        Vehicle scooter1 = new Scooter("MH12A1234");
        Ticket scooterTicket = myParkingLot.parkVehicle(scooter1);

        Vehicle car1 = new Car("KA01B5678");
        Ticket carTicket = myParkingLot.parkVehicle(car1);

        Vehicle auto1 = new Auto("DL02C9012");
        Ticket autoTicket = myParkingLot.parkVehicle(auto1);

        System.out.println("\nParking in progress. Waiting for 2.5 hours to calculate fare...");
        // Simulate waiting for 2.5 hours
        Thread.sleep(2 * 60 * 60 * 1000 + 30 * 60 * 1000);

        // Exit vehicles
        if (scooterTicket != null) {
            myParkingLot.exitVehicle(scooterTicket);
        }
        if (carTicket != null) {
            myParkingLot.exitVehicle(carTicket);
        }
        if (autoTicket != null) {
            myParkingLot.exitVehicle(autoTicket);
        }
    }
}
