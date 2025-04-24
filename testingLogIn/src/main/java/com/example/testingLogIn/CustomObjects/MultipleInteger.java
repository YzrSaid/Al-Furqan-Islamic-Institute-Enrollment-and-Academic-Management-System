package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MultipleInteger {
    private List<Integer> ids;
}
