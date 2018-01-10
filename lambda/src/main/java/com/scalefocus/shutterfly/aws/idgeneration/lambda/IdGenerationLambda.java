package com.scalefocus.shutterfly.aws.idgeneration.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.scalefocus.shutterfly.aws.idgeneration.services.api.IUniqueIdService;
import com.scalefocus.shutterfly.aws.idgeneration.services.impl.TimeCountingService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Dummy lambda for testing
 */
public class IdGenerationLambda implements RequestHandler<Integer, List<Long>> {

    private final IUniqueIdService idService = new TimeCountingService();

    public IdGenerationLambda() {
    }

    public List<Long> handleRequest(Integer i, Context context) {
        return Stream.generate(idService::getUniqueId).limit(i).collect(Collectors.toList());
    }

}
