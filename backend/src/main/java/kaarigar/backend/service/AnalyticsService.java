package kaarigar.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kaarigar.backend.enums.BookingStatus;
import kaarigar.backend.enums.Role;
import kaarigar.backend.repository.BookingRepository;
import kaarigar.backend.repository.ReviewRepository;
import kaarigar.backend.repository.ServiceRepository;
import kaarigar.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final ServiceRepository serviceRepo;
    private final ReviewRepository reviewRepo;

    // Overall Summary Stats
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalUsers", userRepo.count());
        summary.put("totalProviders", userRepo.countByRole(Role.PROVIDER));
        summary.put("totalBookings", bookingRepo.count());
        summary.put("completedBookings", bookingRepo.countByStatus(BookingStatus.COMPLETED));
        summary.put("totalReviews", reviewRepo.count());
        return summary;
    }

    // Bookings grouped by month
    public List<Map<String, Object>> getBookingsPerMonth() {
        return bookingRepo.findBookingsPerMonth();
    }

    // Top 5 providers by total bookings
    public List<Map<String, Object>> getTopProviders() {
        return bookingRepo.findTopProviders()
                          .stream()
                          .limit(5)
                          .toList();
    }

    // Most booked service categories
    public List<Map<String, Object>> getTopServices() {
        return bookingRepo.findTopServices();
    }

    // Location trends (most active areas)
    public List<Map<String, Object>> getLocationTrends() {
        return serviceRepo.findBookingsByLocation();
    }
}

