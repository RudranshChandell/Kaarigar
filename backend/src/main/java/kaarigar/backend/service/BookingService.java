package kaarigar.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kaarigar.backend.enums.BookingStatus;
import kaarigar.backend.model.Booking;
import kaarigar.backend.model.User;
import kaarigar.backend.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
public class BookingService {

    private static final DateTimeFormatter SLOT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    @Autowired
    private BookingRepository bookingRepository;

    // Create a new booking
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Get bookings by customer
    public List<Booking> getBookingsByCustomer(User customer) {
        processAutomaticBookingUpdates();
        return bookingRepository.findByCustomer(customer);
    }

    // Get bookings by provider
    public List<Booking> getBookingsByProvider(User provider) {
        processAutomaticBookingUpdates();
        return bookingRepository.findByProvider(provider);
    }

    // Get a single booking by ID
    public Booking getBookingById(Long bookingId) {
        processAutomaticBookingUpdates();
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // Update booking status
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = getBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED && status != BookingStatus.CANCELLED) {
            return booking;
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        processAutomaticBookingUpdates();
        return bookingRepository.findAll();
    }

    // Provider marks booking complete
    public Booking markCompleteByProvider(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setProviderMarkedComplete(true);

        // If customer already verified, mark status COMPLETED
        if (Boolean.TRUE.equals(booking.getCustomerVerified())) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        return bookingRepository.save(booking);
    }

    // Customer verifies booking completion
    public Booking verifyByCustomer(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.setCustomerVerified(true);

        // Only mark COMPLETED if provider already marked complete
        if (Boolean.TRUE.equals(booking.getProviderMarkedComplete())) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void processAutomaticBookingUpdates() {
        LocalDateTime now = LocalDateTime.now();
        cancelExpiredPendingBookings(now);
        markOverdueConfirmedBookingsCompleteByProvider(now);
    }

    private void cancelExpiredPendingBookings(LocalDateTime now) {
        List<Booking> expiredBookings = bookingRepository.findByStatus(BookingStatus.PENDING).stream()
                .filter(booking -> hasBookingSlotPassed(booking, now))
                .peek(booking -> booking.setStatus(BookingStatus.CANCELLED))
                .toList();

        if (!expiredBookings.isEmpty()) {
            bookingRepository.saveAll(expiredBookings);
        }
    }

    private void markOverdueConfirmedBookingsCompleteByProvider(LocalDateTime now) {
        List<Booking> overdueBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED).stream()
                .filter(booking -> !Boolean.TRUE.equals(booking.getProviderMarkedComplete()))
                .filter(booking -> hasBookingBeenAwaitingProviderCompletionForThreeDays(booking, now))
                .peek(booking -> {
                    booking.setProviderMarkedComplete(true);
                    if (Boolean.TRUE.equals(booking.getCustomerVerified())) {
                        booking.setStatus(BookingStatus.COMPLETED);
                    }
                })
                .toList();

        if (!overdueBookings.isEmpty()) {
            bookingRepository.saveAll(overdueBookings);
        }
    }

    private boolean hasBookingSlotPassed(Booking booking, LocalDateTime now) {
        LocalDateTime slotEndDateTime = getBookingSlotEndDateTime(booking);
        if (slotEndDateTime == null) {
            LocalDate bookingDate = booking.getBookingDate();
            return bookingDate != null && bookingDate.isBefore(now.toLocalDate());
        }

        return slotEndDateTime.isBefore(now);
    }

    private boolean hasBookingBeenAwaitingProviderCompletionForThreeDays(Booking booking, LocalDateTime now) {
        LocalDateTime slotEndDateTime = getBookingSlotEndDateTime(booking);
        if (slotEndDateTime == null) {
            return false;
        }

        return !slotEndDateTime.plusDays(3).isAfter(now);
    }

    private LocalDateTime getBookingSlotEndDateTime(Booking booking) {
        LocalDate bookingDate = booking.getBookingDate();
        LocalTime slotEndTime = parseSlotEndTime(booking.getTimeSlot());
        if (bookingDate == null || slotEndTime == null) {
            return null;
        }

        return LocalDateTime.of(bookingDate, slotEndTime);
    }

    private LocalTime parseSlotEndTime(String timeSlot) {
        if (timeSlot == null || timeSlot.isBlank() || !timeSlot.contains("-")) {
            return null;
        }

        String endTime = timeSlot.substring(timeSlot.lastIndexOf('-') + 1).trim().toUpperCase(Locale.ENGLISH);
        try {
            return LocalTime.parse(endTime, SLOT_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
