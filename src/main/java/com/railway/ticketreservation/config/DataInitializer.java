package com.railway.ticketreservation.config;

import com.railway.ticketreservation.model.Train;
import com.railway.ticketreservation.repository.TrainRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final TrainRepository trainRepository;

    // Constructor injection
    public DataInitializer(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Override
    public void run(String... args) {
        List<Train> trains = List.of(
                new Train(16589, "Rani Chennamma Express", "Express", "KSR Bengaluru", "Miraj",
                        "07:45", "07:50", "Daily", 50, 750.0),
                new Train(20661, "Vande Bharat Express", "Vande Bharat", "Dharwad", "KSR Bengaluru",
                        "13:10", "13:15", "Mon, Wed, Fri", 40, 1500.0),
                new Train(7378, "Vasco - Yesvantpur Express", "Express", "Vasco-da-Gama", "Yesvantpur",
                        "15:30", "15:35", "Wed, Fri", 30, 600.0),
                new Train(56903, "Hubballi - Gadag Passenger", "Passenger", "Hubballi", "Gadag",
                        "18:00", "18:10", "Daily", 20, 200.0),
                new Train(16535, "Gol Gumbaz Express", "Express", "Mysuru", "Solapur",
                        "03:25", "03:30", "Daily", 50, 800.0),
                new Train(17326, "Vishwamanava Express", "Express", "Belagavi", "Mysuru",
                        "11:20", "11:25", "Daily", 40, 700.0),
                new Train(7336, "Hubballi - Vijayapura Special", "Passenger", "Hubballi", "Vijayapura",
                        "21:40", "21:50", "Mon, Thu", 25, 300.0),
                new Train(12781, "Swarnajayanti Express", "Express", "Vasco-da-Gama", "Hazrat Nizamuddin",
                        "10:40", "10:45", "Tue, Sat", 50, 1200.0),
                new Train(16592, "Hampi Express", "Express", "Hubballi", "Mysuru",
                        "20:10", "20:15", "Daily", 40, 650.0),
                new Train(6545, "Yesvantpur - Karwar Express", "Express", "Yesvantpur", "Karwar",
                        "05:35", "05:40", "Thu, Sun", 30, 900.0)
        );
        trainRepository.saveAll(trains);
    }
}