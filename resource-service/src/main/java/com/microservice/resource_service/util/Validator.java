package com.microservice.resource_service.util;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Validator {
    public int validateId(String id) {
        int validatedId;

        if (id == null || id.isEmpty()) {
            throw new ValidationException(getInvalidIdExceptionMessage(id));
        }

        try {
            validatedId = Integer.parseInt(id);

            if (validatedId < 1) {
                throw new ValidationException(getInvalidIdExceptionMessage(id));
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(getInvalidIdExceptionMessage(id));
        }

        return validatedId;
    }

    public List<Integer> validateIds(String ids) {
        if (ids.length() > 200) {
            throw new ValidationException(String.format("CSV string is too long: received %d characters, maximum allowed is 200", ids.length()));
        }

        var splitIds = ids.split(",");
        var validatedIds = new ArrayList<Integer>();

        for (String splitId : splitIds) {
            validatedIds.add(validateId(splitId));
        }

        return validatedIds;
    }

    private String getInvalidIdExceptionMessage(String id) {
        return String.format("Invalid value '%s' for ID. Must be a positive integer", id);
    }
}
