package com.shopsmart.ecommerceapi.model;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, SecondValidation.class})
public interface ValidationSequenceOrder {}