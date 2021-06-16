package com.shubham.digit_classifier.models;

public interface Classifier {
    String name();
    Classification recognize(final float[] pixels);
}
