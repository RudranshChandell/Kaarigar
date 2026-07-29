package kaarigar.backend.service;

import kaarigar.backend.enums.TargetType;
import kaarigar.backend.enums.Role;
import kaarigar.backend.model.Booking;
import kaarigar.backend.model.Report;
import kaarigar.backend.model.User;
import kaarigar.backend.repository.BookingRepository;
import kaarigar.backend.repository.ReportRepository;
import kaarigar.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    // Create new report
    public Report createReport(Long userId, TargetType targetType, Long targetId, String reason) {
        User reportedBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        validateReportAccess(reportedBy, targetType, targetId);

        Report report = new Report();
        report.setReportedBy(reportedBy);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setStatus("PENDING");

        return reportRepository.save(report);
    }

    // Get all reports
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Get reports by target type
    public List<Report> getReportsByType(TargetType type) {
        return reportRepository.findByTargetType(type);
    }

    // Get reports by status
    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    // Get reports by reporting user
    public List<Report> getReportsByUser(Long userId) {
        return reportRepository.findByReportedById(userId);
    }

    // Update report status (Admin)
    public Report updateReportStatus(Long reportId, String status) {
        Optional<Report> optionalReport = reportRepository.findById(reportId);
        if (optionalReport.isEmpty()) {
            throw new RuntimeException("Report not found");
        }

        Report report = optionalReport.get();
        report.setStatus(status);
        return reportRepository.save(report);
    }

    // Delete report (Admin)
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new RuntimeException("Report not found");
        }
        reportRepository.deleteById(reportId);
    }

    private void validateReportAccess(User reportedBy, TargetType targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report target is required");
        }

        if (reportedBy.getRole() == Role.ADMIN) {
            return;
        }

        switch (targetType) {
            case BOOKING -> validateBookingReportAccess(reportedBy, targetId);
            case PROVIDER, CUSTOMER -> validateUserReportAccess(reportedBy, targetType, targetId);
        }
    }

    private void validateBookingReportAccess(User reportedBy, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        boolean isBookingCustomer = booking.getCustomer() != null
                && booking.getCustomer().getId().equals(reportedBy.getId());
        boolean isBookingProvider = booking.getProvider() != null
                && booking.getProvider().getId().equals(reportedBy.getId());

        if (!isBookingCustomer && !isBookingProvider) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only report bookings connected to you");
        }
    }

    private void validateUserReportAccess(User reportedBy, TargetType targetType, Long targetId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reported user not found"));

        if (reportedBy.getId().equals(target.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report yourself");
        }

        if (targetType == TargetType.PROVIDER) {
            validateCustomerCanReportProvider(reportedBy, target);
            return;
        }

        validateProviderCanReportCustomer(reportedBy, target);
    }

    private void validateCustomerCanReportProvider(User reportedBy, User target) {
        if (reportedBy.getRole() != Role.CUSTOMER || target.getRole() != Role.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customers can only report booked providers");
        }

        boolean hasSharedBooking = bookingRepository.existsByCustomerIdAndProviderId(reportedBy.getId(), target.getId());
        if (!hasSharedBooking) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only report providers connected through a booking");
        }
    }

    private void validateProviderCanReportCustomer(User reportedBy, User target) {
        if (reportedBy.getRole() != Role.PROVIDER || target.getRole() != Role.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Providers can only report booked customers");
        }

        boolean hasSharedBooking = bookingRepository.existsByCustomerIdAndProviderId(target.getId(), reportedBy.getId());
        if (!hasSharedBooking) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only report customers connected through a booking");
        }
    }
}
