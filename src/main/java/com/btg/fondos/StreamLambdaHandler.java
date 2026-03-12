package com.btg.fondos;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(FondosApplication.class, "aws");
            String basePath = System.getenv("API_BASE_PATH");
            if (basePath != null && !basePath.isBlank()) {
                handler.getContainerConfig().setServiceBasePath(basePath);
                handler.getContainerConfig().setStripBasePath(true);
            }
        } catch (ContainerInitializationException e) {
            throw new IllegalStateException("No se pudo inicializar el contenedor Spring Boot en Lambda", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        handler.proxyStream(input, output, context);
    }
}
