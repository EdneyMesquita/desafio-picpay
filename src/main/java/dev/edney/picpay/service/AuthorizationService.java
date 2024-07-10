package dev.edney.picpay.service;

import dev.edney.picpay.client.AuthorizationClient;
import dev.edney.picpay.dto.TransferDto;
import dev.edney.picpay.entity.Transfer;
import dev.edney.picpay.exception.PicPayException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final AuthorizationClient authorizationClient;

    public AuthorizationService(AuthorizationClient authorizationClient) {
        this.authorizationClient = authorizationClient;
    }

    public boolean isAuthorized(TransferDto transferDto) {
        var response = authorizationClient.isAuthorized();

        if(response.getStatusCode().isError()) {
            throw new PicPayException();
        }

        return response.getBody().authorized();
    }
}
