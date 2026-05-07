package com.vasilebreban.trainticketing.service;

import com.vasilebreban.trainticketing.exception.EmailSendingException;
import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.model.Train;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendBookingConfirmation(Booking booking) {
        String to = booking.getCustomer().getEmail();
        String subject = "Train ticket booking confirmation";

        String body = """
                Hello %s,

                Your booking has been confirmed.

                Booking details:
                Booking ID: %d
                Train: %s
                Number of tickets: %d
                Booking time: %s

                Thank you for using our train ticketing application.
                """
                .formatted(
                        booking.getCustomer().getFullName(),
                        booking.getId(),
                        booking.getTrain().getTrainNumber(),
                        booking.getNumberOfTickets(),
                        booking.getBookingTime()
                );

        sendEmail(to, subject, body);
    }

    public void sendDelayNotification(Booking booking, Train train) {
        String to = booking.getCustomer().getEmail();
        String subject = "Train delay notification";

        String body = """
                Hello %s,

                We would like to inform you that your train has encountered a delay.

                Delay details:
                Train: %s
                Delay: %d minutes
                Number of booked tickets: %d

                We apologize for the inconvenience.
                """
                .formatted(
                        booking.getCustomer().getFullName(),
                        train.getTrainNumber(),
                        train.getDelayMinutes(),
                        booking.getNumberOfTickets()
                );

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email sent successfully to {}", to);
        } catch (MailException exception) {
            log.error("Failed to send email to {}", to, exception);
            throw new EmailSendingException("Failed to send email to " + to);
        }
    }
}