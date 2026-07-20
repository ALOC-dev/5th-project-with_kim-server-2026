package com.with_kim.aloc_study.util;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class VectorUtils {
    private VectorUtils(){}

    public static String toVectorLiteral(List<Double> vector){
        return vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",","[","]"));
    }
}
