package ru.kafkaexample.other;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    private String login;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String gender;
}
