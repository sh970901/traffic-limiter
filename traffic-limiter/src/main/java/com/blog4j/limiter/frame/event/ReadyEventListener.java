package com.blog4j.limiter.frame.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnWebApplication
public class ReadyEventListener {

    String banner = """
         
        \s
               _____                       /\\  \\         /\\__\\        ___   \s
              /::\\  \\                     /::\\  \\       /:/ _/_      /\\__\\  \s
             /:/\\:\\  \\                   /:/\\:\\  \\     /:/ /\\  \\    /:/__/  \s
            /:/ /::\\__\\   ___     ___   /:/  \\:\\  \\   /:/ /::\\  \\  /::\\  \\  \s
           /:/_/:/\\:|__| /\\  \\   /\\__\\ /:/__/ \\:\\__\\ /:/__\\/\\:\\__\\ \\/\\:\\  \\ \s
           \\:\\/:/ /:/  / \\:\\  \\ /:/  / \\:\\  \\ /:/  / \\:\\  \\ /:/  /  ~~\\:\\  \\\s
            \\::/_/:/  /   \\:\\  /:/  /   \\:\\  /:/  /   \\:\\  /:/  /      \\:\\__\\
             \\:\\/:/  /     \\:\\/:/  /     \\:\\/:/  /     \\:\\/:/  /       /:/  /
              \\::/  /       \\::/  /       \\::/  /       \\::/  /       /:/  /\s
               \\/__/         \\/__/         \\/__/         \\/__/        \\/__/ \s
    
        """;

    @Autowired
    private ApplicationContext context;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        log.info("{}", banner);
    }

}