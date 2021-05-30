public class Main {

    private static CarPark park;

    public static void main(String[] args) throws InterruptedException {
        park = new CarPark(Integer.parseInt(args[0]));
        int size = Integer.parseInt(args[1]);
        for (int i = 1; i <= size; i++) {
            new Thread(new CarParker(i)).start();
        }
    }

    public static class CarParker implements Runnable {
        private int carNumber;

        public CarParker(int carNumber) {
            this.carNumber = carNumber;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((int) (Math.random() * (100)) + 10);
                int parkingNumber = park.arrive(carNumber);
                if (parkingNumber != -1) {
                    new Thread(new CarDeparturer(carNumber, parkingNumber)).start();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static class CarDeparturer implements Runnable {
        private int carNumber;
        private int parkingNumber;

        public CarDeparturer(int carNumber, int parkingNumber) {
            this.carNumber = carNumber;
            this.parkingNumber = parkingNumber;
        }

        @Override
        public void run() {
            try {
                do {
                    Thread.sleep((int) (Math.random() * (100)) + 10);
                } while (!park.depart(carNumber, parkingNumber));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
