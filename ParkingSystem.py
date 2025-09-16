### Class Definitions

#### `Vehicle`

from abc import ABC, abstractmethod


class Vehicle(ABC):
    def __init__(self, license_plate):
        self.license_plate = license_plate

    @abstractmethod
    def get_type(self):
        pass


class Car(Vehicle):
    def get_type(self):
        return "4-wheeler"


class Scooter(Vehicle):
    def get_type(self):
        return "2-wheeler"


class Auto(Vehicle):
    def get_type(self):
        return "3-wheeler"


#### `ParkingSlot`

class ParkingSlot:
    def __init__(self, slot_id, slot_type):
        self.slot_id = slot_id
        self.slot_type = slot_type
        self.is_occupied = False
        self.vehicle = None

    def park_vehicle(self, vehicle):
        if not self.is_occupied and self.slot_type == vehicle.get_type():
            self.is_occupied = True
            self.vehicle = vehicle
            return True
        return False

    def unpark_vehicle(self):
        if self.is_occupied:
            self.is_occupied = False
            self.vehicle = None
            return True
        return False

class ParkingFloor:
    def __init__(self, floor_number, slot_capacities):
        self.floor_number = floor_number
        self.slots = {
            "2-wheeler": [],
            "3-wheeler": [],
            "4-wheeler": []
        }
        for slot_type, count in slot_capacities.items():
            for i in range(count):
                self.slots[slot_type].append(ParkingSlot(f"F{floor_number}-{slot_type[0]}-{i + 1}", slot_type))

    def find_empty_slot(self, vehicle_type):
        for slot in self.slots[vehicle_type]:
            if not slot.is_occupied:
                return slot
        return None

    def unpark_vehicle(self, slot_id):
        for slot_type in self.slots:
            for slot in self.slots[slot_type]:
                if slot.slot_id == slot_id:
                    return slot.unpark_vehicle()
        return False



from datetime import datetime


class Ticket:
    def __init__(self, vehicle, parking_slot):
        self.vehicle = vehicle
        self.entry_time = datetime.now()
        self.parking_slot = parking_slot


class ParkingSystem:
    def __init__(self, num_floors, capacities_per_floor):
        self.floors = [ParkingFloor(i + 1, capacities_per_floor) for i in range(num_floors)]
        self.tickets = {}  # Stores ticket objects with license plate as key
        self.pricing = {
            "per_hour": 10,  # ₹10 per hour
            "per_minute": 0.5  # ₹0.5 per minute
        }

    def park_vehicle(self, vehicle):
        vehicle_type = vehicle.get_type()
        for floor in self.floors:
            slot = floor.find_empty_slot(vehicle_type)
            if slot:
                if slot.park_vehicle(vehicle):
                    ticket = Ticket(vehicle, slot)
                    self.tickets[vehicle.license_plate] = ticket
                    print(f"Vehicle parked successfully! Ticket ID: {ticket.parking_slot.slot_id}")
                    return ticket
        print("Sorry, no available slot for your vehicle type.")
        return None

    def exit_vehicle(self, license_plate):
        if license_plate not in self.tickets:
            print("Invalid license plate. Ticket not found.")
            return

        ticket = self.tickets[license_plate]
        slot_id = ticket.parking_slot.slot_id

        parking_duration = (datetime.now() - ticket.entry_time).total_seconds() / 60

        cost = self.calculate_cost(parking_duration)

        for floor in self.floors:
            if floor.unpark_vehicle(slot_id):
                del self.tickets[license_plate]
                print(
                    f"Vehicle exited. Parking duration: {round(parking_duration, 2)} mins. Total cost: ₹{round(cost, 2)}")
                return cost

        print("Error unparking the vehicle.")

    def calculate_cost(self, minutes):
        hours = minutes // 60
        remaining_minutes = minutes % 60

        cost = (hours * self.pricing["per_hour"]) + (remaining_minutes * self.pricing["per_minute"])
        return cost


if __name__ == "__main__":
    # Define capacities per floor
    capacities = {
        "2-wheeler": 10,
        "3-wheeler": 5,
        "4-wheeler": 8
    }

    # Initialize the parking system with 3 floors
    parking_lot = ParkingSystem(num_floors=3, capacities_per_floor=capacities)

    # Simulate vehicles entering the parking lot
    car1 = Car("UP16 1234")
    scooter1 = Scooter("DL05 5678")
    auto1 = Auto("MH01 9101")

    # Park the vehicles
    ticket1 = parking_lot.park_vehicle(car1)
    ticket2 = parking_lot.park_vehicle(scooter1)
    ticket3 = parking_lot.park_vehicle(auto1)

    # Wait for a bit to simulate parking time (e.g., a few minutes)
    import time

    time.sleep(30)  # Wait for 30 seconds to show a non-zero cost

    # Simulate vehicles exiting
    parking_lot.exit_vehicle("UP16 1234")
    parking_lot.exit_vehicle("MH01 9101")
