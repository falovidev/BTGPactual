package com.btg.fondos.domain.login.model;

import com.btg.fondos.domain.client.model.Client;

public record LoginResult(String token, Client client) {
}
