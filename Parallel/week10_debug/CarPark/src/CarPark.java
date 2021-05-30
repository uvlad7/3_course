public class CarPark {
    private int places;
    private int cars;
    private boolean[] parking_places;

    public CarPark(int capacity) {
        places = capacity;
        cars = 0;
        parking_places = new boolean[capacity];
    }

    synchronized int arrive(int carNumber) throws InterruptedException {
        System.out.printf("Car №%d arrived.\n", carNumber);
        while (cars == places) {
            wait();
        }
        for (int i = 0; i < places; i++) {
            if (!parking_places[i]) {
                parking_places[i] = true;
                System.out.printf("Car №%d at place №%d.\n", carNumber, i + 1);
                cars++;
                notifyAll();
                return i;
            }
        }
        return -1;
    }

    synchronized boolean depart(int carNumber, int parkingNumber) throws InterruptedException {
        System.out.printf("Car №%d ready to departure.\n", carNumber);
        while (cars == 0) {
            wait();
        }
        if (parking_places[parkingNumber]) {
            parking_places[parkingNumber] = false;
            System.out.printf("Car №%d departured from place №%d.\n", carNumber, parkingNumber + 1);
            cars--;
            notifyAll();
            return true;
        }
        return false;
    }
}
