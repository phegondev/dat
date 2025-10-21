package com.example.dat;

import com.example.dat.notification.dto.NotificationDTO;
import com.example.dat.notification.service.NotificationService;
import com.example.dat.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class DatApplication {

//	private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(DatApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner(){
//		return args -> {
//
//			NotificationDTO notificationDTO = NotificationDTO.builder()
//					.recipient("phegontech@gmail.com")
//					.subject("Testing email")
//					.message("Hey, this is a test mail")
//					.build();
//
//			notificationService.sendEmail(notificationDTO, new User());
//		};
//	}

}
