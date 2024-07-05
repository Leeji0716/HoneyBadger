package com.team.HoneyBadger;

import com.team.HoneyBadger.Enum.OsType;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HoneyBadgerApplication {
    @Getter
    private static OsType osType;

    public static void main(String[] args) {
        osType = OsType.getOsType();
        if (osType != null)
            SpringApplication.run(HoneyBadgerApplication.class, args);
        else
            throw new DataNotFoundException("없는 OS 입니다.");
    }

}
