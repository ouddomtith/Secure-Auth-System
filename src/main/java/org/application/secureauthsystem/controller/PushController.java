package org.application.secureauthsystem.controller;

import lombok.AllArgsConstructor;
import org.application.secureauthsystem.service.PushService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push")
@AllArgsConstructor
public class PushController {
    private final PushService pushService;
}
