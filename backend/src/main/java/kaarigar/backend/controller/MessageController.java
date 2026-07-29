package kaarigar.backend.controller;

import kaarigar.backend.dto.ChatContactDTO;
import kaarigar.backend.dto.MessageDTO;
import kaarigar.backend.enums.Role;
import kaarigar.backend.model.Message;
import kaarigar.backend.model.User;
import kaarigar.backend.repository.BookingRepository;
import kaarigar.backend.service.MessageService;
import kaarigar.backend.service.ChatNotificationService;
import kaarigar.backend.repository.UserRepository;
import kaarigar.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;
    private final ChatNotificationService notificationService;
    private final BookingRepository bookingRepository;

    // ---------------- REST API ---------------- //

    @PostMapping
public ResponseEntity<MessageDTO> sendMessage(
        @RequestBody Message message,
        Principal principal
) {
   User sender;
if (principal instanceof UsernamePasswordAuthenticationToken) {
    sender = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
} else {
    throw new RuntimeException("Unauthenticated user attempted to send message");
}
message.setSender(sender);

    if (message.getReceiver() == null || message.getReceiver().getId() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver is required");
    }


    Long receiverId = message.getReceiver().getId();
    User receiver = userRepository.findById(Objects.requireNonNull(receiverId, "Receiver id is required"))
            .orElseThrow(() -> new RuntimeException("Receiver not found"));
        validateChatAccess(sender, receiver);
    message.setReceiver(receiver);

    Message saved = messageService.saveMessage(message);

    return ResponseEntity.ok(convertToDTO(saved));
}

    @GetMapping("/between/{userId}")
public ResponseEntity<List<MessageDTO>> getMessagesWithUser(
        @PathVariable Long userId,
        Principal principal
) {
    User currentUser;
    if (principal instanceof UsernamePasswordAuthenticationToken) {
        currentUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    } else {
        throw new RuntimeException("Unauthenticated user");
    }

    Long otherUserId = Objects.requireNonNull(userId, "User id is required");
    User otherUser = userRepository.findById(otherUserId)
            .orElseThrow(() -> new RuntimeException("Other user not found"));
        validateChatAccess(currentUser, otherUser);

    List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, otherUser);
    List<MessageDTO> dtos = messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
}


    // ---------------- WebSocket ---------------- //

   @MessageMapping("/chat.sendMessage")
public void sendMessageWebSocket(
        @Payload MessageDTO messageDTO,
        SimpMessageHeaderAccessor headerAccessor
) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
        System.out.println("Missing or invalid token, cannot send message");
        return;
    }

    token = token.substring(7);

    // Get user email from token, then fetch actual User from DB
    String email;
    try {
        email = jwtUtil.extractUsername(token); // change to your method that extracts username/email
    } catch (Exception e) {
        System.out.println("Invalid token, cannot authenticate user: " + e.getMessage());
        return;
    }

    User sender = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Sender not found with email: " + email));

    if (messageDTO.getReceiverId() == null) {
        System.out.println("Receiver ID is null, cannot send message");
        return;
    }

    Long receiverId = messageDTO.getReceiverId();
    User receiver = userRepository.findById(Objects.requireNonNull(receiverId, "Receiver id is required"))
            .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + messageDTO.getReceiverId()));

    try {
        validateChatAccess(sender, receiver);
    } catch (ResponseStatusException ex) {
        System.out.println("Blocked unauthorized WebSocket message: " + ex.getReason());
        return;
    }

    Message message = Message.builder()
            .sender(sender)
            .receiver(receiver)
            .content(messageDTO.getContent())
            .sentAt(LocalDateTime.now())
            .build();

    Message saved = messageService.saveMessage(message);
    MessageDTO dto = convertToDTO(saved);

    // Broadcast to both users (real-time)
    String receiverEmail = Objects.requireNonNull(receiver.getEmail(), "Receiver email is required");
    String senderEmail = Objects.requireNonNull(sender.getEmail(), "Sender email is required");

    Object payload = Objects.requireNonNull(dto, "Message payload is required");

   System.out.println("Sending WebSocket message to user: " + receiverEmail);
messagingTemplate.convertAndSendToUser(Objects.requireNonNull(receiverEmail.toLowerCase(), "Receiver username is required"), "/queue/messages", payload);

    messagingTemplate.convertAndSendToUser(Objects.requireNonNull(senderEmail.toLowerCase(), "Sender username is required"), "/queue/messages", payload);

    // Create notification for receiver
    notificationService.createNotification(sender, receiver, messageDTO.getContent(), saved.getSentAt());

    System.out.println("Message saved and sent via WebSocket: " + dto);
    System.out.println("WebSocket header user: " + headerAccessor.getUser());
 System.out.println("Sending to user: " + receiverEmail);

}

    @GetMapping("/contacts")
    public ResponseEntity<List<ChatContactDTO>> getChatContacts(Principal principal) {
        User currentUser;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            currentUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }

        if (currentUser.getRole() == Role.CUSTOMER) {
            List<ChatContactDTO> contacts = bookingRepository.findDistinctProvidersByCustomer(currentUser).stream()
                    .map(user -> new ChatContactDTO(user.getId(), user.getName(), user.getRole()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contacts);
        }

        if (currentUser.getRole() == Role.PROVIDER) {
            List<ChatContactDTO> contacts = bookingRepository.findDistinctCustomersByProvider(currentUser).stream()
                    .map(user -> new ChatContactDTO(user.getId(), user.getName(), user.getRole()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contacts);
        }

        return ResponseEntity.ok(List.of());
    }



    // ---------------- Utility Methods ---------------- //

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getReceiver().getId(),
                message.getReceiver().getName(),
                message.getContent(),
                message.getSentAt()
        );
    }

    private void validateChatAccess(User userA, User userB) {
        if (userA == null || userB == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid chat participants");
        }

        if (userA.getId().equals(userB.getId())) {
            return;
        }

        if (userA.getRole() == Role.ADMIN || userB.getRole() == Role.ADMIN) {
            return;
        }

        boolean customerProviderPair =
                (userA.getRole() == Role.CUSTOMER && userB.getRole() == Role.PROVIDER)
                        || (userA.getRole() == Role.PROVIDER && userB.getRole() == Role.CUSTOMER);

        if (!customerProviderPair) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Messaging is allowed only between customer and provider");
        }

        Long customerId = userA.getRole() == Role.CUSTOMER ? userA.getId() : userB.getId();
        Long providerId = userA.getRole() == Role.PROVIDER ? userA.getId() : userB.getId();

        boolean hasSharedBooking = bookingRepository.existsByCustomerIdAndProviderId(customerId, providerId);
        if (!hasSharedBooking) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only chat with users connected through a booking");
        }
    }
}
